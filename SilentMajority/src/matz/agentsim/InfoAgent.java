package matz.agentsim;

import java.util.ArrayList;

public class InfoAgent {
	
	private String agentName;
	private int agentIndex;
	private ArrayList<String> followingNameList;
	private ArrayList<String> followedNameList;
	private ArrayList<Integer> followingIndexList;
	private ArrayList<Integer> followedIndexList;
	private boolean isSilent = false;
	private Integer opinion;
	private double influence = Math.random();
	private double threshold = 0.5;
	private static final int NAME_BASED = 0;
	private static final int INDEX_BASED = 1;
	private int style;
	
	/**�����������I�E���邢�͖���`�̏�Ԃɂ���Ƃ��C�m��I�E�ے�I��킸���炩�̐�i�I�ӌ��ɐG���ƁC����ɉe�������D<br />
	 * �e�����󂯂邩�ۂ��́C����̉e���͂̋����ɂ��D
	 */
	public void IndependentCascade(InfoAgent[] infoAgentsArray) {
		if (!(this.getOpinion() == 0 || this.getOpinion() == null)) return;

		Integer tmpOp = this.getOpinion();
		for (Object neighbor : this.getIndirectedList()) {
			//TODO �Ƃ肠����Index�x�[�X
			double influence = -1;
			try {
				if (this.getOpinion() == null || infoAgentsArray[(Integer) neighbor].getOpinion() > this.getOpinion()) {
					if (infoAgentsArray[(Integer) neighbor].getInfluence() > influence) {
						tmpOp = infoAgentsArray[(Integer) neighbor].getOpinion();
						influence = infoAgentsArray[(Integer) neighbor].getInfluence();
					}
				}
			} catch(Exception e) {
				continue;
			}
		}
		this.setOpinion(tmpOp);
	}
	
	/**�אڂ��Ă���m�[�h�̒��ł̑����h��m�o���āC���̉e�����󂯂�D
	 * @param infoAgentsArray
	 */
	public void LinearThreashold(InfoAgent[] infoAgentsArray) {
		
	}
	
	/**�����񖼂�^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * ���X�g���S�ĕ����񖼂Ŏ�舵���D
	 * @param name -������̖��O
	 * @param opinion -�����l�̈ӌ�
	 * @param isSilent -�T�C�����g�ł��邩
	 */
	public InfoAgent(String name, int opinion, boolean isSilent) {
		this.setStyle(NAME_BASED);
		this.setAgentName(name);
		this.initFollowingNameList();
		this.initFollowedNameList();
		this.setOpinion(opinion);
		if (isSilent) this.muzzle();
	}
	
	/**�����񖼂�^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * �S�ăT�C�����g�łȂ��i���H�[�J���ł���j���̂Ƃ��Ă���D
	 * @param name
	 * @param opinion
	 */
	public InfoAgent(String name, int opinion) {
		this.setStyle(NAME_BASED);
		this.setAgentName(name);
		this.initFollowingNameList();
		this.initFollowedNameList();
		this.setOpinion(opinion);	
	}
	
	/**�������ʔԍ���^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * ���X�g���S�Ď��ʔԍ��Ŏ�舵���D
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 * @param isSilent -�T�C�����g�ł��邩
	 */
	public InfoAgent(int index, int opinion, boolean isSilent) {
		this.setStyle(INDEX_BASED);
		this.setAgentIndex(index);
		this.initFollowingIndexList();
		this.initFollowedIndexList();
		this.setOpinion(opinion);
		if (isSilent) this.muzzle();	
	}
	
	/**�������ʔԍ���^���ď��G�[�W�F���g������������R���X�g���N�^�D
	 * �S�ăT�C�����g�łȂ��i���H�[�J���ł���j���̂Ƃ��Ă���D
	 * @param index -�����̎��ʔԍ�
	 * @param opinion -�����l�̈ӌ�
	 */
	public InfoAgent(int index, int opinion) {
		this.setStyle(INDEX_BASED);
		this.setAgentIndex(index);
		this.initFollowingIndexList();
		this.initFollowedIndexList();
		this.setOpinion(opinion);
	}
	
