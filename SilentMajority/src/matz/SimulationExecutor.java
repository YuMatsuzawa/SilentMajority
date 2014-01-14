package matz;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.*;

public final class SimulationExecutor {

	private int NumThreads;
	private int NumThreadsDefault = 8;
	/**SimulationExecutorの基幹となるスレッドプールを保持するExecutorService.
	 * @see java.util.concurrent.ExecutorService
	 * 
	 */
	private ExecutorService SimExecServ;
	/**SimulationExecutorのLogger。
	 * @see java.util.logging.Logger
	 */
	private Logger SimExecLogger = null;
	private String SimExecLogFileName;
	
	/**ExecutorServiceのスレッド数を取得する．
	 * @return
	 */
	public int getNumThreads() {
		return NumThreads;
	}
	/**ExecutorServiceのスレッド数を指定する．
	 * @param numThreads - int
	 */
	public void setNumThreads(int numThreads) {
		NumThreads = numThreads;
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
		SimExecServ = Executors.newFixedThreadPool(getNumThreads(),tf);
	}
	/**RunnableタスクをSimulationExecutorのExecutorServiceに投入する．
	 * @param runnable
	 */
	public void execute(Runnable command) {
		SimExecServ.execute(command);
	}
	/**RunnableタスクあるいはCallableオブジェクトを投入し，非同期計算の結果を取得するオブジェクトFutureを得る．
	 * @param command
	 * @return
	 */
	public Future<?> submit(Runnable command) {
		Future<?> future = SimExecServ.submit(command);
		return future;
	}
	/**実行中のタスクが全て終了したあとにExecutorServiceを終了する．
	 * 
	 */
	public void safeShutdown() {
		SimExecServ.shutdown();
		SimExecLogger.info("SimulationExecutor going to be terminated after all submitted tasks done.");
	}
	/**SimulationExecutorのログファイル名を取得．
	 * @return
	 */
	public String getSimExecLogFileName() {
		return SimExecLogFileName;
	}
	/**SimulationExecutorのログファイル名をロガーの名前から設定．
	 * @param simExecLogFileName
	 */
	public void setSimExecLogFileName() {
		SimExecLogFileName = SimExecLogger.getName() + ".log";
	}
	
	/**ロガーを初期化し、ファイルハンドラ・コンソールハンドラを設定する。<br />
	 * ログファイルはアペンドする。
	 */
	private void initSimExecLogger() {
		
		SimExecLogger = Logger.getLogger(this.getClass().getName()); //pseudo-constructor
		setSimExecLogFileName();
		SimExecLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new ShortLogFormatter());
		ch.setLevel(Level.INFO);
		SimExecLogger.addHandler(ch);														//stderr

		try {
			FileHandler fh = new FileHandler(logDir + "/" + getSimExecLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			SimExecLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			logStackTrace(e);
		}
	}
	/**ロガーのファイルハンドラをクローズする．<br />
	 * この処理はlckファイルを掃除するために必要．
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : SimExecLogger.getHandlers()) {
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
		SimExecLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**デフォルトのスレッド数(8)でSimulationExecutorを初期化．
	 * 
	 */
	public SimulationExecutor() {
		try {
			setNumThreads(NumThreadsDefault);
			initSimExecServ();
			initSimExecLogger();
		} catch (Exception e) {
			logStackTrace(e);
			
		}
	}
	/**指定したスレッド数でSimulationExecutorを初期化．
	 * @param numThreads - int
	 */
	public SimulationExecutor(int numThreads) {
		try {
			setNumThreads(numThreads);
			initSimExecServ();
			initSimExecLogger();
		} catch (Exception e) {
			logStackTrace(e);
		}
	}
	
	public static final void main(String[] args) {
		SimulationExecutor SE = null;
		if (args.length > 0) {
			for (String arg : args) {
				try {
					int numThreads = Integer.parseInt(arg);
					SE = new SimulationExecutor(numThreads);
				} catch (NumberFormatException e) {
					SE = new SimulationExecutor();
				}
			}
		} else {
			SE = new SimulationExecutor();
		}
		
		SE.SimExecLogger.info("Starting Simulation Executor. NumThreads = " + SE.getNumThreads());
		
		int resol = 100;
		//Future<?>[] futures = new Future<?>[resol];
		for (int i = 0; i < resol; i++) {
			RunnableSimulator rn = new RunnableSimulator("instance" + i);
			//futures[i] = SE.submit(rn);
			SE.execute(rn);
			SE.SimExecLogger.info("Submitted: " + rn.getInstanceName());
		}
		
		SE.safeShutdown();
		SE.closeLogFileHandler();
	}

}
