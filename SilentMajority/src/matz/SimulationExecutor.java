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
	
	/**ExecutorService�̃X���b�h�����擾����D
	 * @return
	 */
	public int getNumThreads() {
		return NumThreads;
	}
	/**ExecutorService�̃X���b�h�����w�肷��D
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
	/**SimulationExecutor�̃��O�t�@�C�������擾�D
	 * @return
	 */
	public String getSimExecLogFileName() {
		return SimExecLogFileName;
	}
	/**SimulationExecutor�̃��O�t�@�C�������w��D
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
