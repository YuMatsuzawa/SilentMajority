package matz.basics;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.*;

/**
 * ExecutorServiceを使ってマルチスレッドでシミュレーション等を走らせるためのクラス。<br>
 * シミュレーション等、個別のプロジェクトごとにmainメソッドをホストするエントリポイントクラスを作り、<br>
 * そのmainの中でインスタンス化して使用する。<br>
 * 個別の処理内容はRunnable（Callable）を実装したタスククラスを定義し、それをインスタンス化して投入する。<br>
 * 以下スニペット：<br>
 * <br>
 * <code>
 * public class MySimulation {<br>
 * <br>
 * public static main (String[] args) {<br>
 * 	<strong>MatzExecutor _E = new MatzExecutor()</strong>;<br>
 *  RunnableTask rt = new RunnableTask(); //Runnableを実装したタスク<br>
 *  <br>
 *  _E.execute(rt);<br>
 *  <br>
 *  _E.safeShutdown();<br>
 * }<br>
 * }</code>
 * 
 * @author Romancer
 *
 */
public class MatzExecutor {

	private int NumThreads;
	/**ExecutorServiceが持つThread数のデフォルト値。i5等で作業されることも多いので4としている。<br>
	 * デフォルト値なのでStatic。
	 */
	private final static int NumThreadsDefault = 4;
	/**MatzExecutorの基幹となるスレッドプールを保持するExecutorService.
	 * @see java.util.concurrent.ExecutorService
	 * 
	 */
	private ExecutorService SimExecServ;
	/**MatzExecutorのロガー。
	 * @see java.util.logging.Logger
	 */
	public Logger SimExecLogger = null;
	private String SimExecLogFileName;
	
	/**
	 * ExecutorServiceのスレッド数を取得する．
	 * @return
	 */
	public int getNumThreads() {
		return this.NumThreads;
	}
	/**ExecutorServiceのスレッド数を指定する．
	 * @param numThreads - int
	 */
	public void setNumThreads(int numThreads) {
		this.NumThreads = numThreads;
	}
	/**ExecutorServiceをNumThreadsフィールドに指定したスレッド数で初期化．
	 * ThreadFactoryを用いてThreadに名前付けをする．
	 */
	private void initSimExecServ() {
		ThreadFactory tf = new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				String thPrefix = "SimPool";
				Thread th = new Thread(r);
				th.setName(thPrefix + th.getName());
				return th;
			}
		};
		this.SimExecServ = Executors.newFixedThreadPool(this.getNumThreads(),tf);
	}
	/**
	 * RunnableタスクをExecutorServiceに投入する．
	 * @param runnable
	 */
	public void execute(Runnable command) {
		this.SimExecServ.execute(command);
	}
	/**
	 * RunnableタスクあるいはCallableオブジェクトを投入し，非同期計算の結果を取得するオブジェクトFutureを得る．
	 * @param command
	 * @return
	 */
	public Future<?> submit(Runnable command) {
		Future<?> future = this.SimExecServ.submit(command);
		return future;
	}
	/**実行中のタスクが全て終了したあとにExecutorServiceを終了する．
	 * 
	 */
	public void safeShutdown() {
		this.SimExecServ.shutdown();
		this.SimExecLogger.info(this.getClass().getName() + " going to be terminated after all submitted tasks done.");
	}
	/**ログファイル名を取得．
	 * @return
	 */
	public String getSimExecLogFileName() {
		return this.SimExecLogFileName;
	}
	/**ログファイル名をロガーの名前から設定．
	 * @param simExecLogFileName
	 */
	public void setSimExecLogFileName() {
		this.SimExecLogFileName = this.SimExecLogger.getName() + ".log";
	}
	
	/**
	 * ロガーを初期化し、ファイルハンドラ・コンソールハンドラを設定する。<br>
	 * ログファイルはアペンドする。
	 */
	private void initSimExecLogger() {
		
		this.SimExecLogger = Logger.getLogger(this.getClass().getName()); //pseudo-constructor
		this.setSimExecLogFileName();
		this.SimExecLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		ConsoleHandler ch = new ConsoleHandler();
		//StreamHandler ch = new StreamHandler(System.out, new ShortLogFormatter());			//stdout
		ch.setFormatter(new ShortLogFormatter());
		ch.setLevel(Level.INFO);
		this.SimExecLogger.addHandler(ch);														//stderr

		try {
			FileHandler fh = new FileHandler(logDir + "/" + this.getSimExecLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			this.SimExecLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			this.logStackTrace(e);
		}
	}
	/**ロガーのファイルハンドラをクローズする．<br>
	 * この処理はlckファイルを掃除するために必要．
	 * 
	 */
	public void closeLogFileHandler() {
		for (Handler handler : this.SimExecLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
	/**例外をロガーに流すメソッド。<br>
	 * SEVEREレベル（Fatalレベル）で出力される。
	 * 
	 * @param thrown
	 */
	public void logStackTrace(Throwable thrown) {
		this.SimExecLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**デフォルトのスレッド数(8)で初期化するコンストラクタ．
	 * 
	 */
	public MatzExecutor() {
		this(NumThreadsDefault);
	}
	/**指定したスレッド数で初期化するコンストラクタ．
	 * @param numThreads - int
	 */
	public MatzExecutor(int numThreads) {
		try {
			this.setNumThreads(numThreads);
			this.initSimExecServ();
			this.initSimExecLogger();
		} catch (Exception e) {
			this.logStackTrace(e);
		}
	}

}
