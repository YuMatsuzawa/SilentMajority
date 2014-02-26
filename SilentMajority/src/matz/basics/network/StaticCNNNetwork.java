package matz.basics.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Vazquez��CNN���f���Ɋ�Â��A������SNS�ł̗F�l�֌W�ɋ߂��l�b�g���[�N�𐶐�����N���X�B<br>
 * �m��p_nn�Łu�F�l�̗F�l�v�Ƃ����֌W(���݃����N)�������_���ɑI��Őڑ����鑀��A<br>
 * �m��1-p_nn�ŐV�����G�[�W�F���g�������_���ɒǉ����鑀����s���A���S���Y���B<br>
 * p_nn�̓p�����[�^�ł��邪�A�^�����Ă��Ȃ��ꍇ�̓f�t�H���g�l{@value #P_NN_DEFAULT}��p����B<br>
 * �A���S���Y������A�m��p_nn�ŃG�[�W�F���g����1�����A�����̑��a��2���������A<br>
 * 1-p_nn�Ŏ����̑��a������2�����邱�Ƃ��킩��B<br>
 * �]���ē���̎���degree�ɑQ�߂���l�b�g���[�N�ɂ������ꍇ�́A{@code p_nn = 1 - 2/degree}�Ƃ���Ηǂ��B<br>
 * �������A�G�[�W�F���g�����\���ɑ傫���Ȃ��ꍇ�͕K�������ǂ��ߎ��ƂȂ�Ȃ��B
 * 
 * @author Yu
 *
 */
public class StaticCNNNetwork extends StaticNetwork {
	
	private double p_nn;
	private static final double P_NN_DEFAULT = 0.666667;
	//private static final double P_NN_DEFAULT = 0.75;
	private List<Integer[]> potentialLinks = new ArrayList<Integer[]>();
	private int includedAgents = 0;

	@Override
	public void build() {
		//�Ƃ肠���������O���t
		if (this.getOrientation() == UNDIRECTED) {
			//�l�b�g���[�N�̎�����
			this.constructLink(0, 2);
			this.constructLink(1, 2);
			this.includedAgents = 3;
			
			//�w�肳�ꂽ���̃G�[�W�F���g����Ȃ�l�b�g���[�N���o����܂ŃC�e���[�g
			while(this.includedAgents < this.getnAgents()) {
				double roll = this.localRNG.nextDouble();
				if (roll < this.p_nn) {
					this.connectPotential();
				} else {
					this.includeAgent();
				}
			}
		}
	}

	/**
	 * �|�e���V���������N�������N�ɂ���B
	 */
	private void connectPotential() {
		int listSize = this.potentialLinks.size();
		if (listSize == 0) return;
		
		int roll = this.localRNG.nextInt(listSize);
		Integer[] pLink = this.potentialLinks.get(roll);
		this.potentialLinks.remove(roll);
			//roll�Ń����_���ȃ|�e���V���������N��I�яo��
		this.constructLink(pLink[0], pLink[1]);
			//������G�b�W�ɕϊ�
		
	}

	/**
	 * �V�K�G�[�W�F���g�������_���ɉ�����B
	 */
	private void includeAgent() {
		int target = this.localRNG.nextInt(this.includedAgents);
		int newcomer = this.includedAgents++;
		this.constructLink(newcomer, target);
	}

	/**
	 * subject��object�̊ԂɃ����N�𒣂�A������|�e���V���������N��o�^����B<br>
	 * ��d�o�^���Ȃ��悤�ɍׂ����`�F�b�N����<br>
	 * �݂����݂��̃��X�g�ɘR�ꖳ���o�^����̂ŁA�������t�ɂ��Ă��̃��\�b�h��2��ĂԕK�v�͂Ȃ�
	 * @param subject
	 * @param object
	 */
	private void constructLink(int subject, int object) {
		this.appendToUndirectedListOf(subject, object);
		this.appendToUndirectedListOf(object, subject);
		this.safeAppendPotentialLink(subject, object);
		this.safeAppendPotentialLink(object, subject);
	}

	/**
	 * �d���Ȃ��悤�m�F���Ȃ���|�e���V���������N��ǉ��B<br>
	 * subject���猩���ꍇ�ƁAobject���猩���ꍇ�A�����̃|�e���V���������N��o�^���邽�߂ɁA<br>
	 * �������t�ɂ���2��ĂԕK�v������B
	 * @param subject
	 * @param object
	 */
	private void safeAppendPotentialLink(int subject, int object) {
		for(int pIndex : this.getUndirectedListOf(object)) { 
			if (pIndex != subject &&
				!this.getUndirectedListOf(subject).contains(pIndex)) {
				Integer[] pLink = {pIndex, subject};
				Integer[] rLink = {subject, pIndex};
				boolean isNew = true;
				for(Integer[] link : this.potentialLinks) {
					if (Arrays.equals(pLink, link) ||
							Arrays.equals(rLink, link)) { //�������Ȃ����Ƃ��m�F���A
						isNew = false;
						break;
					}
				}
				if (isNew) this.potentialLinks.add(pLink); //����������o�^�B
			}
		}
	}
	
	/**
	 * ����̑I�𗦂�^�����{�R���X�g���N�^�B
	 * @param nAgents -�G�[�W�F���g��
	 * @param p_nn -�|�e���V���������N�ڑ��̑I��
	 * @param orientation -�w����
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation, double p_nn) {
		super("CNN", nAgents, orientation, null);
		this.p_nn = p_nn;
		this.build();
	}
	
	/**
	 * �ڕW���ώ�����^�����{�R���X�g���N�^�B
	 * @param nAgents
	 * @param orientation
	 * @param degree
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation, Double degree) {
		super("CNN", nAgents, orientation, degree);
		this.p_nn = 1.0 - 2.0 / this.getGivenDegree();
		this.build();
	}
	/**
	 * �f�t�H���g��p_nn��p����R���X�g���N�^�B
	 * @param nAgents
	 * @param orientation
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation) {
		this(nAgents, orientation, P_NN_DEFAULT);
	}
	
	public StaticCNNNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	/**
	 * �G�[�W�F���g����^���Ė����O���t�����R���X�g���N�^�B
	 * @param nAgents
	 */
	public StaticCNNNetwork(int nAgents) {
		this(nAgents, null);
	}
}
