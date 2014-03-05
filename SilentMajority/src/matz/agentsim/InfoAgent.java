package matz.agentsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import matz.basics.network.StaticNetwork;

public class InfoAgent {
	
	private int agentIndex;
	private ArrayList<Integer> followingList;
	private ArrayList<Integer> followedList;
	private boolean isSilent;
	private boolean tmpSilent;
	/**���ۓI�Ȉӌ��i���j��\����ԕϐ��D<br>
	 * �v���~�e�B�uint�ł͂Ȃ����b�p�^��Integer�ɂ��Ă����Cnull���g����悤�ɂ���D
	 */
	private Integer opinion;
	private Integer tmpOpinion;
	private double influence = Math.random();
	private double threshold = Math.random(); //�����_���ɂ���
	private double noiseRatio = 0.05;
	private double bendThreshold = 0.05; //relief��muzzling�ŏ����h臒l�̏ꍇ
	private StaticNetwork refNetwork = null;
	private boolean isNetworkStatic = false;
	
	/**�����������I�E���邢�͖���`�̏�Ԃɂ���Ƃ��C�m��I�E�ے�I��킸���炩�̐�i�I�ӌ��ɐG���ƁC����ɉe�������D<br>
	 * �e�����󂯂邩�ۂ��́C����̉e���͂̋����ɂ��D<br>
	 * �אڃ��X�g�ɂ���l�����ɎQ�Ƃ��Ă����A���H�[�J���Ȑl�̒��ōł��e���͂̍����l����e�������B
	 * @return �ω�����������true
	 */
	public boolean independentCascade(InfoAgent[] infoAgentsArray) {
		if (!(this.getOpinion() == null || this.getOpinion() == 0)) return false;

		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		double topInfluence = -1;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion();
			double neighborInfluence = infoAgentsArray[(Integer) neighbor].getInfluence();
				//�T�C�����g�G�[�W�F���g��influence��-1�ƕԂ��Ă���B
			//if (preOp == null || preOp == 0) { //�������ԓx������ł���Ƃ�����IC�̉e�����󂯂Ȃ��C�Ƃ��������i�Ȃ������Ă����j
				if (neighborInfluence > topInfluence && neighborOp != null && neighborOp > 0) { //�����ŁA���肪�T�C�����g�Ȃ�s�K
					tmpOp = neighborOp;
					topInfluence = neighborInfluence;
				} else {
					continue;
				}
			//}
		}
		this.setTmpOpinion(tmpOp);
		if (!this.getTmpOpinion().equals(preOp)) return true;
		return false;
	}

	/**�אڂ��Ă���m�[�h�̒��ł̑����h��m�o���āC���̉e�����󂯂�D
	 * @param infoAgentsArray
	 * @return
	 */
	public boolean linearThreashold(InfoAgent[] infoAgentsArray) {
		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		
		int sum = 0;
		Integer[] opinions = {0,0,0};
		int NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion(); 
			if (neighborOp == null) continue; //����`�̐l�E�T�C�����g�Ȑl�͊��肵�Ȃ��D
			
			sum++;
			opinions[neighborOp]++;
		}
		if (sum == 0) return false;
