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
	private int Opinion;
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
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g���擾�D
	 * @return
	 */
	public ArrayList<String> getFollowingNameList() {
		return this.followingNameList;
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g����̃��X�g�ɏ������D
	 */
	public void initFollowingNameList() {
		this.followingNameList = new ArrayList<String>();
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̕����񖼃��X�g�ɐV���ȃG�[�W�F���g�̕����񖼂�ǉ��D
	 * @param name
	 */
	public void appendFollowingNameList(String name) {
		this.followingNameList.add(name);
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g���擾�D
	 * @return
	 */
	public ArrayList<String> getFollowedNameList() {
		return followedNameList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g����̃��X�g�ɏ������D
	 * 
	 */
	public void initFollowedNameList() {
		this.followedNameList = new ArrayList<String>();
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̕����񖼃��X�g�ɐV���ȃG�[�W�F���g�̕����񖼂�ǉ��D
	 * @param followedNameList
	 */
	public void appendFollowedNameList(String name) {
		this.followedNameList.add(name);
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followingIndexList
	 */
	public ArrayList<Integer> getFollowingIndexList() {
		return followingIndexList;
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followingIndexList �Z�b�g���� followingIndexList
	 */
	public void initFollowingIndexList() {
		this.followingIndexList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ��Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followingIndexList �Z�b�g���� followingIndexList
	 */
	public void initFollowingIndexList(int index) {
		this.followingIndexList.add(index);
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g���擾�B
	 * @return followedIndexList
	 */
	public ArrayList<Integer> getFollowedIndexList() {
		return followedIndexList;
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g����̃��X�g�ɏ������B
	 * @param followedIndexList �Z�b�g���� followedIndexList
	 */
	public void initFollowedIndexList() {
		this.followedIndexList = new ArrayList<Integer>();
	}
	/**���G�[�W�F���g���Q�Ƃ���Ă���G�[�W�F���g�̐����C���f�b�N�X���X�g�ɐV���ȃG�[�W�F���g�̐����C���f�b�N�X��ǉ��B
	 * @param followedIndexList �Z�b�g���� followedIndexList
	 */
	public void appendFollowedIndexList(int index) {
		this.followedIndexList.add(index);
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
	public int getOpinion() {
		if (!this.isSilent()) {
			return Opinion;
		} else {
			return (Integer)null;
		}
	}
	/**���G�[�W�F���g�̈ӌ����w�肷��B
	 * @param opinion �Z�b�g���� opinion
	 */
	public void setOpinion(int opinion) {
		Opinion = opinion;
	}
	
	

}
