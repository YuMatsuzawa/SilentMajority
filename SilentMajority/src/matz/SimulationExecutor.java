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
	/**SimulationExecutor�̊�ƂȂ�X���b�h�v�[����ێ�����ExecutorService.
	 * @see java.util.concurrent.ExecutorService
	 * 
	 */
	private ExecutorService SimExecServ;
	/**SimulationExecutor��Logger�B
	 * @see java.util.logging.Logger
	 */
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
	 * ThreadFactory��p����Thread�ɖ��O�t��������D
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
	/**Runnable�^�X�N��SimulationExecutor��ExecutorService�ɓ�������D
	 * @param runnable
	 */
	public void execute(Runnable command) {
		SimExecServ.execute(command);
	}
	/**Runnable�^�X�N���邢��Callable�I�u�W�F�N�g�𓊓����C�񓯊��v�Z�̌��ʂ��擾����I�u�W�F�N�gFuture�𓾂�D
	 * @param command
	 * @return
	 */
	public Future<?> submit(Runnable command) {
		Future<?> future = SimExecServ.submit(command);
		return future;
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
	
	/**���K�[�����������A�t�@�C���n���h���E�R���\�[���n���h����ݒ肷��B<br />
	 * ���O�t�@�C���̓A�y���h����B
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
	/**���K�[�̃t�@�C���n���h�����N���[�Y����D<br />
	 * ���̏�����lck�t�@�C����|�����邽�߂ɕK�v�D
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : SimExecLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
	/**��O�����K�[�ɗ������\�b�h�B<br />
	 * SEVERE���x���iFatal���x���j�ŏo�͂����B
	 * 
	 * @param thrown
	 */
	public void logStackTrace(Throwable thrown) {
		SimExecLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
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
			logStackTrace(e);
			
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
