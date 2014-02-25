package matz.basics.network;

import java.util.ArrayList;

public class StaticREGNetwork extends StaticNetwork {

	@Override
	public void build() {
		if (this.getOrientation() == UNDIRECTED) {
			for (int subject = 0; subject < this.getnAgents(); subject++) {
				for (int object : this.getObjectsOf(subject)) {
					this.constructLink(subject, object);
				}
			}
		}
		
	}

	/**
	 * subject���猩�āi�Б�D/2�l�����������Ɂj����D�l���̃G�[�W�F���g�̃C���f�b�N�X��ArrayList�`���Ŏ擾����D<br>
	 * D�̓R���X�g���N�g����givenDegree�Ŏw�肳���D�w�肳��ĂȂ���΃f�t�H���g�l��6���g����D<br>
	 * D����������ꍇ�C2�Ŋ������ۂ̒[���͂��̎����ł͐؂�̂Ă���̂ŁCD�ɂ͋���������ׂ��D
	 * @param subject
	 * @return
	 */
	private ArrayList<Integer> getObjectsOf(int subject) {
		int sideBound = (int)(this.getGivenDegree() / 2);
		ArrayList<Integer> objects = new ArrayList<Integer>();
		for (int d = 1; d <= sideBound; d++) {
			int lowerObject = subject - d, upperObject = subject + d;
			if (lowerObject >= 0) objects.add(lowerObject);
			else {
				lowerObject += this.getnAgents();
				objects.add(lowerObject);
			}
			if (upperObject < this.getnAgents()) objects.add(upperObject);
			else {
				upperObject -= this.getnAgents();
				objects.add(upperObject);
			}
		}
		
		return objects;
	}

	/**
	 * subject����object�Ƀ����N�𒣂�D<br>
	 * REG�ł͎����̎��͂ɒ���Ƃ��������㎩�����S�Ō���Ύ������̂ŁC<br>
	 * subject->object�����̓o�^�̂ݍs���D
	 * @param subject
	 * @param object
	 */
	private void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) {
			this.appendUndirectedListOf(subject, object);
		}
	}
	
	public StaticREGNetwork(int nAgents, boolean orientation, Double degree) {
		super("REG", nAgents, orientation, degree);
		this.build();
	}
	
	public StaticREGNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticREGNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}

}
