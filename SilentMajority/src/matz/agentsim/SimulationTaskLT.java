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
			
			//�G�[�W�F���g������
			this.initInfoAgentsArray(this.getnAgents(), this.refNetwork);
			if (this.refNetwork == null) {
				//�ÓI�l�b�g���[�N���g��Ȃ��Ȃ�A�V�~�����[�V�����ʂ̃l�b�g���[�N�𐶐�����D
				CNNNetworkBuilder ntwk = new CNNNetworkBuilder();
				this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			}
			
			//�ӌ����z��������
			if (simType == TYPE_RANKED) this.rankedInitOpinions();
			else this.simpleInitOpinions();
			
			//��芄�������H�[�J���ɂ��ď��`�d�̋N�_�ɂ���imuzzleAgents�ɑ����j
			if (simType == TYPE_BIASED) this.biasedPropagation();
			else this.simpleInitPropagation();
			
			File outDir = new File("results/"+this.getTimeStamp(),
					"n="+String.format("%d",this.getnAgents()) +
					"pos="+String.format("%.2f", this.totalPosRatio) +
					"sil="+String.format("%.2f", this.initSilentRatio) +
					"ctrl="+String.format("%.2f", this.controlVar)
					);
			if (!outDir.isDirectory()) outDir.mkdirs();
			
			//�l�b�g���[�N�̃`�F�b�N
			NetworkVisualizer nv = new NetworkVisualizer(this.infoAgentsArray);
			//������Ԃ̊m�F
			nv.generateGraph(outDir, this.getTimeStamp() + "." + this.getInstanceName()+".initial.png");
			
			//���`�d���s
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
					//Integer opinion = agent.getOpinion(); //�L�^����̂̓��H�[�J���̒��ł̔䗦�Ȃ̂ŁCforceGet���Ȃ�
					Integer opinion = agent.forceGetOpinion(); //debug�̂��߂ɑS���L�^�������̂�forceGet����
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
				//debug�̂��߂ɑS���L�^
				rbw.write(",");
				for (int op = 0; op < NUM_OPINION; op++) rbw.write(","+totalRecords[step][op]);
				rbw.write(",");
				for (int op = 0; op < NUM_OPINION; op++) rbw.write(","+silentRecords[step][op]);
				//debug�����܂�
				rbw.newLine();
				
				/*
				 * LT���f�����ݍ�p�����s
				 * 
				 */
				for (InfoAgent agent : this.infoAgentsArray) {
					if (this.simType == TYPE_RELIEF) agent.linearThresholdMuzzlingWithRelief(infoAgentsArray, this.controlVar);
					else agent.linearThreasholdMuzzling(infoAgentsArray);
					
				}
				
				for (InfoAgent agent : this.infoAgentsArray) agent.applyMuzzling(); //���ԏ�Ԃ�{�K�p
			}

			//�ŏI��Ԃ̊m�F�D
			nv.generateGraph(outDir, this.getTimeStamp() + "." + this.getInstanceName()+".final.png");
			
			rbw.close();
			
			this.endGate.countDown(); //�J�E���g�_�E��
			this.TaskLogger.info("Done: " + this.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			this.logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}

	/**
	 * �ӌ����z�̏������D<br>
	 * �S�̂̈ӌ�������totalPosRatio��POS�̊����Ƃ��Ē�߂�D�^�̈ӌ��͑S��POS��NEG�����D<br>
	 * �T�C�����g�ł���ΊO�����null�ł���悤�ɂ݂��邱�ƂɂȂ�̂ŁC�����I�ɂ�2�l�C�O���I�ɂ�3�l�D<br>
	 * �n�u�G�[�W�F���g�i�������G�[�W�F���g�j��POS�̊����𐧌䂷��DinitPosHubRatio��X%�Ȃ�C<br>
	 * �������X%������POS�ɂ���D�c��G�[�W�F���g�͑S�̂̊�����totalPosRatio�ɍ��킹��悤�Ɋ���U��D<br>
	 * totalPosRatio=initHubPosRatio�Ȃ�C����POS�������Ă���̂̓n�u�G�[�W�F���g�݂̂ŁC�c��G�[�W�F���g��NEG�ɂȂ�D
	 * 
	 */
	public void rankedInitOpinions() {
		//POS�Ŏn�܂�n�u�̋��E�l�ƂȂ鎟����T��
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
		//���������E�l����Ȃ�POS�C����ȊO�Ȃ�S�̂�POS�����𖞂�������POS�C�c���NEG
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
	 * �P�Ɉ�芄����POS�C����ȊO��NEG����������ӌ��������D
	 */
	public void simpleInitOpinions() {
		for (InfoAgent agent : this.infoAgentsArray) {
			double roll = this.localRNG.nextDouble();
			if (roll < this.totalPosRatio) agent.setOpinion(POS_OPINION);
			else agent.setOpinion(NEG_OPINION);
		}
	}

	/**
	 * �S�̂�initSilentRatio����Silent�ɂ��C�c���Vocal�ɂ���D<br>
	 * ��ʂ�initSilentRatio�͍����l�ł���C����Vocal�ł���l�͏��Ȃ�<br>
	 */
	public void simpleInitPropagation() {
		for (InfoAgent agent : this.infoAgentsArray){
			double roll = this.localRNG.nextDouble();
			if (roll < this.initSilentRatio) agent.muzzle();
			else agent.unmuzzle();
		}
	}
	
	/**
	 * �����Ƀ��H�[�J���ł��銄��1.0 - initSilentRatio�̂����C<br>
	 * ������Pos�ł��邩���R���g���[������D�S�̂�Pos/Neg��͕s�ςł���D<br>
	 * �S�̓I�Ȉӌ��̐����Ƌt�̔��M����n�܂����ꍇ�ǂ��Ȃ邩�𒲍�����D
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
	 * �h���V�~�����[�V�����̊�{�R���X�g���N�^�D
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
