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
	@SuppressWarnings("unused")
	private static final int NULL_PATTERN = 0, MIX_PATTERN = 1, SPARSE_PATTERN = 2,
			HUB_DRIVEN_PATTERN = 3, LEAF_DRIVEN_PATTERN = 4;
	private static final String[] PATTERN_NAME = {"NULL","MIX","SPARSE","HUB_DRIVEN","LEAF_DRIVEN"};
	
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
		
		int pattern = HUB_DRIVEN_PATTERN;
		int nIter = 10, sRatioResol = 9, mRatioResol = 11;
		CountDownLatch endGate = new CountDownLatch(sRatioResol * mRatioResol * nIter); //�S�V�~�����[�V�������I������܂ł��J�E���g����CountDownLatch
		int nAgents = 1000;
		// �����Ńl�b�g���[�N����
		StaticNetwork cnnNtwk = new StaticCNNNetwork(nAgents);
		//cnnNtwk.dumpList(outDir);
		cnnNtwk.dumpNetwork(dateDir);
		for (int k = 1; k <= sRatioResol; k++) {
			double sRatio = k * 0.10;
			for (int j = 0; j < mRatioResol; j++) {
				double mRatio = j * 0.10;
				//double mRatio = 0.50;
				for (int i = 0; i < nIter; i++) {
					SimulationTask rn = new SimulationTask(String.valueOf(date.getTime()),
							"condition" + k + "-" + j + "_" + i , nAgents, sRatio, mRatio, pattern, cnnNtwk, endGate);
					//SimulationTask rn = new SimulationTask("condition" + k + "-" + j + "_" + i, nAgents, sRatio, mRatio, pattern, cnnNtwk, endGate);
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
			final int X_INDEX = 0, Y_INDEX = 1, Z_INDEX = 2;
			double[][] VTDivergence = new double[3][sRatioResol*mRatioResol];
			double[][] STDivergence = new double[3][sRatioResol*mRatioResol];
			double[][] totalNullRatio = new double[3][sRatioResol*mRatioResol];

			for (int k = 1; k <= sRatioResol; k++) {
				double sRatio = k * 0.10;
				for (int j = 0; j < mRatioResol; j++) {
					double mRatio = j * 0.10;
					File resultDir = new File(dateDir, 
							PATTERN_NAME[pattern] +
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
					if (resultFiles == null || resultFiles.length == 0) break;
					
					VTDivergence[X_INDEX][(k-1) * mRatioResol + j] = sRatio;
					VTDivergence[Y_INDEX][(k-1) * mRatioResol + j] = mRatio;
					STDivergence[X_INDEX][(k-1) * mRatioResol + j] = sRatio;
					STDivergence[Y_INDEX][(k-1) * mRatioResol + j] = mRatio;
					totalNullRatio[X_INDEX][(k-1) * mRatioResol + j] = sRatio;
					totalNullRatio[Y_INDEX][(k-1) * mRatioResol + j] = mRatio;
					
					Arrays.sort(resultFiles);
					for (File resultFile : resultFiles) {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
						String line = new String(), lastLine = new String();
						while((line = br.readLine()) != null) lastLine = line; //�ŏI�s�擾
						String[] values = lastLine.split(",");
						double nullRatio = Double.parseDouble(values[0]), VTDiv = Double.parseDouble(values[1]), STDiv = Double.parseDouble(values[2]);
						totalNullRatio[Z_INDEX][(k-1) * mRatioResol + j] += nullRatio / nIter;
						VTDivergence[Z_INDEX][(k-1) * mRatioResol + j] += (Double.isInfinite(VTDiv))? Double.NaN : VTDiv / nIter;
						STDivergence[Z_INDEX][(k-1) * mRatioResol + j] += (Double.isInfinite(STDiv))? Double.NaN : STDiv / nIter;
						br.close();
					}
				}
			}

			//csv�o��
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dateDir, "TotalNullRatio.csv"))));
			for (int k = 0; k <= sRatioResol; k++){
				for (int j = -1; j < mRatioResol; j++) {
					if (k == 0) {
						if (j == -1) continue;
						else bw.write("," + j*0.1);
					} else {
						if (j == -1) bw.write(String.format("%.1f",k*0.1));
						else bw.write("," + totalNullRatio[Z_INDEX][(k-1) * mRatioResol + j]);
					}
				}
				bw.newLine();
			}
			bw.close();
			
			//Contour�o��
			ContourGenerator cg = new ContourGenerator("TotalNullRatio", totalNullRatio);
			cg.generateGraph(dateDir, "contour.png");
			
			_E.SimExecLogger.info("Summarizing done.");
		} catch(Exception e) {
			_E.logStackTrace(e);
		} finally {
			_E.closeLogFileHandler();
		}
		
	}
}
