package matz.basics.network;

import java.util.ArrayList;
import java.util.List;


public class StaticWSNetwork extends StaticREGNetwork {
	
	protected static double P_REWIRE_DEFAULT = 0.1;
	protected double pRewire;
	
	public void rewire() {
		// �R���X�g���N�g���_��REG�l�b�g���[�N�𒣂�A�����_���ɒ���ւ���ibuild()��Override�����ɂ����AStaticREGNetwork�̃R���X�g���N�^�����̂܂܎g���j�B
		// Watts-Strogatz�̘_���ʂ�A�i�q�̕Б��Ɍ����ĒH���čs���Ak/2������
		for (int lap = 1; lap <= this.getGivenDegree() / 2; lap++) {
			for (int subject = 0; subject < this.getnAgents(); subject++) {
				int objectRewired = subject + lap;
				if (objectRewired < this.getnAgents()) this.rewireLink(subject, objectRewired);
				else {
					objectRewired -= this.getnAgents();
					this.rewireLink(subject, objectRewired);
				}
			}
		}
	}
	
	private void rewireLink(int subject, int objectRewired) {
		List<Integer> candidateAgents = new ArrayList<Integer>();
		for(int index = 0; index < this.getnAgents(); index++) candidateAgents.add(index);	//�S�G�[�W�F���g�̃��X�g������
		candidateAgents.remove((Integer) subject);
		candidateAgents.removeAll(this.getUndirectedListOf(subject));						//����������Ƀ����N���Ă���G�[�W�F���g�̃��X�g�Ƃ̍��W��������Ē��ւ���⃊�X�g�Ƃ���
		double roll = this.localRNG.nextDouble();
		if (roll < this.pRewire) {
			int newObject = candidateAgents.get(this.localRNG.nextInt(candidateAgents.size()));
			this.constructLink(subject, newObject);
			this.removeFromUndirectedListOf(subject, objectRewired);
			this.removeFromUndirectedListOf(objectRewired, subject);
		}
	}

	public StaticWSNetwork(int nAgents, boolean orientation, Double degree, double pRewire) {
		super("WS", nAgents, orientation, degree);
		this.pRewire = pRewire;
		this.rewire();
	}
	
	public StaticWSNetwork(int nAgents, Double degree, double pRewire) {
		this(nAgents, UNDIRECTED, degree, pRewire);
	}
	
	public StaticWSNetwork(int nAgents, double pRewire) {
		this(nAgents, DEGREE_DEFAULT, pRewire);
	}
	
	public StaticWSNetwork(int nAgents) {
		this(nAgents, P_REWIRE_DEFAULT);
	}

}
