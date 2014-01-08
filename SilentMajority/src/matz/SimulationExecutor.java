package matz;

import java.lang.reflect.Field; //Leave this imported regardless of being used or unused
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SimulationExecutor {

	private int NumThreads;
	private int NumThreadsDefault = 8;
	private ExecutorService SimExecServ;
	
	public int getNumThreads() {
		return NumThreads;
	}
	/**ExecutorServiceのThreadPoolの数を指定する．
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
