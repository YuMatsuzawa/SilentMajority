package matz.agentsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import matz.basics.network.StaticNetwork;

public class SimulationTaskLT extends SimulationTask {
	
	private double controlVar;
	private double totalPosRatio;
	private double initSilentRatio;
	private int simType;
	private static int NUM_OPINION = 2, POS_OPINION = 0, NEG_OPINION = 1;
	static final int TYPE_RANKED = 0, TYPE_BIASED = 1, TYPE_RELIEF = 2;
	static final String[] SIM_TYPE_NAME = {"BiasedOpinionByRank", "BiasedVocalization", "RelievingAgents"};
	
	@Override
	public void run() {
		this.initTaskLogger();
		this.TaskLogger.info("Start: "+this.getInstanceName());
		
		try { //main procedure calling bracket
			
			//エージェント初期化
			this.initInfoAgentsArray(this.getnAgents(), this.refNetwork);
			if (this.refNetwork == null) {
				//静的ネットワークを使わないなら、シミュレーション個別のネットワークを生成する．
				CNNNetworkBuilder ntwk = new CNNNetworkBuilder();
				this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			}
			
			//意見分布を初期化
			if (simType == TYPE_RANKED) this.rankedInitOpinions();
			else this.simpleInitOpinions();
			
			//一定割合をヴォーカルにして情報伝播の起点にする（muzzleAgentsに相当）
			if (simType == TYPE_BIASED) this.biasedPropagation();
			else this.simpleInitPropagation();
			
			File outDir = new File("results/"+this.getTimeStamp(),
					"n="+String.format("%d",this.getnAgents()) +
					"pos="+String.format("%.2f", this.totalPosRatio) +
					"sil="+String.format("%.2f", this.initSilentRatio) +
					"ctrl="+String.format("%.2f", this.controlVar)
					);
			if (!outDir.isDirectory()) outDir.mkdirs();
			
			//ネットワークのチェック
			NetworkVisualizer nv = new NetworkVisualizer(this.infoAgentsArray);
			//初期状態の確認
			nv.generateGraph(outDir, this.getTimeStamp() + "." + this.getInstanceName()+".initial.png");
			
			//情報伝播試行
			int maxStep = 100;
			BufferedWriter rbw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(outDir, this.getTimeStamp() + "." + this.getInstanceName()+".csv"))));
			Integer[][] vocalRecords = new Integer[maxStep][NUM_OPINION], 
					totalRecords = new Integer[maxStep][NUM_OPINION], 
					silentRecords = new Integer[maxStep][NUM_OPINION];
			for (int i = 0; i < maxStep; i++) for(int j = 0; j < NUM_OPINION; j++) {
				vocalRecords[i][j] = 0;
				silentRecords[i][j] = 0;
				totalRecords[i][j] = 0;
			}
			rbw.write("timestep,pos,neg");
			rbw.newLine();
			for (int step = 0; step < maxStep; step++) {
				for (InfoAgent agent : this.infoAgentsArray) {
					//Integer opinion = agent.getOpinion(); //記録するのはヴォーカルの中での比率なので，forceGetしない
					Integer opinion = agent.forceGetOpinion(); //debugのために全部記録したいのでforceGetする
					if (opinion == null) continue;
					else if (opinion == POS_OPINION) {
						//vocalRecords[step][POS_OPINION]++;
						totalRecords[step][POS_OPINION]++;
						if (agent.isSilent()) silentRecords[step][POS_OPINION]++;
						else vocalRecords[step][POS_OPINION]++;
					}
					else if (opinion == NEG_OPINION) {
						//vocalRecords[step][NEG_OPINION]++;
						totalRecords[step][NEG_OPINION]++;
						if (agent.isSilent()) silentRecords[step][NEG_OPINION]++;
						else vocalRecords[step][NEG_OPINION]++;
					}
				}
				rbw.write(String.valueOf(step));
				for (int op = 0; op < NUM_OPINION; op++) rbw.write(","+vocalRecords[step][op]);
				//debugのために全部記録
				rbw.write(",");
				for (int op = 0; op < NUM_OPINION; op++) rbw.write(","+totalRecords[step][op]);
				rbw.write(",");
				for (int op = 0; op < NUM_OPINION; op++) rbw.write(","+silentRecords[step][op]);
				//debugここまで
				rbw.newLine();
				
				/*
				 * LTモデル相互作用を実行
				 * 
				 */
				for (InfoAgent agent : this.infoAgentsArray) {
					if (this.simType == TYPE_RELIEF) agent.linearThresholdMuzzlingWithRelief(infoAgentsArray, this.controlVar);
					else agent.linearThreasholdMuzzling(infoAgentsArray);
					
				}
				
				for (InfoAgent agent : this.infoAgentsArray) agent.applyMuzzling(); //中間状態を本適用
			}

			//最終状態の確認．
			nv.generateGraph(outDir, this.getTimeStamp() + "." + this.getInstanceName()+".final.png");
			
			rbw.close();
			
			this.endGate.countDown(); //カウントダウン
			this.TaskLogger.info("Done: " + this.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			this.logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}

	/**
	 * 意見分布の初期化．<br>
	 * 全体の意見割合はtotalPosRatioでPOSの割合として定める．真の意見は全員POSかNEGを持つ．<br>
	 * サイレントであれば外からはnullであるようにみえることになるので，内部的には2値，外部的には3値．<br>
	 * ハブエージェント（高次数エージェント）のPOSの割合を制御する．initPosHubRatioがX%なら，<br>
	 * 次数上位X%を強制POSにする．残るエージェントは全体の割合をtotalPosRatioに合わせるように割り振る．<br>
	 * totalPosRatio=initHubPosRatioなら，初期POSを持っているのはハブエージェントのみで，残るエージェントはNEGになる．
	 * 
	 */
	public void rankedInitOpinions() {
		//POSで始まるハブの境界値となる次数を探す
		int hubCutoff = this.refNetwork.getDegreeFreq().firstKey() - 1;
		double nPosCandidate = this.getnAgents();
		double nPosInitiator = this.getnAgents() * this.controlVar;
		double nPos = 0.0;
		for (Entry<Integer,Integer> entry : this.refNetwork.getDegreeFreq().entrySet()) {
			hubCutoff = entry.getKey();
			double tmpCandidate = nPosCandidate - (double)entry.getValue();
			if (tmpCandidate > nPosInitiator) {
				nPosCandidate = tmpCandidate;
			} else {
				nPos = tmpCandidate;
				break;
			}
			
		/*	double tmpCandidate = nPosCandidate - (double)entry.getValue();
			if (tmpCandidate >= nPosInitiator) {
				hubCutoff = entry.getKey();
				nPosCandidate = tmpCandidate;
			} else {
				nPos = tmpCandidate;
				break;
			}*/
		}
		double pPosLeaf = ((double)this.getnAgents() * this.totalPosRatio - nPos) / ((double)this.getnAgents() - nPos);
		//次数が境界値より上ならPOS，それ以外なら全体のPOS割合を満たすだけPOS，残りはNEG
		for (InfoAgent agent : this.infoAgentsArray) {
			Integer opinion = null;
			if (agent.getDegree() > hubCutoff) {
				opinion = POS_OPINION;
			} else {
				double roll = this.localRNG.nextDouble();
				if (roll < pPosLeaf) opinion = POS_OPINION;
				else opinion = NEG_OPINION;
			}
			agent.setOpinion(opinion);
		}
	}
	
	/**
	 * 単に一定割合にPOS，それ以外にNEGをもたせる意見初期化．
	 */
	public void simpleInitOpinions() {
		for (InfoAgent agent : this.infoAgentsArray) {
			double roll = this.localRNG.nextDouble();
			if (roll < this.totalPosRatio) agent.setOpinion(POS_OPINION);
			else agent.setOpinion(NEG_OPINION);
		}
	}

	/**
	 * 全体のinitSilentRatioだけSilentにし，残りはVocalにする．<br>
	 * 一般にinitSilentRatioは高い値であり，当初Vocalである人は少ない<br>
	 */
	public void simpleInitPropagation() {
		for (InfoAgent agent : this.infoAgentsArray){
			double roll = this.localRNG.nextDouble();
			if (roll < this.initSilentRatio) agent.muzzle();
			else agent.unmuzzle();
		}
	}
	
	/**
	 * 初期にヴォーカルである割合1.0 - initSilentRatioのうち，<br>
	 * 何割がPosであるかをコントロールする．全体のPos/Neg比は不変である．<br>
	 * 全体的な意見の趨勢と逆の発信から始まった場合どうなるかを調査する．
	 */
	public void biasedPropagation() {
		double pVocalBase = 1.0 - this.initSilentRatio;
		for (InfoAgent agent : this.infoAgentsArray) {
			if (agent.forceGetOpinion() == POS_OPINION) {
				double pVocalPos = pVocalBase * this.controlVar / (this.totalPosRatio);
				double roll = this.localRNG.nextDouble();
				if (roll < pVocalPos) agent.unmuzzle();
				else agent.muzzle();
			} else {
				double pVocalNeg = pVocalBase * (1.0 - this.controlVar) / (1.0 - this.totalPosRatio);
				double roll = this.localRNG.nextDouble();
				if (roll < pVocalNeg) agent.unmuzzle();
				else agent.muzzle();
			}
		}
	}

	/**
	 * 派生シミュレーションの基本コンストラクタ．
	 * @param simName
	 * @param instanceName
	 * @param nAgents
	 * @param controlVar
	 * @param initSilentRatio
	 * @param endGate
	 */
	public SimulationTaskLT(String simName, String instanceName, int nAgents,
			double totalPosRatio, double controlVar, double initSilentRatio,
			StaticNetwork ntwk, CountDownLatch endGate) {
		super(simName, instanceName, nAgents, ntwk, endGate);
		this.totalPosRatio = totalPosRatio;
		this.controlVar = controlVar;
		this.initSilentRatio = initSilentRatio;
		for(int simTypeIndex = 0; simTypeIndex < SIM_TYPE_NAME.length; simTypeIndex++) {
			if (simName.startsWith(SIM_TYPE_NAME[simTypeIndex])) this.simType = simTypeIndex;
		} 
	}
}
