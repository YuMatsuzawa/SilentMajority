package matz.basics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * �O������Q�Ɖ\�ȐÓI�l�b�g���[�N�}�b�v�𐶐����C�ێ�����N���X�D<br>
 * �G�[�W�F���g����int�ŗ^���ăR���X�g���N�g����D<br>
 * �l�b�g���[�N�}�b�v�𐶐�����build()���\�b�h����������D<br>
 * �l�b�g���[�N�}�b�v��ArrayList�̔z��ŊǗ�����D<br>
 * ArrayList�̔z�񂩂�ȉ���getter�Œl�⃊�X�g���擾�ł���D<br>
 * getUndirectedListOf(int),getFollowedListOf(int),getFollowingListOf(int),<br>
 * getDegreeOf(int),getnFollowedOf(int),getnFollowingOf(int)
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	protected static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	
	/**
	 * �ÓI�l�b�g���[�N��ێ�����ArrayList�̔z��B<br>
	 * ���̌^�̔z��Ȃ̂ň����ɒ��ӂ���D�Ӗ��_�I�Ɏg���₷���̂ł������Ă��邪�C�{�����܂���Ȃ��ق��������炵��<br>
	 */
	protected static List<Integer> networkList[][] = null;
	protected static boolean DIRECTED = true, UNDIRECTED = false; 
	protected static boolean orientation = UNDIRECTED;
	private static int nAgents;
	
	public abstract void build();
	
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
		StaticNetwork.nAgents = nAgents;
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
		StaticNetwork.orientation = orientation;
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

	public void dumpList(File outDir) {
		//�l�b�g���[�N�̃`�F�b�N
		if (!outDir.isDirectory()) outDir.mkdirs();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (int i = 0; i < this.getnAgents(); i++) {
				bw.write(i + "(" + this.getnFollowedOf(i) + ")\t:\t");
				for (Object neighbor : this.getUndirectedListOf(i)) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
