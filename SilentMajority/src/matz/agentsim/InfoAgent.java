package matz.agentsim;

import java.util.ArrayList;

public class InfoAgent {
	
	private String agentName;
	private int agentIndex;
	private ArrayList<String> followingNameList;
	private ArrayList<String> followedNameList;
	private ArrayList<Integer> followingIndexList;
	private ArrayList<Integer> followedIndexList;
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
	
	

}
