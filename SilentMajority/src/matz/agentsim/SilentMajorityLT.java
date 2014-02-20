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
		final int NCORE_INDEX = 0, CTRL_PITCH_INDEX = 1, CTRL_RESOL_INDEX = 2, LOWER_BOUND_INDEX = 3, NITER_INDEX = 4;
		int nIter = 10, controlResol;
		double controlPitch, lowerBound;
		
		//�����̓R�A��,�R���g���[���ϐ��̗��x�E�𑜓x�E�����l�C�V�~�����[�V�����񐔁DCorei7�ȏ�Ȃ�R�A��8���w�肵�Ă����DCorei5,i3,Core2 Quad�Ȃ�4�CCore2 Duo�Ȃ�2.
		try {
			_E = new MatzExecutor(Integer.parseInt(args[NCORE_INDEX]));
		} catch (Exception e) { _E = new MatzExecutor(); }
		
		try {
			controlPitch = Double.parseDouble(args[CTRL_PITCH_INDEX]);
			controlResol = Integer.parseInt(args[CTRL_RESOL_INDEX]);
			lowerBound = Double.parseDouble(args[LOWER_BOUND_INDEX]);
		} catch (Exception e) { 
			_E.SimExecLogger.severe("Specify information of control variables.");
			_E.safeShutdown();
			_E.closeLogFileHandler();
			return;
		}
		try {
			nIter = Integer.parseInt(args[NITER_INDEX]);
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		} catch (Exception e) {
			_E.logStackTrace(e);
		}
		
		_E.SimExecLogger.info("Starting "+ _E.getClass().getName() +". NumThreads = " + _E.getNumThreads());

		Date date = new Date();
		String simName = SilentMajorityLT.class.getSimpleName()+date.getTime();
		File outDir = new File("results",simName);
		if (!outDir.isDirectory()) outDir.mkdirs();
		double totalPosRatio = 0.1, initSilentRatio = 0.9;
//		controlPitch = 0.05;
//		controlResol = 19;
		CountDownLatch endGate = new CountDownLatch(controlResol * nIter); //�S�V�~�����[�V�������I������܂ł��J�E���g����CountDownLatch
		int nAgents = 1000;
		// �����Ńl�b�g���[�N����
		StaticNetwork cnnNtwk = new StaticCNNNetwork(nAgents);
		cnnNtwk.dumpNetwork(outDir);
		
		for (double controlVar = lowerBound; controlVar < lowerBound + controlPitch*controlResol; controlVar += controlPitch) {
			//double controlVar = j * controlPitch;
			for (int iter = 0; iter < nIter; iter++) {
				SimulationTaskLT rn = new SimulationTaskLT(simName, 
						"n="+String.format("%d",nAgents) +
						"pos="+String.format("%.2f",totalPosRatio) +
						"ctrl="+String.format("%.2f",controlVar) +
						"_" + iter,
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
			 * ���ʏW�v����
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
						public boolean accept(File dir, String name) { //csv�t�@�C���̂ݓǂݍ��ރt�B���^
							boolean ret = name.endsWith(".csv");
							return ret;
						}
					});
				if (resultFiles == null || resultFiles.length == 0) break;

				for (File resultFile : resultFiles) {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
					String line = new String(), lastLine = new String();
					while((line = br.readLine()) != null) lastLine = line; //�ŏI�s�擾
					String[] values = lastLine.split(",");
					double[] dValues = {Double.parseDouble(values[1]),Double.parseDouble(values[2])};
					vocalOpinions[j-1][0] += dValues[0] / nIter;
					vocalOpinions[j-1][1] += dValues[1] / nIter;
					br.close();
				}
			}
			
			//csv�o��
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
