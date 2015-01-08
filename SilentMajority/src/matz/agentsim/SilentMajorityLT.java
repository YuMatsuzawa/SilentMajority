package matz.agentsim;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import matz.basics.MatzExecutor;
import matz.basics.network.*;

public class SilentMajorityLT {

	static int TYPE_RANKED = 0, TYPE_BIASED = 1, TYPE_RELIEF = 2, TYPE_THRES = 3, TYPE_THRES2 = 4, TYPE_NORMAL = 5;
	static String[] SIM_TYPE_NAME = {"HighD", "BiasedV", "Relief", "CtrlTH", "SepTH", "Normal"};
	
	static int CNN_INDEX = 0, WS_INDEX = 1, BA_INDEX = 2, RND_INDEX = 3, REG_INDEX = 4, CSTM_INDEX = 5;
	static String[] NTWK_NAME = {"CNN","WS","BA","RND","REG","CSTM"};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MatzExecutor _E = null;
		int nIter = 10, simType = 0, nAgents = 1000;
		int controlResol;
		double totalPosRatio = 0.2, initSilentRatio = 0.9;
		double controlPitch, lowerBound;
		Double pRewire = null, degree = null;
		boolean noiseEnabled = false, ntwkFig = true;
		String customNetworkPath = "";
		String ntwkType = NTWK_NAME[CNN_INDEX];
		
		/*
		 * Configファイルでパラメータ管理する．
		 */
		Properties conf = new Properties();
		try {
			conf.loadFromXML(new FileInputStream(args[0]));
		} catch(FileNotFoundException e) {
			System.err.println("First argument must be proper path to configuration XML.");
			return;
		} catch(IOException e) {
			System.err.println("Cannot read "+args[0]);
			return;
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			_E = new MatzExecutor(Integer.parseInt(conf.getProperty("nCore")));
		} catch (Exception e) {
			_E = new MatzExecutor();
		}
		try {
			nIter = Integer.parseInt(conf.getProperty("nIter"));
		} catch (Exception e) {
			//do nothing
		}
		try {
			nAgents = Integer.parseInt(conf.getProperty("nAgents"));
		} catch (Exception e) {
			//do nothing
			//nAgents should always be given by user. if not, default value of 1000 will hold.
		}
		try {
			simType = Integer.parseInt(conf.getProperty("simType"));
			ntwkType = conf.getProperty("ntwkType");
			controlResol = Integer.parseInt(conf.getProperty("controlResol"));
			controlPitch = Double.parseDouble(conf.getProperty("controlPitch"));
			lowerBound = Double.parseDouble(conf.getProperty("lowerBound"));
			totalPosRatio = Double.parseDouble(conf.getProperty("totalPosRatio"));
			initSilentRatio = Double.parseDouble(conf.getProperty("initSilentRatio"));
		} catch (Exception e) {
			_E.logStackTrace(e);
			_E.safeShutdown();
			_E.closeLogFileHandler();
			return;
		}
		try {
			pRewire = Double.parseDouble(conf.getProperty("pRewire"));
		} catch (Exception e) {
			//do nothing
		}
		try {
			degree = Double.parseDouble(conf.getProperty("degree"));
		} catch (Exception e) {
			//do nothing
		}
		try {
			noiseEnabled = (conf.getProperty("noiseEnabled").equals("0"))? false : true;
		} catch (Exception e) {
			//do nothing
		}
		try {
			ntwkFig = (conf.getProperty("ntwkFig").equals("0"))? false : true;
		} catch (Exception e) {
			//do nothing
		}
		try {
			customNetworkPath = conf.getProperty("customNetworkPath");
		} catch (Exception e) {
			//do nothing
		}
		
		if (simType == TYPE_RANKED) {
			String testStr = String.format("%.6f", lowerBound + controlPitch*(controlResol - 1));
			if (Double.parseDouble(testStr) > totalPosRatio) {
				_E.SimExecLogger.severe("ControlVar out of bound.");
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			}
		} else if (simType == TYPE_THRES) {
			String testStr = String.format("%.6f", lowerBound + controlPitch*(controlResol - 1));
			if (Double.parseDouble(testStr) > 1.0) {
				_E.SimExecLogger.severe("ControlVar out of bound.");
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			}
		} else if (simType == TYPE_NORMAL) {
			lowerBound = 0.0;
			controlResol = 1;
		}
		
		_E.SimExecLogger.info("Starting "+ _E.getClass().getName() +". NumThreads = " + _E.getNumThreads());

		Date date = new Date();
		
		CountDownLatch endGate = new CountDownLatch(controlResol * nIter); //全シミュレーションが終了するまでをカウントするCountDownLatch
				
