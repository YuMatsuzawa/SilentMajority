package matz.agentsim;

import java.util.ArrayList;
import java.util.Collections;

public class InfoAgent {
	
	private int agentIndex;
	private ArrayList<Integer> followingList;
	private ArrayList<Integer> followedList;
	private boolean isSilent = false;
	private Integer tmpOpinion;
	/**���ۓI�Ȉӌ��i���j��\����ԕϐ��D<br />
	 * �v���~�e�B�uint�ł͂Ȃ����b�p�^��Integer�ɂ��Ă����Cnull���g����悤�ɂ���D
	 */
	private Integer opinion;
	private double influence = Math.random();
	private double threshold = 0.5;
	
	/**�����������I�E���邢�͖���`�̏�Ԃɂ���Ƃ��C�m��I�E�ے�I��킸���炩�̐�i�I�ӌ��ɐG���ƁC����ɉe�������D<br />
	 * �e�����󂯂邩�ۂ��́C����̉e���͂̋����ɂ��D<br />
	 * �אڃ��X�g�ɂ���l�����ɎQ�Ƃ��Ă����A���H�[�J���Ȑl�̒��ōł��e���͂̍����l����e�������B
	 * @return �ω�����������true
	 */
	public boolean IndependentCascade(InfoAgent[] infoAgentsArray) {
		if (!(this.getOpinion() == null || this.getOpinion() == 0)) return false;

		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		double influence = -1;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion();
			double neighborInfluence = infoAgentsArray[(Integer) neighbor].getInfluence();
			try {
				if (preOp == null || neighborOp > preOp) {
					if (neighborInfluence > influence) {
						tmpOp = neighborOp;
						influence = neighborInfluence;
					}
				}
			} catch(Exception e) {
				//����Execption���A�T�C�����g�G�[�W�F���g���Q�Ƃ������̂��̂ł���igetOpinion��Null��Ԃ��Ă���̂�neighborOp��preOp�̔�r���ɗ�O�j
				continue;
			}
		}
		this.setTmpOpinion(tmpOp);
		if (this.getTmpOpinion() != preOp) return true;
		return false;
	}

	/**�אڂ��Ă���m�[�h�̒��ł̑����h��m�o���āC���̉e�����󂯂�D
	 * @param infoAgentsArray
	 * @return
	 */
	public boolean LinearThreashold(InfoAgent[] infoAgentsArray) {
		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		
		int sum = 0;
		Integer[] opinions = {0,0,0};
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion(); 
			if (neighborOp == null) continue;
			
			sum++;
			opinions[neighborOp]++;
		}
		if (sum == 0) return false;
		for (int opIndex = 0; opIndex < 3; opIndex++) {
			if (opinions[opIndex] / sum > this.threshold) tmpOp = opIndex;
		}
		this.setTmpOpinion(tmpOp);
		if (this.getTmpOpinion() != preOp) return true;
		return false;
	}
	
	/**���Ԉӌ����m��ӌ��Ƃ��ēK�p����B�C�e���[�^�̍Ō�ɌĂԁB
	 * 
	 */
	public void applyOpinion() {
		if (this.tmpOpinion != null) this.setOpinion(this.tmpOpinion);
	}
	/**���Ԉӌ����擾����B�ӌ��ω����`�F�b�N���邽�߂Ɏg���B
	 * 
	 */
	public Integer getTmpOpinion() {
		return this.tmpOpinion;
	}
	/**�ꎞ�I�Ɉӌ����i�[����B�C�e���[�^�̒��ԃf�[�^�̕ۑ��Ɏg���B
	 * @param tmpOp
	 */
	private void setTmpOpinion(Integer tmpOp) {
		this.tmpOpinion = tmpOp;
	}
	
	//�����񖼂�^����X�^�C���͂�߂�.
	
	/**�������ʔԍ���^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * ���X�g���S�Ď��ʔԍ��Ŏ�舵���D
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 * @param isSilent -�T�C�����g�ł��邩
	 */
	public InfoAgent(int index, Integer opinion, boolean isSilent) {
		this.setAgentIndex(index);
		this.initFollowingList();
		this.initFollowedList();
		this.setOpinion(opinion);
		this.setTmpOpinion(this.forceGetOpinion());
		if (isSilent) this.muzzle();	
	}
	
	/**�������ʔԍ���^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * �S�ăT�C�����g�łȂ��i���H�[�J���ł���j���̂Ƃ��Ă���D
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 */
	public InfoAgent(int index, Integer opinion) {
		this.setAgentIndex(index);
		this.initFollowingList();
		this.initFollowedList();
		this.setTmpOpinion(this.forceGetOpinion());
		this.setOpinion(opinion);
	}
	
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
	
	/**�ȉ��C���X�g�֘A�̃��\�b�h�D ���������\�b�h��private�Ƃ���D
	 */	
	
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followingIndexList
	 */
	public ArrayList<Integer> getFollowingList() {
		return this.followingList;
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
	public ArrayList<Integer> getFollowedList() {
		return this.followedList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followedList �Z�b�g���� followedIndexList
	 */
	private void initFollowedList() {
		this.followedList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followedList �Z�b�g���� followedIndexList
	 */
	public void appendFollowedList(int index) {
		this.followedList.add(index);
	}

	
	/**�����N���Ώ̂ł���悤�Ȗ����l�b�g���[�N�̏ꍇ�͎Q�ƃ��X�g�Ɣ�Q�ƃ��X�g�ǂ���ɂ������ɒǉ������̂ŁC���̂��߂̃��\�b�h�D<br />
	 * 
	 * @param index
	 */
	public void appendUndirectedList (int index) {
		this.appendFollowedList(index);
		this.appendFollowingList(index);
	}
	/**�����N���Ώ̂ł���悤�Ȗ����l�b�g���[�N�̏ꍇ�̃��X�g�擾���\�b�h�D<br />
	 * �ǉ����ɗ����ɒǉ�����Ă���͂��Ȃ̂ŁC�ǂ��炩����Ă���΂����D
	 * @param nameOrIndex
	 */
	public ArrayList<Integer> getUndirectedList () {
		return this.getFollowedList();
	}
	
	/**Collection.sort��p���āC2�̃��X�g���\�[�g����D<br />
	 * �l�b�g���[�N�����̌��ؗp�ł���C���ۂ̃V�~�����[�V�����ł͌ĂԕK�v�͂Ȃ��D
	 * 
	 */
	public void sortLists () {
		Collections.sort(this.followedList);
		Collections.sort(this.followingList);
	}
	/**�Q�Ƃ��Ă���G�[�W�F���g�̐���Ԃ��B
	 * 
	 * @return
	 */
	public int getnFollowing () {
		return this.followingList.size();
	}
	/**�Q�Ƃ���Ă���G�[�W�F���g�̐���Ԃ��B
	 * 
	 * @return
	 */
	public int getnFollowed () {
		return this.followedList.size();
	}
	/**�����l�b�g���[�N�ł̐ڑ�������Ԃ��B
	 * 
	 * @return
	 */
	public int getDegree () {
		return this.followedList.size();
	}
	
	
	/**���G�[�W�F���g���T�C�����g�ł����true�C���H�[�J���ł����false��Ԃ�<br />
	 * �f�t�H���g��false
	 * @return isSilent
	 */
	public boolean isSilent() {
		return isSilent;
	}
	/**���G�[�W�F���g���T�C�����g�ɂ���B
	 */
	public void muzzle() {
		this.isSilent = true;
	}
	/**���G�[�W�F���g�����H�[�J���ɂ���B
	 */
	public void unmuzzle() {
		this.isSilent = false;
	}
	/**���G�[�W�F���g�̌��݂̈ӌ����擾����B<br />
	 * �G�[�W�F���g���T�C�����g�ł���ꍇ�͎擾�ł��Ȃ��Bnull�l��Ԃ�
	 * @return opinion
	 */
	public Integer getOpinion() {
		if (!this.isSilent()) {
			return this.opinion;
		} else {
			return null;
		}
	}
	/**�T�C�����g�@�����킸�ӌ����擾����B
	 * @return
	 */
	public Integer forceGetOpinion() {
		return this.opinion;
	}
	/**���G�[�W�F���g�̈ӌ����w�肷��B
	 * @param opinion �Z�b�g���� opinion
	 */
	public void setOpinion(Integer opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return influence
	 */
	public double getInfluence() {
		return influence;
	}

	/**
	 * @param influence �Z�b�g���� influence
	 */
	public void setInfluence(double influence) {
		this.influence = influence;
	}
	
}
