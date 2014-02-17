package matz.agentsim;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.*;

import matz.basics.ShortLogFormatter;
import matz.basics.StaticNetwork;

public class SimulationTask implements Runnable {

	private String InstanceName;
	private int nAgents;
	private static final int NAGENTS_DEFAUT = 100;
	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	private String DataDir = "data";
	private InfoAgent[] infoAgentsArray;
	private Random localRNG = new Random();
	private final int NULL_PATTERN = 0;
	private final int MIX_PATTERN = 1;
	private final int SPARSE_PATTERN = 2;
	private int MAX_ITER = 30;
	public final int SUM_INDEX = 0, UPDATE_INDEX = 1,
			TOTAL_INDEX = 0, SILENT_INDEX = 1, VOCAL_INDEX = 2,
			NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2, NULL_INDEX = 3;
	private String timeStamp;
	private StaticNetwork refNetwork = null;
	private CountDownLatch endGate = null;
	@SuppressWarnings("unused")
	private static boolean DIRECTED = true;
	@SuppressWarnings("unused")
	private static boolean UNDIRECTED = false;
	private static final double CONVERGENCE_CONDITION = 0.05;
	
	@Override
	public void run() {
		this.initTaskLogger();
			//thread���Ƃ̃��O���擾���邽�߂ɁCrun()���Ń��K�[������������D
			//����Runnable�^�X�N���̂��̂��R���X�g���N�g����̂�Executor�̃��C��thread�Ȃ̂ŁC
			//���̎��_�Ń��K�[�����������Ă��܂��Ɗe�X��thread�����擾�ł��Ȃ�(main��thread��񂪕Ԃ��Ă���)
			//run()���Ń��K�[������������΁Arun()���̃v���V�[�W�������s����thread(���v�[������Ă���thread�̂����̈��)�̏����擾�ł���
		
		this.TaskLogger.info("Start: "+this.getInstanceName());
		try { // main procedure calling bracket
			
			//this.WordCount(new File(this.getDataDir(),"zarathustra.txt")); //test procedure
			
			//�G�[�W�F���g�W���̔z�������������D
			//refNetwork�t�B�[���h�́A�ÓI�l�b�g���[�N���^�����Ă���Ȃ炻�̃C���X�^���X���A�^�����Ă��Ȃ��Ȃ�null�������Ă���B
			this.initInfoAgentsArray(this.getnAgents(), this.refNetwork);
			if (this.refNetwork == null) {
				//�ÓI�l�b�g���[�N���g��Ȃ��Ȃ�A�V�~�����[�V�����ʂ̃l�b�g���[�N�𐶐�����D
				CNNNetworkBuilder ntwk = new CNNNetworkBuilder();
				this.infoAgentsArray = ntwk.build(this.infoAgentsArray);
			}
			
			//
			
			//�l�b�g���[�N�m���A�����Ɉˑ�����m�����z�ɏ]���A�G�[�W�F���g���T�C�����g�ɂ���B
			this.muzzleAgents();
			
			File outDir = new File("results/" + this.getTimeStamp() + "/" + 
					"n=" + String.format("%d", this.getnAgents()) +
					"s=" + String.format("%.1f", this.getSilentAgentsRatio()) +
					"m=" + String.format("%.1f", this.getModelReferenceRatio()));
			if (!outDir.isDirectory()) outDir.mkdirs();
			
			//�l�b�g���[�N�̃`�F�b�N
			//this.dumpNetworkList(outDir);
			
			//���`�d�����s����
			int cStep = 0, nUpdated = 0, iStable = 0, nAgents = this.getnAgents();
			BufferedWriter rbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, this.getInstanceName()+".csv"))));
			ArrayList<Integer[][][]> records = new ArrayList<Integer[][][]>();
			//TODO �����T�C�����g���Ɉˑ��������ȓ��v�w�W��T���A�������ł̕����񎎍s��O��Ƃ�����͂���������
			while(iStable < 10 && cStep < MAX_ITER) {
				//���������͈ӌ��ω��̂������G�[�W�F���g���S�̂�5%�ȉ��̏�Ԃ�10�X�e�b�v�p�����邩�A���邢��20�X�e�b�v�ɓ��B���邩�B
				
				// �ӌ��䗦�̒ǐՁB�S�́A�T�C�����g�A���H�[�J���̏��B
				Integer[][] sumRecord = {{0,0,0,0},{0,0,0,0},{0,0,0,0}};
				for (InfoAgent agent : this.infoAgentsArray) {
					Integer opinion = agent.forceGetOpinion();
					if(opinion == null) opinion = 3;
					sumRecord[TOTAL_INDEX][opinion]++;
					if(agent.isSilent()) sumRecord[SILENT_INDEX][opinion]++;
					else sumRecord[VOCAL_INDEX][opinion]++;
				}
				//�ӌ��䗦�̋L�^�B
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 4; j++) rbw.write(sumRecord[i][j] + ",");
					rbw.write(" ,");
				}
				
				nUpdated = 0;
				//update�̒ǐՁB
				Integer[][] updateRecord = {{0,0,0,0},{0,0,0,0},{0,0,0,0}};
				double roll = this.localRNG.nextDouble();
				/*
				 * �S�G�[�W�F���g�ɂ��āA���f���Ɋ�Â����ݍ�p�����s
				 * 
				 */
				for (InfoAgent agent : this.infoAgentsArray) {
					boolean isUpdated = (roll < this.getModelReferenceRatio())? //���f���I��䁁IC�I�𗦂�臒l�Ƃ��Ċm���I�����Ă���B
							agent.IndependentCascade(infoAgentsArray)
							: agent.LinearThreashold(infoAgentsArray);
					if (isUpdated) {
						nUpdated++;
						Integer updatedOpinion = agent.getTmpOpinion();
						if(updatedOpinion == null) updatedOpinion = 3;
						updateRecord[TOTAL_INDEX][updatedOpinion]++;
						if(agent.isSilent()) updateRecord[SILENT_INDEX][updatedOpinion]++;
						else updateRecord[VOCAL_INDEX][updatedOpinion]++;
					}
				}
				//update�L�^�̏�������
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 4; j++) rbw.write(updateRecord[i][j] + ",");
					rbw.write(" ,");					
				}
				rbw.newLine();

				Integer[][][] tmpRecords = {sumRecord, updateRecord};
				records.add(tmpRecords);
				
				for (InfoAgent agent : this.infoAgentsArray) agent.applyOpinion(); //���ԃf�[�^��{�K�p����B
				
				if (((double)nUpdated / (double)nAgents) < CONVERGENCE_CONDITION){
					iStable++;
				} else {
					iStable = 0;
				}

				cStep++;
			}
			
			//�W�v�f�[�^�̌v�Z
			double totalPNRatio = (double)records.get(cStep)[SUM_INDEX][TOTAL_INDEX][POS_INDEX] / (double)records.get(cStep)[SUM_INDEX][TOTAL_INDEX][NEG_INDEX];
			double silentPNRatio = (double)records.get(cStep)[SUM_INDEX][SILENT_INDEX][POS_INDEX] / (double)records.get(cStep)[SUM_INDEX][SILENT_INDEX][NEG_INDEX];
			double vocalPNRatio = (double)records.get(cStep)[SUM_INDEX][VOCAL_INDEX][POS_INDEX] / (double)records.get(cStep)[SUM_INDEX][VOCAL_INDEX][NEG_INDEX];

			double VTDivergence = (vocalPNRatio > totalPNRatio)? vocalPNRatio / totalPNRatio : totalPNRatio / vocalPNRatio;
			double STDivergence = (silentPNRatio > totalPNRatio)? silentPNRatio / totalPNRatio : totalPNRatio / silentPNRatio;
			//double silentDivergence = silentPNRatio / totalPNRatio;
			
			//�W�v�f�[�^�̋L�^(�ǂݍ��݂₷�����邽�߂ɍŏI�s�ɂ܂Ƃ߂ď���)
			rbw.newLine();
			rbw.write(VTDivergence + "," + STDivergence);
			
			rbw.close();
			try {
				AreaChartGenerator acg = new AreaChartGenerator(records);
				acg.generateGraph(outDir, this.getInstanceName() + ".sum.png");
				LineChartGenerator lcg = new LineChartGenerator(records);
				lcg.generateGraph(outDir, this.getInstanceName() + ".update.png");
			} catch (Exception e) {
				this.logStackTrace(e);
			}
			
			this.endGate.countDown(); //�J�E���g�_�E��
			this.TaskLogger.info("Done: " + this.getInstanceName());
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
	private void initInfoAgentsArray(int nAgents, StaticNetwork ntwk) {
		this.infoAgentsArray= new InfoAgent[nAgents];
		for (int index = 0; index < nAgents; index++) {
			this.infoAgentsArray[index] = new InfoAgent(index, this.initOpinion(this.SPARSE_PATTERN), ntwk);
			if (this.localRNG.nextDouble() < this.getSilentAgentsRatio()) this.infoAgentsArray[index].muzzle();
				//������Ԃł͒P���Ƀ����_���Ŏw�芄���̃G�[�W�F���g���T�C�����g�ɂ��Ă����B
		}
		
	}
	
	/**�����Ɉˑ�����m�����z�ɏ]���A�G�[�W�F���g���T�C�����g�ɂ���B<br>
	 * �����O���t�Ȃ�getDegree()�Ŏ���������BgetnFollowed()�ł�����Ă��鐔�l�͓��������B<br>
	 * �L���O���t�Ȃ瑽���̎Q�Ƃ��W�߂�G�[�W�F���g���n�u�ƍl������̂ŁAgetnFollowed()���g���B
	 */
	@SuppressWarnings("unused")
	private void muzzleAgents() {
		int sumDegree = 0;
		for (InfoAgent agent : this.infoAgentsArray) {
			sumDegree += agent.getnFollowed();
		}
		
		for (InfoAgent agent : this.infoAgentsArray) {
			int degree = agent.getnFollowed();
			double roll = this.localRNG.nextDouble();
			if (roll <= this.silentPDF(degree)) agent.muzzle(); else agent.unmuzzle();
		}
	}

	/**
	 * �����Ɉˑ�����T�C�����g���̊m�����z�֐��B<br>
	 * �����͗��U�l�̎����Ȃ̂�Probability Distribution Function(PDF)�BProbability Mass Function(PMF)�Ƃ�������B<br>
	 * @param degree ����
	 * @return
	 */
	private double silentPDF(int degree) {
		// TODO �T�C�����g�ɂȂ�m���̕��z������
		double probability = 0.0;
		int flag = 1; //�X�C�b�`
		if (flag == 0) {
			if (degree < 10) {
				probability = this.getSilentAgentsRatio();
			} else {
				probability = 0.0;
			}
		} else if(flag == 1) {
			probability = this.getSilentAgentsRatio();
		}
		return probability;
	}

	//TODO ���̏o���������̍������[�U�̏ꍇ�A�Ⴂ���[�U�̏ꍇ�A�Ƃ���������Ԃ̌X���̈Ⴂ���l������̂ŁA����𐷂荞�ށB
	/**
	 * �ӌ��̏����l��^����Dpattern�ɂ���ċ������ς��D<br>
	 * �ENULL_PATTERN�i=0�j�̏ꍇ�F�S��null�ɂ���Dnull�͈ӌ��������ԁD<br>
	 * �EMIX_PATTERN�i=1)�̏ꍇ�F0,1,2�̂����ꂩ�ɂ���D<br>
	 * �ESPARSE_PATTERN(=2)�̏ꍇ�F90%��NULL�C10%�̓����_����0,1,2�̂����ꂩ�ɂ���B
	 * @param pattern
	 * @return
	 */
	private Integer initOpinion(int pattern) {
		Integer opinion = null;
		if (pattern == this.NULL_PATTERN) {
			opinion = null;
		} else if (pattern == this.MIX_PATTERN) {
			opinion = this.localRNG.nextInt(3);
		} else if (pattern == this.SPARSE_PATTERN) {
			opinion = null;
			double roll = this.localRNG.nextDouble();
			if (roll < 0.1) {
				opinion = this.localRNG.nextInt(3);
			}
		}
		return opinion;
	}

	/**���O�ȊO�����^�����A�����_���ȃp�����[�^�ŏ���������R���X�g���N�^�B
	 * 
	 * @param instanceName - ���O
	 */
	public SimulationTask(Object instanceName, CountDownLatch endGate) {
		this("recent", instanceName, NAGENTS_DEFAUT, Math.random(),Math.random(), null, endGate);
	}
	
	/**
	 * �^�C���X�^���v��^�����ɏ���������R���X�g���N�^�B�l�b�g���[�N�͌ʂɐ�������B���ʂ�"recent"�ȉ��ɏo�͂����B
	 * @param instanceName
	 * @param nAgents
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public SimulationTask(Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio, CountDownLatch endGate) {
		this("recent", instanceName, nAgents, silentAgentsRatio, modelReferenceRatio, null, endGate);
	}
	
	/**
	 * �^�C���X�^���v��^�����ɏ���������R���X�g���N�^�B�ÓI�l�b�g���[�N��^����B���ʂ�"recent"�ȉ��ɏo�͂����B
	 * @param instanceName
	 * @param nAgents
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 * @param ntwk
	 */
	public SimulationTask(Object instanceName, int nAgents, double silentAgentsRatio, double modelReferenceRatio, StaticNetwork ntwk, CountDownLatch endGate) {
		this("recent", instanceName, nAgents, silentAgentsRatio, modelReferenceRatio, ntwk, endGate);
	}
	/**��{�R���X�g���N�^�B
	 * 
	 * @param timeStamp - �V�~�����[�V�����S�̂̎��ʂ̂��߂ɗ^����ExectorService���N�����������B
	 * @param instanceName - �K���ȉǂȖ��O�B
	 * @param nAgents - �G�[�W�F���g��
	 * @param silentAgentsRatio - �T�C�����g��
	 * @param modelReferenceRatio - ���f���I���
	 * @param ntwk - �l�b�g���[�N�C���X�^���X
	 */
	public SimulationTask(String timeStamp,
						Object instanceName,
						int nAgents,
						double silentAgentsRatio,
						double modelReferenceRatio,
						StaticNetwork ntwk,
						CountDownLatch endGate) {
		try {
			this.setTimeStamp(timeStamp);
			this.setInstanceName(instanceName);
			this.setnAgents(nAgents);
			this.setSilentAgentsRatio(silentAgentsRatio);
			this.setModelReferenceRatio(modelReferenceRatio);
			this.refNetwork  = (ntwk == null)? null : ntwk;
			this.endGate  = endGate;
			//initTaskLogger();
		} catch (Exception e) {
			e.printStackTrace(); //TaskLogger���R���X�g���N�^�ŏ��������Ȃ��̂Ńf�t�H���g�o�͂��g�p����D
		}
	}


	public String getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
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
	/**���K�[�����������A�t�@�C���n���h����ݒ肷��B<br>
	 * ���O�t�@�C���̓A�y���h����B
	 * Runnable�^�X�N�̃��O�́i���S�ȏ�ɋy�Ԃ��Ƃ̂���j�^�X�N���Ƃł͂Ȃ��C�i���������v���Z�b�T��*�R�A���Ɏ��܂�j���s�X���b�h���ƂɎ擾�������D<br>
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
			ConsoleHandler ch = new ConsoleHandler();
			ch.setFormatter(new ShortLogFormatter());
			ch.setLevel(Level.WARNING);
			this.TaskLogger.addHandler(ch);				//stderr
			
			FileHandler fh = new FileHandler(logDir + "/" + this.getTaskLogFileName(), true);
			fh.setFormatter(new ShortLogFormatter());
			fh.setLevel(Level.ALL);
			this.TaskLogger.addHandler(fh);				//logfile
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**���K�[�̃t�@�C���n���h�����N���[�Y����D<br>
	 * ���̏�����lck�t�@�C����|�����邽�߂ɕK�v�D
	 * 
	 */
	private void closeLogFileHandler() {
		for (Handler handler : this.TaskLogger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
	/**��O�����K�[�ɗ������\�b�h�B<br>
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

	/**���̓f�[�^���i�[���Ă���f�B���N�g���p�X���w��B�f�t�H���g�l��(current)/data
	 * @param dataDir �Z�b�g���� dataDir
	 */
	public void setDataDir(String dataDir) {
		this.DataDir = dataDir;
	}
	
	/**
	 * outDir�ȉ��ɐ��������l�b�g���[�N�̗אڃ��X�g��dat�e�L�X�g�ŏo�͂���B
	 * @param outDir
	 */
	public void dumpNetworkList(File outDir) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (InfoAgent iAgent : this.infoAgentsArray) {
				bw.write(iAgent.getAgentIndex() + "(" + iAgent.getnFollowed() + ")\t:\t");
				for (Object neighbor : iAgent.getUndirectedList()) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**����^�X�N�����̃e�X�g�p�̃��\�b�h�D<br>
	 * �K���ȃe�L�X�g�t�@�C������͂Ƃ��C�P��̏o�����𐔂��グ��D<br>
	 * HashMap��String�Dsplit���g���C�Ō��ArrayList��Collection�Dsort�ŕ��ёւ���D<br>
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
