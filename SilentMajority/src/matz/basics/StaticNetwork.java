package matz.basics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * �O������Q�Ɖ\�ȐÓI�l�b�g���[�N�}�b�v�𐶐����C�ێ�����N���X�D<br>
 * �G�[�W�F���g����int�ŗ^���ăR���X�g���N�g����D<br>
 * �l�b�g���[�N�}�b�v�𐶐�����build()���\�b�h�ƁA<br>
 * �l�b�g���[�N�̊e��f�[�^���t�@�C���E�摜���ɏ����o��dumpNetwork()����������D<br>
 * �l�b�g���[�N�}�b�v��ArrayList�̔z��ŊǗ�����D<br>
 * ArrayList�̔z�񂩂�ȉ���getter�Œl�⃊�X�g���擾�ł���D<br>
 * getUndirectedListOf(int),getFollowedListOf(int),getFollowingListOf(int),<br>
 * getDegreeOf(int),getnFollowedOf(int),getnFollowingOf(int)
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	protected static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	protected static boolean DIRECTED = true, UNDIRECTED = false; 
	
	/**
	 * �ÓI�l�b�g���[�N��ێ�����ArrayList�̔z��B<br>
	 * ���̌^�̔z��Ȃ̂ň����ɒ��ӂ���D�Ӗ��_�I�Ɏg���₷���̂ł������Ă��邪�C�{�����܂���Ȃ��ق��������炵��<br>
	 */
	protected List<Integer> networkList[][] = null;
	protected boolean orientation = UNDIRECTED;
	private int nAgents;

	protected TreeMap<Integer, Integer> nFollowedFreqMap = new TreeMap<Integer,Integer>(); //TreeMap��Key�������ɏ����t������̂ŁA
	protected TreeMap<Integer, Integer> nFollowingFreqMap = new TreeMap<Integer,Integer>();
	
	public abstract void build();
	public abstract void dumpNetwork(File outDir);
	
	/**
	 * �G�[�W�F���g���Ǝw������^����R���X�g���N�^.�G�[�W�F���g����^���Ȃ��R���X�g���N�^�͂Ȃ�.<br>
	 * 
	 * @param nAgents
	 * @param orientation - �L���Ȃ�true,�����Ȃ�false
	 */
	@SuppressWarnings("unchecked")
	public StaticNetwork(int nAgents, boolean orientation) {
		this.setnAgents(nAgents);
		this.setOrientation(orientation);
		networkList = new ArrayList[nAgents][2];
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) networkList[i][j] = new ArrayList<Integer>();
		}
	}
	
	/**
	 * �G�[�W�F���g���̂ݗ^���Ė����O���t�����R���X�g���N�^�B
	 * @param nAgents
	 */
	public StaticNetwork(int nAgents) {
		this(nAgents, UNDIRECTED);
	}
	
	// TODO �l�b�g���[�N�f�[�^���擾�ł���ꍇ�C��������ɃR���X�g���N�g�ł���悤�Ȏ���
	
	/**
	 * �G�[�W�F���g�����擾����B
	 * @return nAgents
	 */
	public int getnAgents() {
		return nAgents;
	}

	/**
	 * �G�[�W�F���g�����w�肷��B
	 * @param nAgents �Z�b�g���� nAgents
	 */
	public void setnAgents(int nAgents) {
		this.nAgents = nAgents;
	}

	/**
	 * �l�b�g���[�N�̎w�������擾�B
	 * @return orientation
	 */
	public boolean getOrientation() {
		return orientation;
	}

	/**
	 * �l�b�g���[�N�̎w�������w��
	 * @param orientation �Z�b�g���� orientation
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

	/**
	 * subject�̔�Q�ƃ��X�g��object��ǉ��B
	 * @param subject
	 * @param object
	 */
	public void appendFollowedListOf(int subject, int object) {
		networkList[subject][FOLLOWED_INDEX].add(object);
	}
	/**
	 * subject�̎Q�ƃ��X�g��object��ǉ��B
	 * @param subject
	 * @param object
	 */
	public void appendFollowingListOf(int subject, int object) {
		networkList[subject][FOLLOWING_INDEX].add(object);
	}
	/**
	 * �����O���t�ŁAsubject�̗������̃��X�g��object��ǉ��B
	 * @param subject
	 * @param object
	 */
	public void appendUndirectedListOf(int subject, int object) {
		this.appendFollowedListOf(subject, object);
		this.appendFollowingListOf(subject, object);
	}
	/**
	 * �L���O���t�ɂ������Q�ƃ��X�g��Ԃ�.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowedListOf(int index) {
		return networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * �L���O���t�ɂ�����Q�ƃ��X�g��Ԃ�.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowingListOf(int index) {
		return networkList[index][FOLLOWING_INDEX];
	}
	
	/**
	 * �����O���t�ɂ�����אڃ��X�g��Ԃ��D�����I�ɂ͗L���O���t�̔�Q�ƃ��X�g�Ɠ������̂�Ԃ��D
	 * @param index
	 * @return
	 */
	public List<Integer> getUndirectedListOf(int index) {
		return this.getFollowedListOf(index);
	}
	
	/**
	 * �L���O���t�ɂ������Q�Ɛ��i�������j��Ԃ��D
	 * @param index
	 * @return
	 */
	public int getnFollowedOf(int index) {
		return networkList[index][FOLLOWED_INDEX].size();
	}
	
	/**
	 * �L���O���t�ɂ�����Q�Ɛ��i�o�����j��Ԃ��D
	 * @param index
	 * @return
	 */
	public int getnFollowingOf(int index) {
		return networkList[index][FOLLOWING_INDEX].size();
	}
	
	/**
	 * �����O���t�ɂ����鎟����Ԃ��D�����I�ɂ͗L���O���t�̔�Q�Ɛ��Ɠ������̂�Ԃ��D
	 * @param index
	 * @return
	 */
	public int getDegreeOf(int index) {
		return this.getnFollowedOf(index);
	}
	
	public void countDegreeFreq() {
		for (int i = 0; i < this.getnAgents(); i++) {
			int nFollowed = this.getnFollowedOf(i), nFollowing = this.getnFollowingOf(i);
			if (nFollowedFreqMap.containsKey(nFollowed)) {
				int val = nFollowedFreqMap.get(nFollowed);
				nFollowedFreqMap.put(nFollowed, ++val);
			} else nFollowedFreqMap.put(nFollowed, 1);
			if (nFollowingFreqMap.containsKey(nFollowing)) {
				int val = nFollowingFreqMap.get(nFollowing);
				nFollowingFreqMap.put(nFollowing, ++val);
			} else nFollowingFreqMap.put(nFollowing, 1);
		}
	}
	public TreeMap<Integer,Integer> getnFollowedFreq() {
		return this.nFollowedFreqMap;
	}
	
	public TreeMap<Integer,Integer> getnFollowingFreq() {
		return this.nFollowingFreqMap;
	}
	
	public TreeMap<Integer,Integer> getDegreeFreq() {
		return this.getnFollowedFreq();
	}
	
	public int getnFollowedFreqOf(int index) {
		return this.nFollowedFreqMap.get(this.getnFollowedOf(index));
	}
	
	public int getnFollowingFreqOf(int index) {
		return this.nFollowingFreqMap.get(this.getnFollowingOf(index));
	}
	
	public int getDegreeFreqOf(int index) {
		return this.getnFollowedFreqOf(index);
	}

}
