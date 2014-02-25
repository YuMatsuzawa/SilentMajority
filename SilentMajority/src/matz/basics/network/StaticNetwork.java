package matz.basics.network;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import matz.basics.ScatterPlotGenerator;

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
	protected static Double DEGREE_DEFAULT = 6.0;
	
	/**
	 * �ÓI�l�b�g���[�N��ێ�����ArrayList�̔z��B<br>
	 * ���̌^�̔z��Ȃ̂ň����ɒ��ӂ���D�Ӗ��_�I�Ɏg���₷���̂ł������Ă��邪�C�{�����܂���Ȃ��ق��������炵��<br>
	 */
	protected List<Integer>[][] networkList = null;
	private String ntwkName;
	protected boolean orientation = UNDIRECTED;
	protected int nAgents;
	private double givenDegree;
	protected boolean degreeGiven = false;

	protected TreeMap<Integer, Integer> nFollowedFreqMap = new TreeMap<Integer,Integer>(); //TreeMap��Key�������ɏ����t������̂ŁA
	protected TreeMap<Integer, Integer> nFollowingFreqMap = new TreeMap<Integer,Integer>();
	
	public abstract void build();
	/**
	 * ��{�R���X�g���N�^.�G�[�W�F���g����^���Ȃ��R���X�g���N�^�͍��Ȃ�.<br>
	 * 
	 * @param nAgents
	 * @param orientation - �L���Ȃ�true,�����Ȃ�false
	 */
	@SuppressWarnings("unchecked")
	public StaticNetwork(String ntwkName, int nAgents, boolean orientation, Double degree) {
		this.setNtwkName(ntwkName);
		this.setnAgents(nAgents);
		this.setOrientation(orientation);
		this.networkList = new ArrayList[nAgents][2];
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) this.networkList[i][j] = new ArrayList<Integer>();
		}
		if (degree == null) this.degreeGiven = false;
		else {
			this.degreeGiven = true;
			this.setGivenDegree(degree);
		}
	}
	
	/**
	 * �G�[�W�F���g���̂ݗ^���Ė����O���t�����R���X�g���N�^�B
	 * @param nAgents
	 */
	public StaticNetwork(String ntwkName, int nAgents) {
		this(ntwkName, nAgents, UNDIRECTED, null);
	}
	
	// TODO �l�b�g���[�N�f�[�^���擾�ł���ꍇ�C��������ɃR���X�g���N�g�ł���悤�Ȏ���
	
	/**
	 * @return ntwkName
	 */
	public String getNtwkName() {
		return ntwkName;
	}
	/**
	 * @param ntwkName �Z�b�g���� ntwkName
	 */
	public void setNtwkName(String ntwkName) {
		this.ntwkName = ntwkName;
	}
	/**
	 * �G�[�W�F���g�����擾����B
	 * @return nAgents
	 */
	public int getnAgents() {
		return this.nAgents;
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
		return this.orientation;
	}

	/**
	 * �l�b�g���[�N�̎w�������w��
	 * @param orientation �Z�b�g���� orientation
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return givenDegree
	 */
	public double getGivenDegree() {
		return givenDegree;
	}
	/**
	 * @param givenDegree �Z�b�g���� givenDegree
	 */
	public void setGivenDegree(Double givenDegree) {
		this.givenDegree = givenDegree;
	}
	/**
	 * subject�̔�Q�ƃ��X�g��object��ǉ��B
	 * @param subject
	 * @param object
	 */
	public void appendFollowedListOf(int subject, int object) {
		this.networkList[subject][FOLLOWED_INDEX].add(object);
	}
	/**
	 * subject�̎Q�ƃ��X�g��object��ǉ��B
	 * @param subject
	 * @param object
	 */
	public void appendFollowingListOf(int subject, int object) {
		this.networkList[subject][FOLLOWING_INDEX].add(object);
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
		return this.networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * �L���O���t�ɂ�����Q�ƃ��X�g��Ԃ�.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowingListOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX];
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
	
	public void countDegreeFreq() {
		for (int i = 0; i < this.getnAgents(); i++) {
			int nFollowed = this.getnFollowedOf(i), nFollowing = this.getnFollowingOf(i);
			if (this.nFollowedFreqMap.containsKey(nFollowed)) {
				int val = this.nFollowedFreqMap.get(nFollowed);
				this.nFollowedFreqMap.put(nFollowed, ++val);
			} else this.nFollowedFreqMap.put(nFollowed, 1);
			if (this.nFollowingFreqMap.containsKey(nFollowing)) {
				int val = this.nFollowingFreqMap.get(nFollowing);
				this.nFollowingFreqMap.put(nFollowing, ++val);
			} else this.nFollowingFreqMap.put(nFollowing, 1);
		}
	}
	
	public double getAvgDegree() {
		double avgDegree = 0.0;
		for (Entry<Integer,Integer> entry : this.getnFollowedFreq().entrySet()) {
			avgDegree += (double)entry.getKey() * (double)entry.getValue() / (double)this.getnAgents();
		}
		return avgDegree;
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
	
	/**
	 * �`�F�b�N�̂��߂Ƀl�b�g���[�N�̏����t�@�C����摜�ɏo�͂���D
	 * @param outDir
	 */
	public void dumpNetwork(File outDir) {
		if (!outDir.isDirectory()) outDir.mkdirs();
		
		//�S�G�[�W�F���g�̗אڃ��X�g���\�[�g����B�R�����g�A�E�g���Ă��܂��Ă������B
		for (List<Integer>[] agentLists : networkList) {
			Collections.sort(agentLists[FOLLOWED_INDEX]);
			Collections.sort(agentLists[FOLLOWING_INDEX]);
		}
		//�l�b�g���[�N�̓��v�I�������`�F�b�N����B
		this.countDegreeFreq(); //�����̕p�x���z
		
		try {
			//�אڃ��X�g�f���o��
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (int i = 0; i < this.getnAgents(); i++) {
				bw.write(i + "(" + this.getnFollowedOf(i) + ")\t:\t");
				for (Object neighbor : this.getUndirectedListOf(i)) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			bw.close();
			
			//�p�x���z�f���o��
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwkDegreeFreq.csv"))));
			for (Entry<Integer,Integer> entry : this.nFollowedFreqMap.entrySet()) {
				bw2.write(entry.getKey() + "," + entry.getValue());
				bw2.newLine();
			}
			bw2.close();
			ScatterPlotGenerator spg = new ScatterPlotGenerator(
					this.getNtwkName() + 
					",N=" + this.getnAgents() + 
					",Avg_D=" + String.format("%.2f", this.getAvgDegree()) ,this.nFollowedFreqMap);
			spg.generateGraph(outDir, "ntwkDegreeFreq.png");
	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
