package matz.basics.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class StaticCNNNetwork extends StaticNetwork {
	
	private double p_nn;
	//private static final double P_NN_DEFAULT = 0.666667;
	private static final double P_NN_DEFAULT = 0.65;
	private List<Integer[]> potentialLinks = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
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
		this.appendUndirectedListOf(subject, object);
		this.appendUndirectedListOf(object, subject);
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
	 * ��{�R���X�g���N�^�B
	 * @param nAgents -�G�[�W�F���g��
	 * @param p_nn -�|�e���V���������N�ڑ��̑I��
	 * @param orientation -�w����
	 */
	public StaticCNNNetwork(int nAgents, double p_nn, boolean orientation, Double degree) {
		super("CNN", nAgents, orientation, degree);
		this.p_nn = p_nn;
		this.build();
	}
	
	public StaticCNNNetwork(int nAgents, Double degree) {
		this(nAgents, P_NN_DEFAULT, UNDIRECTED, degree);
	}
	
	/**
	 * �G�[�W�F���g����^���Ė����O���t�����R���X�g���N�^�B
	 * @param nAgents
	 */
	public StaticCNNNetwork(int nAgents) {
		this(nAgents, null);
	}
}
