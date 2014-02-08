package matz.agentsim;

import java.util.Date;

import matz.basics.MatzExecutor;

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
		@SuppressWarnings("unused")
		Date date = new Date();
		int nIter = 1, sRatioResol = 10, mRatioResol = 1;
		int nAgents = 500;
		for (int k = 0; k < mRatioResol; k++) {
			//double mRatio = k * 0.10;
			double mRatio = 0.50;
			for (int j = 0; j < sRatioResol; j++) {
				double sRatio = j * 0.10;
				for (int i = 0; i < nIter; i++) {
					//SimulationTask rn = new SimulationTask(String.valueOf(date.getTime()), "condition" + j + "-" + i, 500, sRatio, mRatio);
					SimulationTask rn = new SimulationTask("condition" + j + "-" + i, nAgents, sRatio, mRatio);
						//�R���X�g���N�g���Ɏ�����^���Ȃ��ƁA"recent"�ȉ��Ɍ��ʂ��㏑���o�͂����B
					_E.execute(rn);
					//TODO �����ŃG�[�W�F���g���������E�l�b�g���[�N����
					_E.SimExecLogger.info("Submitted: " + rn.getInstanceName());
				}
			}
		}
		
		_E.safeShutdown();
		_E.closeLogFileHandler();
	}
}
