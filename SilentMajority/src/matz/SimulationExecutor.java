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
	/**SimulationExecutor�̃��O�t�@�C���������K�[�̖��O����ݒ�D
	 * @param simExecLogFileName
	 */
	public void setSimExecLogFileName() {
		SimExecLogFileName = SimExecLogger.getName() + ".log";
	}
	
	/**���K�[�����������A�t�@�C���n���h���E�R���\�[���n���h����ݒ肷��B
	 * ���O�t�@�C���̓A�y���h����B
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
	/**���K�[�̃t�@�C���n���h�����N���[�Y����D
	 * ���̏�����lck�t�@�C����|�����邽�߂ɕK�v�D
	 * 
	 */
	public void closeLogFileHandler() {
		for (Handler handler : SimExecLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
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
