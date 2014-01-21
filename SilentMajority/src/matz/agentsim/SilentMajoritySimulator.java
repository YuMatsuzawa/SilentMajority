package matz.agentsim;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class SilentMajoritySimulator implements Runnable {

	private String InstanceName;
	private int nAgents;
	private final static int NAGENTS_DEFAUT = 100;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	private InfoAgent[] infoAgentsArray;
	private final int NAME_BASED = 0;
	private final int INDEX_BASED = 1;
	private Random localRNG = new Random();
	private final int NULL_PATTERN = 0;
	private final int MIX_PATTERN = 1;
	
	@Override
	public void run() {
		this.initTaskLogger();
			//thread���Ƃ̃��O���擾���邽�߂ɁCrun()���Ń��K�[������������D
			//����Runnable�^�X�N���̂��̂��R���X�g���N�g����̂�Executor�̃��C��thread�Ȃ̂ŁC
			//���̎��_�Ń��K�[�����������Ă��܂��Ɗe�X��thread�����擾�ł��Ȃ�(main��thread��񂪕Ԃ��Ă���)
			//run()���Ń��K�[������������΁Arun()���̃v���V�[�W�������s����thread(���v�[������Ă���thread�̂����̈��)�̏����擾�ł���
		
		this.TaskLogger.info("Start: "+this.getInstanceName());
		try {
			//main procedure calling bracket
			//this.WordCount(new File(this.getDataDir(),"zarathustra.txt"));
			
			//TODO deploy actual simulation method
			//�G�[�W�F���g�W���̔z�������������D
			this.initInfoAgentsArray(this.getnAgents());
			//�l�b�g���[�N�𐶐�����D
			CNNModel ntwk = new CNNModel();
			this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			File outDir = new File("results/cnn");
			if (!outDir.isDirectory()) outDir.mkdirs();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (InfoAgent iAgent : this.infoAgentsArray) {
				bw.write(iAgent.getAgentIndex() + "\t:\t");
				for (Object neighbor : iAgent.getIndirectedList()) {
					bw.write((Integer)neighbor + ",");
				}
				bw.write("\n");
			}
			bw.close();
			this.TaskLogger.info("Done.");
		} catch (Exception e) {
			e.printStackTrace();
			this.logStackTrace(e);
		} finally {
			this.closeLogFileHandler();
		}
	}
	
	

	


	/**���G�[�W�F���g�z�������������D���̏�����run()���ŌĂ΂��ׂ��ł���i�q�X���b�h���ŏ��������ׂ��ł���j�D
	 * @param nAgents
	 */
	private void initInfoAgentsArray(int nAgents) {
		this.infoAgentsArray= new InfoAgent[nAgents];
		for (int index = 0; index < nAgents; index++) {
			this.infoAgentsArray[index] = new InfoAgent(index, this.initOpinion(this.MIX_PATTERN)); 
		}
	}

	/**�ӌ��̏����l��^����Dpattern�ɂ���ċ������ς��D<br />
	 * �ENULL_PATTERN�i=0�j�̏ꍇ�F�S��null�ɂ���Dnull�͈ӌ��������ԁD<br />
	 * �EMIX_PATTERN�i=1)�̏ꍇ�F0,1,2�̂����ꂩ�ɂ���D
	 * @param pattern
	 * @return
	 */
	private Integer initOpinion(int pattern) {
		Integer opinion = null;
		if (pattern == this.NULL_PATTERN ) {
			opinion = null;
		} else if (pattern == this.MIX_PATTERN) {
			opinion = this.localRNG.nextInt(3);
		}
		return opinion;
	}

	/**�f�t�H���g�G�[�W�F���g���i1000�j�ƃ����_���ȃT�C�����g���E���f���I���ŃV�~�����[�V����������������R���X�g���N�^�D
	 * �K���ȉǌ^�Ŗ��O��^���邱�ƁB
	 * @param instanceName - ���O
	 */
	public SilentMajoritySimulator(Object instanceName) {
		this(instanceName, NAGENTS_DEFAUT, Math.random(),Math.random());
	}
	
	/**�w�肵���T�C�����g���ƃ��f���I���ŃV�~�����[�V����������������R���X�g���N�^�D
	 * �K���ȉǌ^�Ŗ��O��^���邱�ƁB
	 * @param instanceName - ���O
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public SilentMajoritySimulator(Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio) {
		try {
			this.setInstanceName(instanceName);
			this.setnAgents(nAgents);
			this.setSilentAgentsRatio(silentAgentsRatio);
			this.setModelReferenceRatio(modelReferenceRatio);
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLogger���R���X�g���N�^�ŏ��������Ȃ��̂Ńf�t�H���g�o�͂��g�p����D
		}
	}
	
	/**�V�~�����[�^�C���X�^���X�̖��O���擾����D
	 * @return
	 */
	public String getInstanceName() {
		return this.InstanceName;
	}

	/**�V�~�����[�^�C���X�^���X�̖��O���w�肷��D
	 * �^�����閼�O�̌^�����ł������悤�ɁCvalueOf��parse����D
	 * @param instanceName
	 */
	public void setInstanceName(Object instanceName) {
		this.InstanceName = String.valueOf(instanceName);
	}
	/**�G�[�W�F���g�����擾�D
	 * @return nAgents
	 */
	public int getnAgents() {
		return nAgents;
	}

	/**�G�[�W�F���g�����w��D
	 * @param nAgents �Z�b�g���� nAgents
	 */
	public void setnAgents(int nAgents) {
		this.nAgents = nAgents;
	}

	/**�T�C�����g�����擾�D
	 * @return
	 */
	public double getSilentAgentsRatio() {
		return this.SilentAgentsRatio;
	}

	/**�T�C�����g������́D
	 * @param silentAgentsRatio
	 */
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		this.SilentAgentsRatio = silentAgentsRatio;
	}

	/**���f���I�����擾�D
	 * @return
	 */
	public double getModelReferenceRatio() {
		return this.ModelReferenceRatio;
	}

	/**���f���I������́D
	 * @param modelReferenceRatio
	 */
	public void setModelReferenceRatio(double modelReferenceRatio) {
		this.ModelReferenceRatio = modelReferenceRatio;
	}

	/**Task���Ƃ̃��O�t�@�C�������擾�B
	 * @return taskLogFileName
	 */
	public String getTaskLogFileName() {
		return this.TaskLogFileName;
	}
	/**SimulationExecutor�̃��O�t�@�C�������X���b�h���x�[�X�Őݒ�D
	 * @param simExecLogFileName
	 */
	public void setTaskLogFileName() {
		this.TaskLogFileName = this.TaskLogger.getName() + ".log";
	}
	/**���K�[�����������A�t�@�C���n���h����ݒ肷��B<br />
	 * ���O�t�@�C���̓A�y���h����B
	 * Runnable�^�X�N�̃��O�́i���S�ȏ�ɋy�Ԃ��Ƃ̂���j�^�X�N���Ƃł͂Ȃ��C�i���������v���Z�b�T��*�R�A���Ɏ��܂�j���s�X���b�h���ƂɎ擾�������D
	 * ���̂��߂ɁCinitTaskLogger��Thread.currendThread�i�j���g�p����̂ŁCRunnable�I�u�W�F�N�g��run()���\�b�h���Ŏ��s����Ȃ���΂Ȃ�Ȃ��D
	 */
	private void initTaskLogger() {
		this.TaskLogger = Logger.getLogger(this.getClass().getName()+"."+Thread.currentThread().getName()); //pseudo-constructor
		this.setTaskLogFileName();		
		//for (Handler handler : TaskLogger.getHandlers()) TaskLogger.removeHandler(handler); //remove default handlers
		this.TaskLogger.setUseParentHandlers(false);
		 //this is essential for disabling 'root logger' to display your logs in default settings and formats.
		 //with this setting, LogRecord from this class won't be passed up to root logger.
		
		File logDir = new File("logs");
		if (!logDir.isDirectory()) logDir.mkdirs();
		
		try {
			FileHandler fh = new FileHandler(logDir + "/" + this.getTaskLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			this.TaskLogger.addHandler(fh);														//logfile
		} catch (Exception e) {
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.WARNING);
			ch.setFormatter(new ShortLogFormatter());
			this.TaskLogger.addHandler(ch);
			this.logStackTrace(e);
		}
	}
	/**���K�[�̃t�@�C���n���h�����N���[�Y����D<br />
	 * ���̏�����lck�t�@�C����|�����邽�߂ɕK�v�D
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : this.TaskLogger.getHandlers()) {
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
		this.TaskLogger.log(Level.SEVERE, thrown.getLocalizedMessage(), thrown);
	}
	/**���̓f�[�^���i�[���Ă���f�B���N�g���p�X���擾�B
	 * @return dataDir
	 */
	public String getDataDir() {
		return this.DataDir;
	}

	/**���̓f�[�^���i�[���Ă���f�B���N�g���p�X���w��B�f�t�H���g�l��<current>/data
	 * @param dataDir �Z�b�g���� dataDir
	 */
	public void setDataDir(String dataDir) {
		this.DataDir = dataDir;
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
			this.logStackTrace(e);
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
			this.logStackTrace(e);
		}
		osw.close();		
	}

}
