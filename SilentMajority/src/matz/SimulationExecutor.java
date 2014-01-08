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
	/**ExecutorService��ThreadPool�̐����w�肷��D
	 * @param numThreads - int
	 */
	public void setNumThreads(int numThreads) {
		NumThreads = numThreads;
	}
	/**ExecutorService��NumThreads�t�B�[���h�Ɏw�肵���X���b�h���ŏ������D
	 * 
	 */
	public void initSimExecServ() {
		SimExecServ = Executors.newFixedThreadPool(getNumThreads());
	}
	/**Runnable�^�X�N��SimulationExecutor��ExecutorService�ɓ�������D
	 * @param runnable
	 */
	public void execute(Runnable command) {
		SimExecServ.execute(command);
	}
	/**���s���̃^�X�N���S�ďI���������Ƃ�ExecutorService���I������D
	 * 
	 */
	public void safeShutdown() {
		SimExecServ.shutdown();
		System.out.println("Simulation Executor going to be terminated after all submitted tasks done.");
	}
	/**�f�t�H���g�̃X���b�h��(8)��SimulationExecutor���������D
	 * 
	 */
	public SimulationExecutor() {
		setNumThreads(NumThreadsDefault);
		initSimExecServ();
	}
	/**�w�肵���X���b�h����SimulationExecutor���������D
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
