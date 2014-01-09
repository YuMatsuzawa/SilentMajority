package matz;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

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
		SimExecLogger.info("SimulationExecutor going to be terminated after all submitted tasks done.");
	}
	/**SimulationExecutor�̃��O�t�@�C�������擾�D
	 * @return
	 */
	public String getSimExecLogFileName() {
		return SimExecLogFileName;
	}
	/**SimulationExecutor�̃��O�t�@�C��������t�x�[�X�Őݒ�D
	 * @param simExecLogFileName
	 */
	public void setSimExecLogFileName() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimExecLogFileName = this.getClass().getName() + df.format(date);
	}
	
	/**Logger�����������A�t�@�C���n���h����ݒ肷��B
	 * ���O�t�@�C���͓��t�x�[�X�Ŗ��O�t�����A�A�y���h����B
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void initSimExecLogger() throws SecurityException, IOException {
		setSimExecLogFileName();
		SimExecLogger = Logger.getLogger(this.getClass().getName());
		FileHandler fh = new FileHandler("logs/" + getSimExecLogFileName() + ".log", true);
		fh.setFormatter(new SimpleFormatter());
		SimExecLogger.addHandler(fh);														//logfile
		SimExecLogger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));		//stdout
	}
	public void logConf(String msg) {
		SimExecLogger.config(msg);
	}
	public void logInfo(String msg) {
		SimExecLogger.info(msg);
	}
	public void logWarn(String msg) {
		SimExecLogger.warning(msg);
	}
	public void logErr(String msg) {
		SimExecLogger.severe(msg);
	}
	/**�f�t�H���g�̃X���b�h��(8)��SimulationExecutor���������D
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
	/**�w�肵���X���b�h����SimulationExecutor���������D
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
		SimulationExecutor SE = new SimulationExecutor();
		for (String arg : args) {
			try {
				int numThreads = Integer.parseInt(arg);
				SE = new SimulationExecutor(numThreads);
			} catch (NumberFormatException e) {
				
			}
		}
		
		SE.logInfo(Thread.currentThread().getId() + " : " + Thread.currentThread().toString()
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
