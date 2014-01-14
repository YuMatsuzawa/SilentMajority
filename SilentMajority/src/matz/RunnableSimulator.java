package matz;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class RunnableSimulator implements Runnable {

	private String InstanceName;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	
	/**�V�~�����[�^�C���X�^���X�̖��O���擾����D
	 * @return
	 */
	public String getInstanceName() {
		return InstanceName;
	}

	/**�V�~�����[�^�C���X�^���X�̖��O���w�肷��D
	 * �^�����閼�O�̌^�����ł������悤�ɁCvalueOf��parse����D
	 * @param instanceName
	 */
	public void setInstanceName(Object instanceName) {
		InstanceName = String.valueOf(instanceName);
	}
	/**�T�C�����g�����擾�D
	 * @return
	 */
	public double getSilentAgentsRatio() {
		return SilentAgentsRatio;
	}

	/**�T�C�����g������́D
	 * @param silentAgentsRatio
	 */
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		SilentAgentsRatio = silentAgentsRatio;
	}

	/**���f���I�����擾�D
	 * @return
	 */
	public double getModelReferenceRatio() {
		return ModelReferenceRatio;
	}

	/**���f���I������́D
	 * @param modelReferenceRatio
	 */
	public void setModelReferenceRatio(double modelReferenceRatio) {
		ModelReferenceRatio = modelReferenceRatio;
	}

	/**Task���Ƃ̃��O�t�@�C�������擾�B
	 * @return taskLogFileName
	 */
	public String getTaskLogFileName() {
		return TaskLogFileName;
	}
	/**SimulationExecutor�̃��O�t�@�C�������X���b�h���x�[�X�Őݒ�D
	 * @param simExecLogFileName
	 */
	public void setTaskLogFileName() {
		TaskLogFileName = TaskLogger.getName() + ".log";
	}
	/**���K�[�����������A�t�@�C���n���h����ݒ肷��B<br />
	 * ���O�t�@�C���̓A�y���h����B
	 * Runnable�^�X�N�̃��O�́i���S�ȏ�ɋy�Ԃ��Ƃ̂���j�^�X�N���Ƃł͂Ȃ��C�i���������v���Z�b�T��*�R�A���Ɏ��܂�j���s�X���b�h���ƂɎ擾�������D
	 * ���̂��߂ɁCinitTaskLogger��Thread.currendThread�i�j���g�p����̂ŁCRunnable�I�u�W�F�N�g��run()���\�b�h���Ŏ��s����Ȃ���΂Ȃ�Ȃ��D
	 */
	private void initTaskLogger() {
		TaskLogger = Logger.getLogger(this.getClass().getName()+"."+Thread.currentThread().getName()); //pseudo-constructor
		setTaskLogFileName();		
		//for (Handler handler : TaskLogger.getHandlers()) TaskLogger.removeHandler(handler); //remove default handlers
		TaskLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		try {
			FileHandler fh = new FileHandler(logDir + "/" + getTaskLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			TaskLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.WARNING);
			ch.setFormatter(new ShortLogFormatter());
			TaskLogger.addHandler(ch);
			logStackTrace(e);
		}
	}
	/**���K�[�̃t�@�C���n���h�����N���[�Y����D<br />
	 * ���̏�����lck�t�@�C����|�����邽�߂ɕK�v�D
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : TaskLogger.getHandlers()) {
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
		TaskLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**���̓f�[�^���i�[���Ă���f�B���N�g���p�X���擾�B
	 * @return dataDir
	 */
	public String getDataDir() {
		return DataDir;
	}

	/**���̓f�[�^���i�[���Ă���f�B���N�g���p�X���w��B�f�t�H���g�l��<current>/data
	 * @param dataDir �Z�b�g���� dataDir
	 */
	public void setDataDir(String dataDir) {
		DataDir = dataDir;
	}

	/**�����_���ȃT�C�����g���ƃ��f���I���ŃV�~�����[�V�������������D
	 * �K���ȉǌ^�Ŗ��O��^���邱�ƁB
	 * @param instanceName - ���O
	 */
	public RunnableSimulator(Object instanceName) {
		try {
			setInstanceName(instanceName);
			setSilentAgentsRatio(Math.random());
			setModelReferenceRatio(Math.random());
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLogger���R���X�g���N�^�ŏ��������Ȃ��̂Ńf�t�H���g�o�͂��g�p����D
		}
	}
	
	/**�w�肵���T�C�����g���ƃ��f���I���ŃV�~�����[�V�������������D
	 * �K���ȉǌ^�Ŗ��O��^���邱�ƁB
	 * @param instanceName - ���O
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public RunnableSimulator(Object instanceName, double silentAgentsRatio, double modelReferenceRatio) {
		try {
			setInstanceName(instanceName);
			setSilentAgentsRatio(silentAgentsRatio);
			setModelReferenceRatio(modelReferenceRatio);
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLogger���R���X�g���N�^�ŏ��������Ȃ��̂Ńf�t�H���g�o�͂��g�p����D
		}
	}
	/**����^�X�N�����̃e�X�g�p�̃��\�b�h�D<br />
	 * �K���ȃe�L�X�g�t�@�C������͂Ƃ��C�P��̏o�����𐔂��グ��D<br />
	 * HashMap��String�Dsplit���g���C�Ō��ArrayList��Collection�Dsort�ŕ��ёւ���D<br />
	 * @param input
	 * @throws IOException 
	 */
	public void WordCount(File input) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				for (String word : line.split("\\s")) {
					if (hm.containsKey(word)) {
						hm.put(word, hm.get(word) + 1);
					} else {
						hm.put(word, 1);
					}
				}
			}	
		} catch (IOException e) {
			logStackTrace(e);
		}
		br.close();
		
		ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		Collections.sort(entries, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				Map.Entry<String, Integer> e1 =(Map.Entry<String, Integer>) o1;
				Map.Entry<String, Integer> e2 =(Map.Entry<String, Integer>) o2;
				return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());
			}
		});
		
		File outDir = new File("results/wc");
		if (!outDir.isDirectory()) outDir.mkdirs();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(outDir,"wc_"
				+ Thread.currentThread().toString().replaceAll("\\s", "") + ".txt"), true));
		try {
			osw.write(entries.toString());
		} catch (IOException e) {
			logStackTrace(e);
		}
		osw.close();		
	}
	
	@Override
	public void run() {
		this.initTaskLogger();
			//thread���Ƃ̃��O���擾���邽�߂ɁCrun()���Ń��K�[������������D
			//����Runnable�^�X�N���̂��̂��R���X�g���N�g����̂�Executor�̃��C��thread�Ȃ̂ŁC���̎��_�Ń��K�[�����������Ă��܂��Ɗe�X��thread�����擾�ł��Ȃ�
		
		this.TaskLogger.info("Start: "+this.getInstanceName());
		try {
			//main procedure calling bracket
			WordCount(new File(this.getDataDir(),"zarathustra.txt"));
			//TODO deploy actual simulation method
			//();
			this.TaskLogger.info("Done.");
		} catch (Exception e) {
			logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}

}
