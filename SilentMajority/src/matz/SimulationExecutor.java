package matz;

import java.lang.reflect.Field; //Leave this imported regardless of being used or unused
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
		System.out.println("Simulation Executor going to be terminated after all submitted tasks done.");
	}
	/**SimulationExecutorのログファイル名を取得．
	 * @return
	 */
	public String getSimExecLogFileName() {
		return SimExecLogFileName;
	}
	/**SimulationExecutorのログファイル名を指定．
	 * @param simExecLogFileName
	 */
	public void setSimExecLogFileName(String simExecLogFileName) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		SimExecLogFileName = this.getClass().getName() + df.format(date) + ".log";
	}
	public void initSimExecLogger() {
		SimExecLogger = Logger.getLogger(this.getClass().getName());
		SimExecLogger.addHandler(new FileHandler(getSimExecLogFileName()));
	}
	/**デフォルトのスレッド数(8)でSimulationExecutorを初期化．
	 * 
	 */
	public SimulationExecutor() {
		setNumThreads(NumThreadsDefault);
		initSimExecServ();
	}
	/**指定したスレッド数でSimulationExecutorを初期化．
	 * @param numThreads - int
	 */
	public SimulationExecutor(int numThreads) {
		setNumThreads(numThreads);
		initSimExecServ();
	}
	
	public static final void main(String[] args) {
		SimulationExecutor SE = new SimulationExecutor();
		for (String arg : args) {
			try {
				int numThreads = Integer.parseInt(arg);
				SE = new SimulationExecutor(numThreads);
			} catch (NumberFormatException e) {
				
			}
		}
		
		System.out.println(Thread.currentThread().getId() + " : " + Thread.currentThread().toString()
				+ "\tStarting Simulation Executor. NumThreads = " + SE.getNumThreads());
		
		/*for (Field field : SE.SimExecServ.getClass().getDeclaredFields()) {
			try {
				System.out.println(field.getName() + " = " + field.get(SE).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		for (int i = 0; i < 8; i++) {
			SE.execute(new RunnableSimulator());
		}
		
		SE.safeShutdown();
	}

}
