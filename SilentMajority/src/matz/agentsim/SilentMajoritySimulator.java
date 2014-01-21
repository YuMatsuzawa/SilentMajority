package matz.agentsim;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class SilentMajoritySimulator implements Runnable {

	private String InstanceName;
	private int nAgents;
	private final static int NAGENTS_DEFAUT = 100;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	private InfoAgent[] infoAgentsArray;
	private final int NAME_BASED = 0;
	private final int INDEX_BASED = 1;
	private Random localRNG = new Random();
	private final int NULL_PATTERN = 0;
	private final int MIX_PATTERN = 1;
	
	@Override
	public void run() {
		this.initTaskLogger();
			//threadごとのログを取得するために，run()内でロガーを初期化する．
			//このRunnableタスクそのものをコンストラクトするのはExecutorのメインthreadなので，
			//その時点でロガーを初期化してしまうと各々のthread名が取得できない(mainのthread情報が返ってくる)
			//run()内でロガーを初期化すれば、run()内のプロシージャを実行するthread(＝プールされているthreadのうちの一つ)の情報を取得できる
		
		this.TaskLogger.info("Start: "+this.getInstanceName());
		try {
			//main procedure calling bracket
			//this.WordCount(new File(this.getDataDir(),"zarathustra.txt"));
			
			//TODO deploy actual simulation method
			//エージェント集合の配列を初期化する．
			this.initInfoAgentsArray(this.getnAgents());
			//ネットワークを生成する．
			CNNModel ntwk = new CNNModel();
			this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			File outDir = new File("results/cnn");
			if (!outDir.isDirectory()) outDir.mkdirs();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (InfoAgent iAgent : this.infoAgentsArray) {
				bw.write(iAgent.getAgentIndex() + "\t:\t");
				for (Object neighbor : iAgent.getIndirectedList()) {
					bw.write((Integer)neighbor + ",");
				}
				bw.write("\n");
			}
			bw.close();
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
			this.infoAgentsArray[index] = new InfoAgent(index, this.initOpinion(this.MIX_PATTERN)); 
		}
	}

	/**意見の初期値を与える．patternによって挙動が変わる．<br />
	 * ・NULL_PATTERN（=0）の場合：全てnullにする．nullは意見未決定状態．<br />
	 * ・MIX_PATTERN（=1)の場合：0,1,2のいずれかにする．
	 * @param pattern
	 * @return
	 */
	private Integer initOpinion(int pattern) {
		Integer opinion = null;
		if (pattern == this.NULL_PATTERN ) {
			opinion = null;
		} else if (pattern == this.MIX_PATTERN) {
			opinion = this.localRNG.nextInt(3);
		}
		return opinion;
	}

	/**デフォルトエージェント数（1000）とランダムなサイレント率・モデル選択比でシミュレーションを初期化するコンストラクタ．
	 * 適当な可読型で名前を与えること。
	 * @param instanceName - 名前
	 */
	public SilentMajoritySimulator(Object instanceName) {
		this(instanceName, NAGENTS_DEFAUT, Math.random(),Math.random());
	}
	
	/**指定したサイレント率とモデル選択比でシミュレーションを初期化するコンストラクタ．
	 * 適当な可読型で名前を与えること。
	 * @param instanceName - 名前
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public SilentMajoritySimulator(Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio) {
		try {
			this.setInstanceName(instanceName);
			this.setnAgents(nAgents);
			this.setSilentAgentsRatio(silentAgentsRatio);
			this.setModelReferenceRatio(modelReferenceRatio);
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLoggerをコンストラクタで初期化しないのでデフォルト出力を使用する．
		}
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
	 * Runnableタスクのログは（数百以上に及ぶことのある）タスクごとではなく，（たかだかプロセッサ数*コア数に収まる）実行スレッドごとに取得したい．
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