/*		for (int opIndex = 0; opIndex < 3; opIndex++) { //�P���ɁCnull�ȊO�̈ӌ��̔䗦�𒲂ׁC臒l�𒴂��Ă�����̂ɕt�a��������
			if (opinions[opIndex] / sum > this.threshold) tmpOp = opIndex;
		}*/
		
		/*
		 * ������́C1or2�̈ӌ����ǂ���������h���������Ă��Ȃ���΁C�����h�������h�ł����Ă��Ȃ��Ă������̗�������C�Ƃ������f���D
		 */
		if (opinions[POS_INDEX] / sum > this.getThreshold()) tmpOp = POS_INDEX;
		else if (opinions[NEG_INDEX] / sum > this.getThreshold()) tmpOp = NEG_INDEX;
		else tmpOp = NEU_INDEX;
		
		this.setTmpOpinion(tmpOp);
		if (!this.getTmpOpinion().equals(preOp)) return true;
		return false;
	}

	/**
	 * �h���V�~�����[�V�����̂��߂̃��\�b�h�D<br>
	 * LT���f���Ɋ�Â��C�אڎ��͂̑����h�ɔ������āC�T�C�����g��������C���H�[�J���������肷��D<br>
	 * ���[��:�����Ɠ����ӌ����C<br>
	 * 1.������臒l�𒴂��鑽���h�Ȃ烔�H�[�J���ɂȂ�<br>
	 * 2.������臒l������鏭���h�Ȃ�T�C�����g�ɂȂ�<br>
	 * 臒l�̓����_���ɕ��z���Ă���̂ŁC�����Ɠ����ӌ������Ƃ������h�ł��T�C�����g�ɂȂ�Ȃ�������C<br>
	 * �t�ɑ��������h�ł����H�[�J���ɂȂ�Ȃ������肷��G�[�W�F���g����������D<br>
	 * @param infoAgentsArray
	 * @return �ω������true
	 */
	public boolean linearThreasholdMuzzling(InfoAgent[] infoAgentsArray) {
		boolean preSilent = (this.isSilent())? true : false;
		boolean tmpSilent = (this.isSilent())? true : false;
		
		int sumOfVocal = 0, sumOfVocalizedSameOpinion = 0;
		for (int neighborIndex : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[neighborIndex].getOpinion();
			if (neighborOp == null) continue; //�T�C�����g�͖���
			else {
				sumOfVocal++; //null�łȂ��Ȃ烔�H�[�J��
				if (neighborOp.equals(this.forceGetOpinion())) {
					sumOfVocalizedSameOpinion++; //�����̐^�̈ӌ��Ɠ����ӌ��̐l�𐔂���
				}
			}
		}
		if (sumOfVocal == 0){
			this.tmpSilent = tmpSilent;
			return false;
		}
		if ((double)sumOfVocalizedSameOpinion / (double)sumOfVocal >= this.getThreshold()) tmpSilent = false;
		else tmpSilent = true;
		
		this.tmpSilent = tmpSilent;
		if (preSilent ^ this.tmpSilent) return true;
		return false;
	}

	/**
	 * ���gRelief���邢�͑��l�C��Slack�𓱓�����{@link #linearThreasholdMuzzling(InfoAgent[])}����̔h���V�~�����[�V����<br>
	 * �����̈ӌ����ɂ߂ď����h�ł���ꍇ�T�C�����g�ɂȂ�C<br>
	 * ������x�s�����𓾂�ƃ��H�[�J���ɂȂ�C<br>
	 * �X�ɒ����Ĉ��S�o���邾���̑����h�����ɂ���Ɗ�����Ƃ܂��T�C�����g�ɂȂ�D
	 * @param infoAgentsArray
	 */
	public boolean linearThresholdMuzzlingWithRelief(InfoAgent[] infoAgentsArray, double reliefRatio) {
		boolean preSilent = (this.isSilent())? true : false;
		boolean tmpSilent = (this.isSilent())? true : false;
		
		int sumOfVocal = 0, sumOfVocalizedSameOpinion = 0;
		for (int neighborIndex : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[neighborIndex].getOpinion();
			if (neighborOp == null) continue; //�T�C�����g�͖���
			else {
				sumOfVocal++; //null�łȂ��Ȃ烔�H�[�J��
				if (neighborOp.equals(this.forceGetOpinion())) sumOfVocalizedSameOpinion++;
					//�����̐^�̈ӌ��Ɠ����ӌ��̐l�𐔂���
			}
		}
		if (sumOfVocal == 0) {
			this.tmpSilent = tmpSilent;
			return false;
		}
		if ((double)sumOfVocalizedSameOpinion / (double)sumOfVocal > this.bendThreshold &&
				(double)sumOfVocalizedSameOpinion / (double)sumOfVocal < reliefRatio) tmpSilent = false; //�ق��Ă��܂��قǏ��Ȃ����Ȃ����C���S�ł���قǑ������Ȃ��Ƃ��Ƀ��H�[�J���ɂȂ�
		else tmpSilent = true;
		
		this.tmpSilent = tmpSilent;
		if (preSilent ^ this.tmpSilent) return true;
		return false;
	}

	/**
	 * ���ݍ�p���N����Ȃ������ꍇ�ɌĂ΂��A�����_���Ȏ����ω��B<br>
	 * �v�̓m�C�Y���B���O��Ԃ����Ă����A�����_���ɃT�C�����g�����H�[�J���ɂ���B<br>
	 * @return
	 */
	public boolean randomUpdate(Random localRNG) {
		boolean ret = false;
		double roll = localRNG.nextDouble();
		if (roll < this.noiseRatio) {
			int rollInt = localRNG.nextInt(2);
			if (rollInt == 0) this.tmpSilent = true;
			else this.tmpSilent = false;
			ret = true;
		}
		return ret;
	}
	
	/**
	 * ��̔h���ŁD�����_���Ƀ��H�[�J���ɂ��邪�T�C�����g�ɂ��Ȃ��D
	 * @param localRNG
	 * @return
	 */
	public boolean randomUnmuzzle(Random localRNG) {
		boolean ret = false;
		double roll = localRNG.nextDouble();
		if (roll < this.noiseRatio) {
			this.tmpSilent = false;
			ret = false;
		}
		return ret;
	}
	
	//�����񖼂�^����X�^�C���͂�߂�.
	
	/**
	 * ��{�R���X�g���N�^�D
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 * @param isSilent -�T�C�����g�ł��邩
	 * @param ntwk -�Q�Ƃ���ÓI�l�b�g���[�N
	 */
	public InfoAgent(int index, Integer opinion, boolean isSilent, StaticNetwork ntwk) {
		this.setAgentIndex(index);
		if (ntwk == null) {
			this.isNetworkStatic = false;
			this.initFollowingList();
			this.initFollowedList();
		} else {
			this.isNetworkStatic = true;
			this.refNetwork = ntwk;
		}
		this.setOpinion(opinion);
		this.setTmpOpinion(this.forceGetOpinion());
		if (isSilent) this.muzzle();
	}
	
	/**
	 * ���ׂă��H�[�J���Ƃ��A�ÓI�l�b�g���[�N��^����R���X�g���N�^�B
	 * @param index
	 * @param opinion
	 * @param ntwk
	 */
	public InfoAgent(int index, Integer opinion, StaticNetwork ntwk) {
		this(index, opinion, false, ntwk);
	}
	
	/**
	 * ���ׂă��H�[�J���Ƃ��A�V�~�����[�V�����ʂ̃l�b�g���[�N���g�p����R���X�g���N�^�B
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 */
	public InfoAgent(int index, Integer opinion) {
		this(index, opinion, false, null);
	}
	
	/*
	 * �ȉ�getter/setter�y�т��̑��̃��\�b�h
	 * 
	 */
	
	
	/**���G�[�W�F���g�̐������ʔԍ����擾�D
	 * @return
	 */
	public int getAgentIndex() {
		return this.agentIndex;
	}
	/**���G�[�W�F���g�̐������ʔԍ����w��D
	 * @param agentIndex
	 */
	public void setAgentIndex(int agentIndex) {
		this.agentIndex = agentIndex;
	}
	
	/*
	 * �ȉ��C���X�g�֘A�̃��\�b�h�D ���������\�b�h��private�Ƃ���D
	 */	
	
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followingIndexList
	 */
	public List<Integer> getFollowingList() {
		return (this.isNetworkStatic)? this.refNetwork.getFollowingListOf(this.getAgentIndex()) : this.followingList;
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followingList �Z�b�g���� followingIndexList
	 */
	private void initFollowingList() {
		this.followingList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followingList �Z�b�g���� followingIndexList
	 */
	public void appendFollowingList(int index) {
		this.followingList.add(index);
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followedIndexList
	 */
	public List<Integer> getFollowedList() {
		return (this.isNetworkStatic)? this.refNetwork.getFollowedListOf(this.getAgentIndex()) : this.followedList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followedList �Z�b�g���� followedIndexList
	 */
	private void initFollowedList() {
		this.followedList = new ArrayList<Integer>();
	}
	/**
	 * ���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followedList �Z�b�g���� followedIndexList
	 */
	public void appendFollowedList(int index) {
		this.followedList.add(index);
	}

	
	/**
	 * �����N���Ώ̂ł���悤�Ȗ����l�b�g���[�N�̏ꍇ�͎Q�ƃ��X�g�Ɣ�Q�ƃ��X�g�ǂ���ɂ������ɒǉ������̂ŁC���̂��߂̃��\�b�h�D<br>
	 * @param index
	 */
	public void appendUndirectedList (int index) {
		this.appendFollowedList(index);
		this.appendFollowingList(index);
	}
	/**
	 * �����N���Ώ̂ł���悤�Ȗ����l�b�g���[�N�̏ꍇ�̃��X�g�擾���\�b�h�D<br>
	 * �ǉ����ɗ����ɒǉ�����Ă���͂��Ȃ̂ŁC�ǂ��炩����Ă���΂����D
	 * @param nameOrIndex
	 */
	public List<Integer> getUndirectedList () {
		return this.getFollowedList();
	}
	
	/**
	 * Collection.sort��p���āC2�̃��X�g���\�[�g����D<br>
	 * �l�b�g���[�N�����̌��ؗp�ł���C���ۂ̃V�~�����[�V�����ł͌ĂԕK�v�͂Ȃ��D
	 */
	public void sortLists () {
		Collections.sort(this.followedList);
		Collections.sort(this.followingList);
	}
	/**
	 * �Q�Ƃ��Ă���G�[�W�F���g�̐���Ԃ��B
	 * @return
	 */
	public int getnFollowing () {
		return (this.isNetworkStatic)? this.refNetwork.getnFollowedOf(this.getAgentIndex()) : this.followingList.size();
	}
	/**
	 * �Q�Ƃ���Ă���G�[�W�F���g�̐���Ԃ��B
	 * @return
	 */
	public int getnFollowed () {
		return (this.isNetworkStatic)? this.refNetwork.getnFollowedOf(this.getAgentIndex()) : this.followedList.size();
	}
	/**
	 * �����l�b�g���[�N�ł̐ڑ�������Ԃ��B
	 * @return
	 */
	public int getDegree () {
		return (this.isNetworkStatic)? this.refNetwork.getDegreeOf(this.getAgentIndex()) : this.followedList.size();
	}
	
	
	/**
	 * ���G�[�W�F���g���T�C�����g�ł����true�C���H�[�J���ł����false��Ԃ�<br>
	 * �f�t�H���g��false
	 * @return isSilent
	 */
	public boolean isSilent() {
		return this.isSilent;
	}
	/**
	 * ���G�[�W�F���g���T�C�����g�ɂ���B
	 */
	public void muzzle() {
		this.isSilent = true;
	}
	/**
	 * ���G�[�W�F���g�����H�[�J���ɂ���B
	 */
	public void unmuzzle() {
		this.isSilent = false;
	}
	/**
	 * �O������G�[�W�F���g�̌��݂̈ӌ����擾����B�V�~�����[�V�����p�B<br>
	 * �G�[�W�F���g���T�C�����g�ł���ꍇ�͎擾�ł��Ȃ��Bnull�l��Ԃ�
	 * @return opinion
	 */
	public Integer getOpinion() {
		if (this.isSilent()) return null;
		return this.forceGetOpinion();
	}
	/**
	 * �T�C�����g�@�����킸�ӌ����擾����B�L�^�p�B
	 * @return
	 */
	public Integer forceGetOpinion() {
		return this.opinion;
	}
	/**
	 * ���G�[�W�F���g�̈ӌ����w�肷��B
	 * @param opinion �Z�b�g���� opinion
	 */
	public void setOpinion(Integer opinion) {
		this.opinion = opinion;
	}
	/**
	 * ���Ԉӌ����m��ӌ��Ƃ��ēK�p����B�C�e���[�^�̍Ō�ɌĂԁB
	 */
	public void applyOpinion() {
		if (this.tmpOpinion != null) this.setOpinion(this.tmpOpinion);
	}
	/**
	 * ���Ԉӌ����擾����B�ӌ��ω����`�F�b�N���邽�߂Ɏg���B
	 */
	public Integer getTmpOpinion() {
		return this.tmpOpinion;
	}
	/**
	 * �ꎞ�I�Ɉӌ����i�[����B�C�e���[�^�̒��ԃf�[�^�̕ۑ��Ɏg���B
	 * @param tmpOp
	 */
	private void setTmpOpinion(Integer tmpOp) {
		this.tmpOpinion = tmpOp;
	}

	/**
	 * �h���V�~�����[�V�����̂��߂̃��\�b�h�D<br>
	 * {@link #linearThreasholdMuzzling(InfoAgent[])}�Ō��肵��muzzling�̒��ԏ�Ԃ�{�K�p����D
	 */
	public void applyMuzzling() {
		if (this.tmpSilent) this.muzzle();
		else this.unmuzzle();
	}
	
	/**
	 * �e���͂��擾����B�T�C�����g�Ȃ�-1��Ԃ��B����-1�Ƃ��������̓C�e���[�^���ŉe���͍ő�̃G�[�W�F���g��T���ۂ̔�r�Ɉ���������Ȃ����߂Ɏw�肳��Ă���B
	 * @return influence
	 */
	public double getInfluence() {
		return (this.isSilent())? -1 : this.influence;
	}

	/**
	 * �e���͂��w�肷��B
	 * @param influence �Z�b�g���� influence
	 */
	public void setInfluence(double influence) {
		this.influence = influence;
	}

	/**
	 * 臒l���擾����D
	 * @return threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * 臒l���w�肷��D臒l���ӌ��ɂ���ĕω�������V�~�����[�V�����ŗp����D
	 * @param threshold �Z�b�g���� threshold
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