		// ここでネットワーク生成
		StaticNetwork ntwk = null;
		if (ntwkType.equals(NTWK_NAME[CNN_INDEX])) ntwk = new StaticCNNNetwork(nAgents, degree);
		else if (ntwkType.equals(NTWK_NAME[WS_INDEX])) {
			if (pRewire == null) ntwk = new StaticWSNetwork(nAgents, degree);
			else ntwk = new StaticWSNetwork(nAgents, degree, pRewire);
		}
		else if (ntwkType.equals(NTWK_NAME[BA_INDEX])) ntwk = new StaticBANetwork(nAgents, degree);
		else if (ntwkType.equals(NTWK_NAME[RND_INDEX])) ntwk = new StaticRNDNetwork(nAgents, degree);
		else if (ntwkType.equals(NTWK_NAME[REG_INDEX])) ntwk = new StaticREGNetwork(nAgents, degree);
		else if (ntwkType.equals(NTWK_NAME[CSTM_INDEX])) { //カスタムネットワーク
			try {
				ntwk = new StaticCSTMNetwork(customNetworkPath, nAgents);
				degree = ntwk.get
			} catch (FileNotFoundException e) {
				_E.SimExecLogger.severe("Custom Network file not found.");
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			} catch (IOException e) {
				_E.SimExecLogger.severe("Cannot read Custom Network file.");
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			} catch (Exception e) {
				_E.logStackTrace(e);
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			}
		}
		
		String simName = SIM_TYPE_NAME[simType] + "_" + ntwkType + date.getTime();
		File outDir = new File("results",simName);
		if (!outDir.isDirectory()) outDir.mkdirs();
		try {
			ntwk.dumpNetwork(outDir);
		} catch(Exception e) {
			_E.logStackTrace(e);
		}
		
		for (double controlVar = lowerBound; controlVar < lowerBound + controlPitch*controlResol; controlVar += controlPitch) {
			for (int iter = 0; iter < nIter; iter++) {
				SimulationTaskLT rn = new SimulationTaskLT(simName, 
						"n="+String.format("%d",nAgents) +
						"pos="+String.format("%.4f",totalPosRatio) +
						"sil="+String.format("%.4f",initSilentRatio) +
						"ctrl="+String.format("%.4f",controlVar) +
						"_" + iter,
						nAgents, totalPosRatio, controlVar, initSilentRatio, noiseEnabled, ntwk, endGate, ntwkFig);
				_E.execute(rn);
				_E.SimExecLogger.info("Submitted: " + rn.getInstanceName());
			}
		}
		
		_E.safeShutdown();
		_E.SimExecLogger.info("Waiting for tasks to complete...");
		try {
			endGate.await();
			_E.SimExecLogger.info("Result summarizing...");
			/*
			 * 結果集計操作
			 * 
			 */
			double[][] vocalOpinions = new double[controlResol][2];
			int index = 0;
			for (double controlVar = lowerBound; controlVar < lowerBound + controlPitch*controlResol; controlVar += controlPitch) {
				File resultDir = new File("results/"+simName,
						"n="+String.format("%d",nAgents) +
						"pos="+String.format("%.4f",totalPosRatio) +
						"sil="+String.format("%.4f",initSilentRatio) +
						"ctrl="+String.format("%.4f",controlVar)
						);
				File[] resultFiles = resultDir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) { //csvファイルのみ読み込むフィルタ
							boolean ret = name.endsWith(".csv");
							return ret;
						}
					});
				if (resultFiles == null || resultFiles.length == 0) break;

				for (File resultFile : resultFiles) {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
					String line = new String(), lastLine = new String();
					while((line = br.readLine()) != null) lastLine = line; //最終行取得
					String[] values = lastLine.split(",");
					double[] dValues = {Double.parseDouble(values[1]),Double.parseDouble(values[2])};
					vocalOpinions[index][0] += dValues[0] / nIter; //重み付き加算=>平均
					vocalOpinions[index][1] += dValues[1] / nIter; //重み付き加算=>平均
					br.close();
				}
				index++;
			}
			
			//csv出力
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "PosNegRatio" + date.getTime() + ".csv"))));
			bw.write("control,pos,neg");
			bw.newLine();
			for (int j = 0; j < controlResol; j++){
				bw.write(lowerBound + j*controlPitch + "," + (int)vocalOpinions[j][0] +"," + (int)vocalOpinions[j][1]);
				bw.newLine();
			}
			bw.newLine();
			bw.write("totalPos,initSilent");
			bw.newLine();
			bw.write(totalPosRatio+","+initSilentRatio);
			bw.close();
			
			_E.SimExecLogger.info("Summarizing done.");
		} catch(Exception e) {
			_E.logStackTrace(e);
		} finally {
			_E.closeLogFileHandler();
		}
	}

}
