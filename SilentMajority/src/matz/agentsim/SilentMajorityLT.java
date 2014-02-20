package matz.agentsim;

import java.io.*;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import matz.basics.MatzExecutor;
import matz.basics.StaticNetwork;

public class SilentMajorityLT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatzExecutor _E = null;
		
		//引数はコア数のみ．Corei7以上なら8を指定していい．Corei5,i3,Core2 Quadなら4，Core2 Duoなら2.
		if (args.length > 0) {
			for (String arg : args) {
				try {
					int numThreads = Integer.parseInt(arg);
					_E = new MatzExecutor(numThreads);
				} catch (NumberFormatException e) {
					_E = new MatzExecutor();
				}
			}
		} else {
			_E = new MatzExecutor();
		}
		
		_E.SimExecLogger.info("Starting "+ _E.getClass().getName() +". NumThreads = " + _E.getNumThreads());

		Date date = new Date();
		String simName = SilentMajorityLT.class.getSimpleName()+date.getTime();
		File outDir = new File("results",simName);
		if (!outDir.isDirectory()) outDir.mkdirs();
		double controlPitch = 0.1, totalPosRatio = 0.2, initSilentRatio = 0.9;
		int nIter = 10, controlResol = 9;
		CountDownLatch endGate = new CountDownLatch(controlResol * nIter); //全シミュレーションが終了するまでをカウントするCountDownLatch
		int nAgents = 1000;
		// ここでネットワーク生成
		StaticNetwork cnnNtwk = new StaticCNNNetwork(nAgents);
		cnnNtwk.dumpNetwork(outDir);
		
		for (int j = 1; j <= controlResol; j++) {
			double controlVar = j * controlPitch;
			for (int i = 0; i < nIter; i++) {
				SimulationTaskLT rn = new SimulationTaskLT(simName, "condition" + j + "_" + i,
						nAgents, totalPosRatio, controlVar, initSilentRatio, cnnNtwk, endGate);
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
			
			for (int j = 1; j <= controlResol; j++) {
				double controlVar = j * controlPitch;
				File resultDir = new File("results/"+simName,
						"n="+String.format("%d",nAgents) +
						"pos="+String.format("%.2f",totalPosRatio) +
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
					vocalOpinions[j-1][0] += dValues[0] / nIter;
					vocalOpinions[j-1][1] += dValues[1] / nIter;
					br.close();
				}
			}
			
			//csv出力
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "PosNegRatio" + date.getTime() + ".csv"))));
			bw.write("control,pos,neg");
			bw.newLine();
			for (int j = 1; j <= controlResol; j++){
				bw.write(j*controlPitch + "," + (int)vocalOpinions[j-1][0] +"," + (int)vocalOpinions[j-1][1]);
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
