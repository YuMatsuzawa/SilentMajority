package matz;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public final class SimulationExecutor {

	private int NumThreads;
	private int NumThreadsDefault = 8;
	private ExecutorService SimExecServ;
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
	 * 
	 */
	public void initSimExecServ() {
		SimExecServ = Executors.newFixedThreadPool(getNumThreads());
	}
	/**RunnableタスクをSimulationExecutorのExecutorServiceに投入する．
	 * @param runnable
	 */
	public void execute(Runnable command) {
		SimExecServ.execute(command);
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
	
	/**ロガーを初期化し、ファイルハンドラ・コンソールハンドラを設定する。
	 * ログファイルはアペンドする。
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void initSimExecLogger() throws SecurityException, IOException {
		
		SimExecLogger = Logger.getLogger(this.getClass().getName()); //pseudo-constructor
		setSimExecLogFileName();
		SimExecLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		FileHandler fh = new FileHandler(logDir + "/" + getSimExecLogFileName(), true);
		fh.setFormatter(new ShortLogFormatter());
		SimExecLogger.addHandler(fh);														//logfile
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new ShortLogFormatter());
		SimExecLogger.addHandler(ch);														//stderr
	}
	/**ロガーのファイルハンドラをクローズする．
	 * この処理はlckファイルを掃除するために必要．
	 * 
	 */
	public void closeLogFileHandler() {
		for (Handler handler : SimExecLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
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
			e.printStackTrace();
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
			e.printStackTrace();
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
		
		for (int i = 0; i < 8; i++) {
			SE.execute(new RunnableSimulator("instance" + i));
		}
		
		SE.safeShutdown();
		SE.closeLogFileHandler();
	}

}
