package matz;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class RunnableSimulator implements Runnable {

	private String InstanceName;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	
	/**シミュレータインスタンスの名前を取得する．
	 * @return
	 */
	public String getInstanceName() {
		return InstanceName;
	}

	/**シミュレータインスタンスの名前を指定する．
	 * 与えられる名前の型が何でもいいように，valueOfでparseする．
	 * @param instanceName
	 */
	public void setInstanceName(Object instanceName) {
		InstanceName = String.valueOf(instanceName);
	}
	/**サイレント率を取得．
	 * @return
	 */
	public double getSilentAgentsRatio() {
		return SilentAgentsRatio;
	}

	/**サイレント率を入力．
	 * @param silentAgentsRatio
	 */
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		SilentAgentsRatio = silentAgentsRatio;
	}

	/**モデル選択比を取得．
	 * @return
	 */
	public double getModelReferenceRatio() {
		return ModelReferenceRatio;
	}

	/**モデル選択比を入力．
	 * @param modelReferenceRatio
	 */
	public void setModelReferenceRatio(double modelReferenceRatio) {
		ModelReferenceRatio = modelReferenceRatio;
	}

	/**Taskごとのログファイル名を取得。
	 * @return taskLogFileName
	 */
	public String getTaskLogFileName() {
		return TaskLogFileName;
	}
	/**SimulationExecutorのログファイル名をスレッド情報ベースで設定．
	 * @param simExecLogFileName
	 */
	public void setTaskLogFileName() {
		TaskLogFileName = TaskLogger.getName() + ".log";
	}
	/**ロガーを初期化し、ファイルハンドラを設定する。<br />
	 * ログファイルはアペンドする。
	 */
	public void initTaskLogger() {
		TaskLogger = Logger.getLogger(this.getClass().getName()+"."+this.getInstanceName()); //pseudo-constructor
		setTaskLogFileName();		
		//for (Handler handler : TaskLogger.getHandlers()) TaskLogger.removeHandler(handler); //remove default handlers
		TaskLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		try {
			FileHandler fh = new FileHandler(logDir + "/" + getTaskLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			TaskLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.WARNING);
			ch.setFormatter(new ShortLogFormatter());
			TaskLogger.addHandler(ch);
			logStackTrace(e);
		}
	}
	/**ロガーのファイルハンドラをクローズする．<br />
	 * この処理はlckファイルを掃除するために必要．
	 * 
	 */
	public void closeLogFileHandler() {
		for (Handler handler : TaskLogger.getHandlers()) {
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
		TaskLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**入力データを格納してあるディレクトリパスを取得。
	 * @return dataDir
	 */
	public String getDataDir() {
		return DataDir;
	}

	/**入力データを格納してあるディレクトリパスを指定。デフォルト値は<current>/data
	 * @param dataDir セットする dataDir
	 */
	public void setDataDir(String dataDir) {
		DataDir = dataDir;
	}

	/**ランダムなサイレント率とモデル選択比でシミュレーションを初期化．
	 * 適当な可読型で名前を与えること。
	 * @param instanceName - 名前
	 */
	public RunnableSimulator(Object instanceName) {
		try {
			setInstanceName(instanceName);
			setSilentAgentsRatio(Math.random());
			setModelReferenceRatio(Math.random());
			initTaskLogger();
		} catch (Exception e) {
			logStackTrace(e);
		}
	}
	
	/**指定したサイレント率とモデル選択比でシミュレーションを初期化．
	 * 適当な可読型で名前を与えること。
	 * @param instanceName - 名前
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public RunnableSimulator(Object instanceName, double silentAgentsRatio, double modelReferenceRatio) {
		try {
			setInstanceName(instanceName);
			setSilentAgentsRatio(silentAgentsRatio);
			setModelReferenceRatio(modelReferenceRatio);
			initTaskLogger();
		} catch (Exception e) {
			logStackTrace(e);
		}
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
			logStackTrace(e);
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
			logStackTrace(e);
		}
		osw.close();		
	}
	
	@Override
	public void run() {
		
		this.TaskLogger.info("Start.");
		try {
			//procedure
			WordCount(new File(this.getDataDir(),"zarathustra.txt"));
			this.TaskLogger.info("Done.");
		} catch (Exception e) {
			logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}

}