	/**���G�[�W�F���g�̕����񖼂��擾�D
	 * @return
	 */
	public String getAgentName() {
		return this.agentName;
	}
	/**���G�[�W�F���g�̕����񖼂��w��D
	 * 
	 * @param agentName
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
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
	
	/**�ȉ��C���X�g�֘A�̃��\�b�h�D
	 * �������C�����̃��\�b�h��private�Ƃ��C�����ł����g��Ȃ��D
	 * �Ō�ɂ���^���������肷��悤�ɂ������\�b�h�Q�̂�public�ɂ���D
	 * ���������\�b�h��private�Ƃ���D
	 */	
	
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g���擾�D
	 * @return
	 */
	private ArrayList<String> getFollowingNameList() {
		return this.followingNameList;
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g����̃��X�g�ɏ������D
	 */
	private void initFollowingNameList() {
		this.followingNameList = new ArrayList<String>();
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g�ɐV���ȃG�[�W�F���g�̕����񖼂�ǉ��D
	 * @param name
	 */
	private void appendFollowingNameList(String name) {
		this.followingNameList.add(name);
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g���擾�D
	 * @return
	 */
	private ArrayList<String> getFollowedNameList() {
		return followedNameList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g����̃��X�g�ɏ������D
	 * 
	 */
	private void initFollowedNameList() {
		this.followedNameList = new ArrayList<String>();
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g�ɐV���ȃG�[�W�F���g�̕����񖼂�ǉ��D
	 * @param followedNameList
	 */
	private void appendFollowedNameList(String name) {
		this.followedNameList.add(name);
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followingIndexList
	 */
	private ArrayList<Integer> getFollowingIndexList() {
		return followingIndexList;
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followingIndexList �Z�b�g���� followingIndexList
	 */
	private void initFollowingIndexList() {
		this.followingIndexList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followingIndexList �Z�b�g���� followingIndexList
	 */
	private void appendFollowingIndexList(int index) {
		this.followingIndexList.add(index);
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followedIndexList
	 */
	private ArrayList<Integer> getFollowedIndexList() {
		return followedIndexList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followedIndexList �Z�b�g���� followedIndexList
	 */
	private void initFollowedIndexList() {
		this.followedIndexList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followedIndexList �Z�b�g���� followedIndexList
	 */
	private void appendFollowedIndexList(int index) {
		this.followedIndexList.add(index);
	}


	/**���G�[�W�F���g�̒�`�ɏ]���C�K�؂ȃX�^�C���̎Q�ƃ��X�g��Ԃ��D
	 * @return 
	 */
	public ArrayList<?> getFollowingList() {
		if (this.getStyle() == NAME_BASED) {
			return this.getFollowingNameList();
		}
		return this.getFollowingIndexList();
	}
	/**���G�[�W�F���g�̒�`�ɏ]���C�K�؂ȃX�^�C���̔�Q�ƃ��X�g��Ԃ��D
	 * @return 
	 */
	public ArrayList<?> getFollowedList() {
		if (this.getStyle() == NAME_BASED) {
			return this.getFollowedNameList();
		}
		return this.getFollowedIndexList();
	}
	/**���G�[�W�F���g�̒�`�ɏ]���C�K�؂ȃX�^�C���̎Q�ƃ��X�g�ɐV�K�G�[�W�F���g��ǉ��D
	 * String��Integer�ň�����^����D
	 * @param nameOrIndex -String��Integer�̒l
	 */
	public <SorI> void appendFolowingList (SorI nameOrIndex) {
		if (this.getStyle() == NAME_BASED) {
			this.appendFollowingNameList((String) nameOrIndex);
		} else {
			this.appendFollowingIndexList((Integer) nameOrIndex);
		}
	}
	/**���G�[�W�F���g�̒�`�ɏ]���C�K�؂ȃX�^�C���̔�Q�ƃ��X�g�ɐV�K�G�[�W�F���g��ǉ��D
	 * String��Integer�ň�����^����D
	 * @param nameOrIndex -String��Integer�̒l
	 */
	public <SorI> void appendFolowedList (SorI nameOrIndex) {
		if (this.getStyle() == NAME_BASED) {
			this.appendFollowedNameList((String) nameOrIndex);
		} else {
			this.appendFollowedIndexList((Integer) nameOrIndex);
		}
	}
	/**�����N���Ώۂł���悤�Ȗ������l�b�g���[�N�̏ꍇ�͎Q�ƃ��X�g�Ɣ�Q�ƃ��X�g�ǂ���ɂ������ɒǉ������̂ŁC���̂��߂̃��\�b�h�D
	 * @param nameOrIndex
	 */
	public <SorI> void appendIndirectedList (SorI nameOrIndex) {
		if (this.getStyle() == NAME_BASED) {
			this.appendFollowedNameList((String) nameOrIndex);
			this.appendFollowingNameList((String) nameOrIndex);
		} else {
			this.appendFollowedIndexList((Integer) nameOrIndex);
			this.appendFollowingIndexList((Integer) nameOrIndex);
		}
	}
	/**�����N���Ώۂł���悤�Ȗ������l�b�g���[�N�̏ꍇ�̃��X�g�擾���\�b�h�D
	 * �ǉ����ɗ����ɒǉ�����Ă���͂��Ȃ̂ŁC�ǂ��炩����Ă���΂����D
	 * @param nameOrIndex
	 */
	public ArrayList<?> getIndirectedList () {
		if (this.getStyle() == NAME_BASED) {
			return this.getFollowedNameList();
		}
		return this.getFollowedIndexList();
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
	/**���G�[�W�F���g�̈ӌ����w�肷��B
	 * @param opinion �Z�b�g���� opinion
	 */
	public void setOpinion(int opinion) {
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

	/**���G�[�W�F���g�������񖼃x�[�X���������ʔԍ��x�[�X���ǂ���ŊǗ�����Ă��邩�擾����D
	 * ������x�[�X�Ȃ��0�C�����x�[�X�Ȃ�1��Ԃ��D
	 * @return nameOrIndex
	 */
	public int getStyle() {
		return style;
	}

	/**�R���X�g���N�^�ŌĂԁD
	 * ������x�[�X�̃R���X�g���N�^�ł�NAME_BASED�������ɓ����D
	 * �����x�[�X�̃R���X�g���N�^�ł�INDEX_BASED�������ɓ����D
	 * @param nameOrIndex �Z�b�g���� nameOrIndex
	 */
	public void setStyle(int nameOrIndex) {
		this.style = nameOrIndex;
	}
	
}
