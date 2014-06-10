package matz.basics.network;

import java.util.ArrayList;

/**
 * Erdos-Renyi���f����G(n,p)�����Ɋ�Â��A�����_���l�b�g���[�N�𐶐�����N���X�B<br>
 * ���̕����ł̓G�b�W�̗L�����m��p��^����K�v�����邪�A�������z��Poisson���z�ɂȂ�Ƃ�����������<br>
 * �������ς�n->infty�ɂ�����np�ƂȂ邱�Ƃ��킩��̂ŁA�w�肳�ꂽ���ώ�������p���t�Z���邱�Ƃ��\�ł���B<br>
 * ���̃N���X�ł͂��̕�����p����̂ŁAp�𒼐ڎw�肷�邱�Ƃ͂ł��Ȃ��悤�ɂ��Ă���B<br>
 * �������An��������x�傫�����Ƃ͑O��Ƃ��ĕK�v�ł���B
 * @author Yu
 *
 */
public class StaticRNDNetwork extends StaticNetwork {
	
	protected double pConnect;
	
	@Override
	public void build() {
		ArrayList<Integer[]> potentialLinks = new ArrayList<Integer[]>(); //�\�ȃ����N�𐔂��グ�B���̃��X�g��n^2�I�[�_�[�ő傫���Ȃ�̂ŉ����ʂ̕��@���l�������������B
		for (int subject = 0; subject < this.getnAgents(); subject++) {
			for (int object = subject + 1; object < this.getnAgents(); object++) {
				Integer[] pLink = {subject, object};
				potentialLinks.add(pLink);
			}
		}
		
		for (Integer[] pLink : potentialLinks) {
			double roll = this.localRNG.nextDouble();
			if (roll < pConnect) {
				this.constructLink(pLink[0], pLink[1]);
			}
		}
	}

	/**
	 * subject����object�Aobject����subject�Ƀ����N�𒣂�D<br>
	 * ��d�o�^���Ȃ��悤�Ƀ`�F�b�N���邪�ApotentialLinks�ɓ�d�o�^�������悤�ɂ��Ă����΋N���肦�Ȃ��B
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}
	
	public StaticRNDNetwork(int nAgents, boolean orientation, Double degree) {
		super("RND", nAgents, orientation, degree);
		this.pConnect = this.getGivenDegree() / (double)this.getnAgents();
		this.build();
	}
	
	/**
	 * �G�[�W�F���g����degree��^����R���X�g���N�^�D
	 * @param nAgents
	 * @param degree
	 */
	public StaticRNDNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticRNDNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}
}
