package matz;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class RunnableSimulator implements Runnable {

	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	private String TaskLogFileName;
	private Logger TaskLogger = null;
	
	/**�T�C�����g������́D
	 * @param silentAgentsRatio
	 */
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		SilentAgentsRatio = silentAgentsRatio;
	}

	/**�T�C�����g�����擾�D
	 * @return
	 */
	public double getSilentAgentsRatio() {
		return SilentAgentsRatio;
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

	/**�^�X�N���Ƃ̃��O�t�@�C��������t�x�[�X�Ŏw��B
	 * @param taskLogFileName �Z�b�g���� taskLogFileName
	 */
	public void setTaskLogFileName() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		TaskLogFileName = this.getClass().getName() + Thread.currentThread().getId() + "-" + df.format(date);
	}
	/**Logger�����������A�t�@�C���n���h����ݒ肷��B
	 * ���O�t�@�C���͓��t�x�[�X�Ŗ��O�t�����A�A�y���h����B
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void initSimExecLogger() throws SecurityException, IOException {
		setTaskLogFileName();
		TaskLogger = Logger.getLogger(this.getClass().getName());
		FileHandler fh = new FileHandler("logs/" + getTaskLogFileName() + ".log", true);
		fh.setFormatter(new SimpleFormatter());
		TaskLogger.addHandler(fh);														//logfile
		TaskLogger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));		//stdout
	}
	public void logConf(String msg) {
		TaskLogger.config(msg);
	}
	public void logInfo(String msg) {
		TaskLogger.info(msg);
	}
	public void logWarn(String msg) {
		TaskLogger.warning(msg);
	}
	public void logErr(String msg) {
		TaskLogger.severe(msg);
	}
	/**�����_���ȃT�C�����g���ƃ��f���I���ŃV�~�����[�V�������������D
	 * 
	 */
	public RunnableSimulator() {
		setSilentAgentsRatio(Math.random());
		setModelReferenceRatio(Math.random());
	}
	
	/**�w�肵���T�C�����g���ƃ��f���I���ŃV�~�����[�V�������������D
	 * @param silentAgentsRatio
	 * @param modelReferenceRatio
	 */
	public RunnableSimulator(double silentAgentsRatio, double modelReferenceRatio) {
		setSilentAgentsRatio(silentAgentsRatio);
		setModelReferenceRatio(modelReferenceRatio);
	}
	/**����^�X�N�����̃e�X�g�p�̃��\�b�h�D
	 * �K���ȃe�L�X�g�t�@�C������͂Ƃ��C�P��̏o�����𐔂��グ��D
	 * HashMap��String�Dsplit���g���C�Ō��ArrayList��Collection�Dsort�ŕ��ёւ���D
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
			e.printStackTrace();
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
		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("wc_"
				+ Thread.currentThread().toString().replaceAll("\\s", "") + ".txt", true));
		try {
			osw.write(entries.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		osw.close();		
	}
	
	@Override
	public void run() {
		RunnableSimulator rs = new RunnableSimulator();
		
		rs.logInfo(Thread.currentThread().getId() + " : " + Thread.currentThread().toString()
				+ "\tRunning simulation with following parameters:");
		for (Field field : rs.getClass().getDeclaredFields()) {
			try {
				rs.logInfo(field.getName()+" = "+field.get(this).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < 10; i++) {
			try {
				//procedure
				WordCount(new File("zarathustra.txt"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		rs.logInfo("Done.");
	}

}
