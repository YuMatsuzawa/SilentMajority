package matz.basics;

import java.util.ArrayList;

/**
 * �O������Q�Ɖ\�ȐÓI�l�b�g���[�N�}�b�v�𐶐����C�ێ�����N���X�D<br>
 * �G�[�W�F���g����int�ŗ^���ăR���X�g���N�g����D<br>
 * �l�b�g���[�N�}�b�v�𐶐�����build(int)���\�b�h����������D<br>
 * �l�b�g���[�N�}�b�v��ArrayList�ŊǗ�����D<br>
 * ArrayList����͈ȉ���getter�Œl�⃊�X�g���擾�ł���D<br>
 * getUndirectedListOf(int),getFollowedListOf(int),getFollowingListOf(int),<br>
 * getDegreeOf(int),getnFollowedOf(int),getnFollowingOf(int)
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	
	private ArrayList<Integer> networkList[][] = null;
	
	public abstract void build();
	
	/**
	 * �G�[�W�F���g����^����R���X�g���N�^.�G�[�W�F���g����^���Ȃ��R���X�g���N�^�͂Ȃ�.
	 * @param nAgents
	 */
	public StaticNetwork(int nAgents) {
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) networkList[i][j] = new ArrayList<Integer>();
		}
	}
	
	// TODO �l�b�g���[�N�f�[�^���擾�ł���ꍇ�C��������ɃR���X�g���N�g�ł���悤�Ȏ���
	
	/**
	 * �L���O���t�ɂ������Q�ƃ��X�g��Ԃ�.
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getFollowedListOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * �L���O���t�ɂ�����Q�ƃ��X�g��Ԃ�.
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getFollowingListOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX];
	}
	
	/**
	 * �����O���t�ɂ�����אڃ��X�g��Ԃ��D�����I�ɂ͗L���O���t�̔�Q�ƃ��X�g�Ɠ������̂�Ԃ��D
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getUndirectedListOf(int index) {
		return this.getFollowedListOf(index);
	}
	
	/**
	 * �L���O���t�ɂ������Q�Ɛ��i�������j��Ԃ��D
	 * @param index
	 * @return
	 */
	public int getnFollowedOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX].size();
	}
	
	/**
	 * �L���O���t�ɂ�����Q�Ɛ��i�o�����j��Ԃ��D
	 * @param index
	 * @return
	 */
	public int getnFollowingOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX].size();
	}
	
	/**
	 * �����O���t�ɂ����鎟����Ԃ��D�����I�ɂ͗L���O���t�̔�Q�Ɛ��Ɠ������̂�Ԃ��D
	 * @param index
	 * @return
	 */
	public int getDegreeOf(int index) {
		return this.getnFollowedOf(index);
	}
}
