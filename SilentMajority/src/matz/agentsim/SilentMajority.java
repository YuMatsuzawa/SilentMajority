package matz.agentsim;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import matz.basics.MatzExecutor;
import matz.basics.StaticNetwork;

/**
 * ���`�d�l�b�g���[�N�ɂ�����T�C�����g�E���[�U�̉e���𕪐͂���V�~�����[�V�����B<br>
 * ���̃N���X��main�֐����z�X�g���A�R�}���h���C����������������G���g���|�C���g�ł���B<br>
 * �p�����[�^��ύX�����ĘA���V�~�����[�V�������邽�߂ɁAbasics�p�b�P�[�W��MatzExecutor���C���X�^���X�����Ďg�p���邱�ƁB<br>
 * MatzExecutor�̓}���`�X���b�h�����̂��߂̃t���[�����[�N�ł���ExecutorService���������Ă���B<br>
 * �X�̃V�~�����[�V�����ɂ�������ۂ̏������e��Runnable�i���邢��Callable�j���������ă^�X�N�I�u�W�F�N�g���`���A<br>
 * �����MatzExecutor�C���X�^���X��execute�i���邢��submit�j���Ďg�p����B
 * 
 * @author Romancer
 *
 */
public final class SilentMajority {
	
	public static final void main(String[] args) {
		MatzExecutor _E = null;
		
		//�����̓R�A���̂݁DCorei7�ȏ�Ȃ�8���w�肵�Ă����DCorei5,i3,Core2 Quad�Ȃ�4�CCore2 Duo�Ȃ�2.
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
		//�p�����[�^��ύX�����Ȃ���V�~�����[�V��������C�e���[�^�D
		//nIter�͓�������ł̃V�~�����[�V���������񂸂s�����w�肷��D
		//�V�~�����[�V�����̉𑜓x�̓p�����[�^���Ƃ�Resol�Ŏw�肷��D
		Date date = new Date();
		//File outDir = new File("results/recent");
		File dateDir = new File("results/"+date.getTime());
		//if (!outDir.isDirectory()) outDir.mkdirs();
		if (!dateDir.isDirectory()) dateDir.mkdirs();
		
		int nIter = 2, sRatioResol = 9, mRatioResol = 11;
		CountDownLatch endGate = new CountDownLatch(sRatioResol * mRatioResol * nIter); //�S�V�~�����[�V�������I������܂ł��J�E���g����CountDownLatch
		int nAgents = 500;
		// �����Ńl�b�g���[�N����
		StaticNetwork cnnNtwk = new StaticCNNNetwork(nAgents);
		//cnnNtwk.dumpList(outDir);
		cnnNtwk.dumpList(dateDir);
		for (int k = 1; k <= sRatioResol; k++) {
			double sRatio = k * 0.10;
			for (int j = 0; j < mRatioResol; j++) {
				double mRatio = j * 0.10;
				//double mRatio = 0.50;
				for (int i = 0; i < nIter; i++) {
					SimulationTask rn = new SimulationTask(String.valueOf(date.getTime()), "condition" + k + "-" + j + "_" + i , 500, sRatio, mRatio, cnnNtwk, endGate);
					//SimulationTask rn = new SimulationTask("condition" + k + "-" + j + "_" + i, nAgents, sRatio, mRatio, cnnNtwk, endGate);
						//�R���X�g���N�g���Ɏ�����^���Ȃ��ƁA"recent"�ȉ��Ɍ��ʂ��㏑���o�͂����B
					_E.execute(rn);
					_E.SimExecLogger.info("Submitted: " + rn.getInstanceName());
				}
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
			double[][] VTDivergence = new double[sRatioResol][mRatioResol];
			double[][] STDivergence = new double[sRatioResol][mRatioResol];
			

			for (int k = 1; k <= sRatioResol; k++) {
				double sRatio = k * 0.10;
				for (int j = 0; j < mRatioResol; j++) {
					double mRatio = j * 0.10;
					File resultDir = new File(dateDir, 
							"n=" + String.format("%d", nAgents) +
							"s=" + String.format("%.1f", sRatio) +
							"m=" + String.format("%.1f", mRatio));
					File[] resultFiles = resultDir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) { //csv�t�@�C���̂ݓǂݍ��ރt�B���^
							boolean ret = name.endsWith(".csv");
							return ret;
						}
					});
					Arrays.sort(resultFiles);
					for (File resultFile : resultFiles) {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
						String lastline;
						while(br.readLine() != null) {
							lastline = br.readLine();
						}

					}
				}
			}
			
			_E.SimExecLogger.info("Summarizing done.");
		} catch(InterruptedException e) {
			_E.logStackTrace(e);
		} catch (FileNotFoundException e) {
			_E.logStackTrace(e);
		} catch (IOException e) {
			_E.logStackTrace(e);
		}
		
	}
}
