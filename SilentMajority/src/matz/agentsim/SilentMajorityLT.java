package matz.agentsim;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import matz.basics.MatzExecutor;
import matz.basics.network.*;

public class SilentMajorityLT {

	static int TYPE_RANKED = 0, TYPE_BIASED = 1, TYPE_RELIEF = 2;
	static String[] SIM_TYPE_NAME = {"BiasedOpinionByRank", "BiasedVocalization", "RelievingAgents"};
	static int CNN_INDEX = 0, WS_INDEX = 1, BA_INDEX = 2, RND_INDEX = 3, REG_INDEX = 4;
	static String[] NTWK_NAME = {"CNN","WS","BA","RND","REG"};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MatzExecutor _E = null;
		int nIter = 10, simType = 0, nAgents = 1000;
		int controlResol;
		double totalPosRatio = 0.2, initSilentRatio = 0.9;
		double controlPitch, lowerBound;
		String ntwkType = NTWK_NAME[CNN_INDEX];
		
		/*
		 * Configファイルでパラメータ管理する．
		 */
		Properties conf = new Properties();
		try {
			conf.loadFromXML(new FileInputStream(args[0]));
		} catch(FileNotFoundException e) {
			System.err.println("First argument must be proper path to configuration XML.");
			System.exit(-1);
		} catch(IOException e) {
			System.err.println("Cannot read "+args[0]);
			System.exit(-1);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
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
		
		if (simType == TYPE_RANKED) {
			if (lowerBound + controlPitch*controlResol > totalPosRatio) {
				_E.SimExecLogger.severe("ControlVar out of bound.");
				_E.safeShutdown();
				_E.closeLogFileHandler();
				return;
			}
		}
		
		_E.SimExecLogger.info("Starting "+ _E.getClass().getName() +". NumThreads = " + _E.getNumThreads());

		Date date = new Date();
		
		CountDownLatch endGate = new CountDownLatch(controlResol * nIter); //全シミュレーションが終了するまでをカウントするCountDownLatch
				
		// ここでネットワーク生成
		StaticNetwork ntwk = null;
		if (ntwkType.equals(NTWK_NAME[CNN_INDEX])) ntwk = new StaticCNNNetwork(nAgents);
		else if (ntwkType.equals(NTWK_NAME[WS_INDEX])) ntwk = new StaticWSNetwork(nAgents);
		else if (ntwkType.equals(NTWK_NAME[BA_INDEX])) ntwk = new StaticBANetwork(nAgents);
		else if (ntwkType.equals(NTWK_NAME[RND_INDEX])) ntwk = new StaticRNDNetwork(nAgents);
		else if (ntwkType.equals(NTWK_NAME[REG_INDEX])) ntwk = new StaticREGNetwork(nAgents);
		
		String simName = SIM_TYPE_NAME[simType] + "_" + ntwkType + date.getTime();
		File outDir = new File("results",simName);
		if (!outDir.isDirectory()) outDir.mkdirs();
		ntwk.dumpNetwork(outDir);
		
		for (double controlVar = lowerBound; controlVar < lowerBound + controlPitch*controlResol; controlVar += controlPitch) {
			for (int iter = 0; iter < nIter; iter++) {
				SimulationTaskLT rn = new SimulationTaskLT(simName, 
						"n="+String.format("%d",nAgents) +
						"pos="+String.format("%.2f",totalPosRatio) +
						"sil="+String.format("%.2f",initSilentRatio) +
						"ctrl="+String.format("%.2f",controlVar) +
						"_" + iter,
						nAgents, totalPosRatio, controlVar, initSilentRatio, ntwk, endGate);
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
						"pos="+String.format("%.2f",totalPosRatio) +
						"sil="+String.format("%.2f",initSilentRatio) +
						"ctrl="+String.format("%.2f",controlVar)
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
					vocalOpinions[index][0] += dValues[0] / nIter;
					vocalOpinions[index][1] += dValues[1] / nIter;
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
			bw.close();
			
			_E.SimExecLogger.info("Summarizing done.");
		} catch(Exception e) {
			_E.logStackTrace(e);
		} finally {
			_E.closeLogFileHandler();
		}
	}

}
