package matz.agentsim;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class SilentMajoritySimulator implements Runnable {

	private String InstanceName;
	private int nAgents;
	private static final int NAGENTS_DEFAUT = 100;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	private InfoAgent[] infoAgentsArray;
	private Random localRNG = new Random();
	private final int NULL_PATTERN = 0;
	private final int MIX_PATTERN = 1;
	private final int SPARSE_PATTERN = 2;
	private int MAX_ITER = 20;
	public final int SUM_INDEX = 0, UPDATE_INDEX = 1,
			TOTAL_INDEX = 0, SILENT_INDEX = 1, VOCAL_INDEX = 2,
			NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2, NULL_IDEX = 3;
	private String timeStamp;
	@SuppressWarnings("unused")
	private static boolean DIRECTED = true;
	@SuppressWarnings("unused")
	private static boolean UNDIRECTED = false;
	private static final double CONVERGENCE_CONDITION = 0.05;
	
	@Override
	public void run() {
		this.initTaskLogger();
			//threadごとのログを取得するために，run()内でロガーを初期化する．
			//このRunnableタスクそのものをコンストラクトするのはExecutorのメインthreadなので，
			//その時点でロガーを初期化してしまうと各々のthread名が取得できない(mainのthread情報が返ってくる)
			//run()内でロガーを初期化すれば、run()内のプロシージャを実行するthread(＝プールされているthreadのうちの一つ)の情報を取得できる
		
		this.TaskLogger.info("Start: "+this.getInstanceName());
		try { // main procedure calling bracket
			
			//this.WordCount(new File(this.getDataDir(),"zarathustra.txt")); //test procedure
			
			//エージェント集合の配列を初期化する．
			this.initInfoAgentsArray(this.getnAgents());
			//ネットワークを生成する．
			CNNModel ntwk = new CNNModel();
			this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			//ネットワーク確定後、次数に依存する確率分布に従い、エージェントをサイレントにする。
			this.muzzleAgents();
			
			//ネットワークのチェック
			File outDir = new File("results/" + this.getTimeStamp() + "/"+ "n="+this.getnAgents()+"s="+this.getSilentAgentsRatio()+"m="+this.getModelReferenceRatio());
			if (!outDir.isDirectory()) outDir.mkdirs();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (InfoAgent iAgent : this.infoAgentsArray) {
				bw.write(iAgent.getAgentIndex() + "(" + iAgent.getnFollowed() + ")\t:\t");
				for (Object neighbor : iAgent.getUndirectedList()) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			bw.close();
			
			//情報伝播を試行する
			int cStep = 0, nUpdated = 0, iStable = 0, nAgents = this.getnAgents();
			BufferedWriter rbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, this.getInstanceName()+".csv"))));
			Integer[][][][] records = new Integer[MAX_ITER][2][3][4];
			//TODO 上と合わせて、jfreechartで画像自動出力＋何かサイレント率に依存しそうな統計指標を用意し、同条件での複数回試行を前提とした解析を準備する
			while(iStable < 10 && cStep < MAX_ITER) {
				//収束条件は意見変化のあったエージェントが全体の5%以下の状態が10ステップ継続するか、あるいは20ステップに到達するか。
				
				// 意見比率の追跡。全体、サイレント、ヴォーカルの順。
				Integer[][] sumRecord = {{0,0,0,0},{0,0,0,0},{0,0,0,0}};
				for (InfoAgent agent : this.infoAgentsArray) {
					Integer opinion = agent.forceGetOpinion();
					if(opinion == null) opinion = 3;
					sumRecord[TOTAL_INDEX][opinion]++;
					if(agent.isSilent()) sumRecord[SILENT_INDEX][opinion]++;
					else sumRecord[VOCAL_INDEX][opinion]++;
				}
				//意見比率の書き込み。
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 4; j++) rbw.write(sumRecord[i][j] + ",");
					rbw.write(" ,");					
				}/*
				rbw.write(sumRecord[0][0]+","+sumRecord[0][1]+","+sumRecord[0][2]+","+sumRecord[0][3]+", ,"
						+sumRecord[1][0]+","+sumRecord[1][1]+","+sumRecord[1][2]+","+sumRecord[1][3]+", ,"
						+sumRecord[2][0]+","+sumRecord[2][1]+","+sumRecord[2][2]+","+sumRecord[2][3]);
				rbw.write(", ,");*/
				
				nUpdated = 0;
				//updateの追跡。
				Integer[][] updateRecord = {{0,0,0,0},{0,0,0,0},{0,0,0,0}};
				double roll = this.localRNG.nextDouble();
				//全エージェントについて、モデルに基づく相互作用を実行
				for (InfoAgent agent : this.infoAgentsArray) {
					boolean isUpdated = (roll < this.getModelReferenceRatio())? //モデル選択比を閾値として確率選択している。
							agent.IndependentCascade(infoAgentsArray)
							: agent.LinearThreashold(infoAgentsArray);
					if (isUpdated) {
						nUpdated++;
						Integer updatedOpinion = agent.getTmpOpinion();
						if(updatedOpinion == null) updatedOpinion = 3;
						updateRecord[TOTAL_INDEX][updatedOpinion]++;
						if(agent.isSilent()) updateRecord[SILENT_INDEX][updatedOpinion]++;
						else updateRecord[VOCAL_INDEX][updatedOpinion]++;
					}
				}
				//update記録の書き込み
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 4; j++) rbw.write(updateRecord[i][j] + ",");
					rbw.write(" ,");					
				}
				rbw.newLine();

				records[cStep][SUM_INDEX] = sumRecord;
				records[cStep][UPDATE_INDEX] = updateRecord;
				
				for (InfoAgent agent : this.infoAgentsArray) agent.applyOpinion(); //中間データを本適用する。
				
				if (((double)nUpdated / (double)nAgents) < CONVERGENCE_CONDITION){
					iStable++;
				} else {
					iStable = 0;
				}

				cStep++;
			}
			rbw.close();
			
			this.TaskLogger.info("Done.");
		} catch (Exception e) {
			e.printStackTrace();
			this.logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}

	/**情報エージェント配列を初期化する．この処理はrun()内で呼ばれるべきである（子スレッド内で処理されるべきである）．
	 * @param nAgents
	 */
	private void initInfoAgentsArray(int nAgents) {
		this.infoAgentsArray= new InfoAgent[nAgents];
		for (int index = 0; index < nAgents; index++) {
			this.infoAgentsArray[index] = new InfoAgent(index, this.initOpinion(this.SPARSE_PATTERN));
			if (this.localRNG.nextDouble() < this.getSilentAgentsRatio()) this.infoAgentsArray[index].muzzle();
				//初期状態では単純にランダムで指定割合のエージェントをサイレントにしておく。
		}
		
	}
	/**次数に依存する確率分布に従い、エージェントをサイレントにする。<br />
	 * 無向グラフならgetDegree()で次数を取れる。getnFollowed()でも取ってくる数値は同じだが。<br />
	 * 有向グラフなら多くの参照を集めるエージェントがハブと考えられるので、getnFollowed()を使う。
	 */
	@SuppressWarnings("unused")
	private void muzzleAgents() {
		int sumDegree = 0;
		for (InfoAgent agent : this.infoAgentsArray) {
			sumDegree += agent.getnFollowed();
		}
		
		for (InfoAgent agent : this.infoAgentsArray) {
			int degree = agent.getnFollowed();
			double roll = this.localRNG.nextDouble();
			if (roll <= this.silentPDF(degree)) agent.muzzle(); else agent.unmuzzle();
		}
	}

	/**次数に依存するサイレント性の確率分布関数。<br />
	 * 引数は離散値の次数なのでProbability Distribution Function(PDF)。Probability Mass Function(PMF)とも言える。<br />
	 * @param degree 次数
	 * @return
	 */
	private double silentPDF(int degree) {
		// TODO 実装
		double probability = 0.0;
		if (degree < 10) {
			probability = this.getSilentAgentsRatio();
		}
		return probability;
	}

	/**意見の初期値を与える．patternによって挙動が変わる．<br />
	 * ・NULL_PATTERN（=0）の場合：全てnullにする．nullは意見未決定状態．<br />
	 * ・MIX_PATTERN（=1)の場合：0,1,2のいずれかにする．<br />
	 * ・SPARSE_PATTERN(=2)の場合：90%はNULL，10%はランダムで0,1,2のいずれかにする。
	 * @param pattern
	 * @return
	 */
	private Integer initOpinion(int pattern) {
		Integer opinion = null;
		if (pattern == this.NULL_PATTERN) {
			opinion = null;
		} else if (pattern == this.MIX_PATTERN) {
			opinion = this.localRNG.nextInt(3);
		} else if (pattern == this.SPARSE_PATTERN) {
			opinion = null;
			double roll = this.localRNG.nextDouble();
			if (roll < 0.1) {
				opinion = this.localRNG.nextInt(3);
			}
		}
		return opinion;
	}

	/**名前以外何も与えず、ランダムなパラメータで初期化するコンストラクタ。
	 * 
	 * @param instanceName - 名前
	 */
	public SilentMajoritySimulator(Object instanceName) {
		this("recent", instanceName, NAGENTS_DEFAUT, Math.random(),Math.random());
	}
	
	/**タイムスタンプを与えずに初期化するコンストラクタ。ディレクトリ結果は"recent"以下に出力される。
	 * 
	 * @param instanceName
	 * @param nAgents
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public SilentMajoritySimulator(Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio) {
		this("recent", instanceName, nAgents, silentAgentsRatio, modelReferenceRatio);
	}
	
	/**基本コンストラクタ。
	 * 
	 * @param timeStamp - シミュレーション全体の識別のために与えるExectorServiceが起動した時刻。
	 * @param instanceName - 適当な可読な名前。
	 * @param nAgents - エージェント数
	 * @param silentAgentsRatio - サイレント率
	 * @param modelReferenceRatio - モデル選択比
	 */
	public SilentMajoritySimulator(String timeStamp, Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio) {
		try {
			this.setTimeStamp(timeStamp);
			this.setInstanceName(instanceName);
			this.setnAgents(nAgents);
			this.setSilentAgentsRatio(silentAgentsRatio);
			this.setModelReferenceRatio(modelReferenceRatio);
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLoggerをコンストラクタで初期化しないのでデフォルト出力を使用する．
		}
	}






	public String getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**シミュレータインスタンスの名前を取得する．
	 * @return
	 */
	public String getInstanceName() {
		return this.InstanceName;
	}

	/**シミュレータインスタンスの名前を指定する．
	 * 与えられる名前の型が何でもいいように，valueOfでparseする．
	 * @param instanceName
	 */
	public void setInstanceName(Object instanceName) {
		this.InstanceName = String.valueOf(instanceName);
	}
	/**エージェント数を取得．
	 * @return nAgents
	 */
	public int getnAgents() {
		return nAgents;
	}

	/**エージェント数を指定．
	 * @param nAgents セットする nAgents
	 */
	public void setnAgents(int nAgents) {
		this.nAgents = nAgents;
	}

	/**サイレント率を取得．
	 * @return
	 */
	public double getSilentAgentsRatio() {
		return this.SilentAgentsRatio;
	}

	/**サイレント率を入力．
	 * @param silentAgentsRatio
	 */
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		this.SilentAgentsRatio = silentAgentsRatio;
	}

	/**モデル選択比を取得．
	 * @return
	 */
	public double getModelReferenceRatio() {
		return this.ModelReferenceRatio;
	}

	/**モデル選択比を入力．
	 * @param modelReferenceRatio
	 */
	public void setModelReferenceRatio(double modelReferenceRatio) {
		this.ModelReferenceRatio = modelReferenceRatio;
	}

	/**Taskごとのログファイル名を取得。
	 * @return taskLogFileName
	 */
	public String getTaskLogFileName() {
		return this.TaskLogFileName;
	}
	/**SimulationExecutorのログファイル名をスレッド情報ベースで設定．
	 * @param simExecLogFileName
	 */
	public void setTaskLogFileName() {
		this.TaskLogFileName = this.TaskLogger.getName() + ".log";
	}
	/**ロガーを初期化し、ファイルハンドラを設定する。<br />
	 * ログファイルはアペンドする。
	 * Runnableタスクのログは（数百以上に及ぶことのある）タスクごとではなく，（たかだかプロセッサ数*コア数に収まる）実行スレッドごとに取得したい．<br />
	 * そのために，initTaskLoggerはThread.currendThread（）を使用するので，Runnableオブジェクトのrun()メソッド内で実行されなければならない．
	 */
	private void initTaskLogger() {
		this.TaskLogger = Logger.getLogger(this.getClass().getName()+"."+Thread.currentThread().getName()); //pseudo-constructor
		this.setTaskLogFileName();		
		//for (Handler handler : TaskLogger.getHandlers()) TaskLogger.removeHandler(handler); //remove default handlers
		this.TaskLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		try {
			FileHandler fh = new FileHandler(logDir + "/" + this.getTaskLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			this.TaskLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.WARNING);
			ch.setFormatter(new ShortLogFormatter());
			this.TaskLogger.addHandler(ch);
			this.logStackTrace(e);
		}
	}
	/**ロガーのファイルハンドラをクローズする．<br />
	 * この処理はlckファイルを掃除するために必要．
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : this.TaskLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
	/**例外をロガーに流すメソッド。<br />
	 * SEVEREレベル（Fatalレベル）で出力される。
	 * 
	 * @param thrown
	 */
	public void logStackTrace(Throwable thrown) {
		this.TaskLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**入力データを格納してあるディレクトリパスを取得。
	 * @return dataDir
	 */
	public String getDataDir() {
		return this.DataDir;
	}

	/**入力データを格納してあるディレクトリパスを指定。デフォルト値は<current>/data
	 * @param dataDir セットする dataDir
	 */
	public void setDataDir(String dataDir) {
		this.DataDir = dataDir;
	}
	
	
	
	/**並列タスク処理のテスト用のメソッド．<br />
	 * 適当なテキストファイルを入力とし，単語の出現数を数え上げる．<br />
	 * HashMapとString．splitを使い，最後にArrayListとCollection．sortで並び替える．<br />
	 * @param input
	 * @throws IOException 
	 */
	public void WordCount(File input) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				for (String word : line.split("\\s")) {
					if (hm.containsKey(word)) {
						hm.put(word, hm.get(word) + 1);
					} else {
						hm.put(word, 1);
					}
				}
			}	
		} catch (IOException e) {
			this.logStackTrace(e);
		}
		br.close();
		
		ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		Collections.sort(entries, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				Map.Entry<String, Integer> e1 =(Map.Entry<String, Integer>) o1;
				Map.Entry<String, Integer> e2 =(Map.Entry<String, Integer>) o2;
				return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());
			}
		});
		
		File outDir = new File("results/wc");
		if (!outDir.isDirectory()) outDir.mkdirs();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(outDir,"wc_"
				+ Thread.currentThread().toString().replaceAll("\\s", "") + ".txt"), true));
		try {
			osw.write(entries.toString());
		} catch (IOException e) {
			this.logStackTrace(e);
		}
		osw.close();		
	}

}
