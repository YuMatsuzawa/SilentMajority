package matz.basics.network;

import java.util.ArrayList;
import java.util.List;


/**
 * Barabasi-Albert���f���Ɋ�Â��C�X�P�[���t���[�l�b�g���[�N�𐶐�����N���X�D<br>
 * �p�����[�^�͏����m�[�h�̐�m_0�ƁC�ǉ�����m�[�h�̃G�b�W��m(<=m_0)�ł���D<br>
 * ���̂Ƃ��C�\���ɑ傫��N�ɑ΂��Ă�m_0�͖����ł���悤�ɂȂ�C<br>
 * ���m�[�h��1�ǉ�����ƃG�b�W��m�{�����邱�Ƃ��玟�����a��2m�������邱�ƂɂȂ邽�߁C<br>
 * �w�肵�����ώ���degree�ɑQ�߂���l�b�g���[�N�𓾂���悤��m��m=degree/2�ƂȂ�D<br>
 * m_0��m_0=m�Ƃ��ė^����D
 * @author Matsuzawa
 *
 */
public class StaticBANetwork extends StaticNetwork {
	
	protected int mEdge;
	
	@Override
	public void build() {
		//�������̂��߂ɁC�ǉ�����m�[�h�̃G�b�W���ȏ�̃m�[�h���K�v�D
		int initHub = this.mEdge;
		for (int initLeaf = 0; initLeaf < this.mEdge; initLeaf++) this.constructLink(initHub,initLeaf); //�l�b�g���[�N�̎�
		//���Ƃ�Preferential attachment
		for(int subject = initHub + 1; subject < this.getnAgents(); subject++) {
			List<Integer> candidates = new ArrayList<Integer>();
			for (int candidate = 0; candidate < subject; candidate++) candidates.add(candidate);
			int attached = 0;
			while (attached < this.mEdge){
				double roll = this.localRNG.nextDouble();
				double sumCandDegree = 0.0; //����ɂȂ�D���ɑI�����ꂽ���m�[�h����菜�����тɍČv�Z����
				for (Integer candidate : candidates) sumCandDegree += this.getDegreeOf(candidate);
				double pAttached = 0.0;
				for (Integer candidate : candidates) {
					//���[���b�g�̃|�P�b�g��1�����ԂɌ��Ă����C���[�W
					//roll���_�Ń{�[���̐Î~�ʒu�͊m�肵�Ă���C���_���珇�Ɏ����̑傫���ɉ������傫���̃|�P�b�g���e���ɂ��Ă����Ă����D
					//�{�[���̐Î~�ʒu�Ƀ|�P�b�g�������Ă�����₪������ƂȂ�D
					pAttached += (double) this.getDegreeOf(candidate) / sumCandDegree; //���̉��Z�l���|�P�b�g�̕��ɑ�������
					if (roll < pAttached) {
						this.constructLink(subject, candidate);
						candidates.remove(candidate); //�����肪�m�肵�����_�ł�������������菜��
						break;
					}
				}
				attached++;
			}
		}
	}

	/**
	 * subject����object�Aobject����subject�Ƀ����N�𒣂�D<br>
	 * ��d�o�^���Ȃ��悤�Ƀ`�F�b�N���邪�A���Ƀ����N������ꂽ�G�[�W�F���g�͐ڑ���₩�珜����Ă���͂��Ȃ̂ŋN���肦�Ȃ��D
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}

	public StaticBANetwork(int nAgents, boolean orientation, Double degree) {
		super("BA", nAgents, orientation, degree);
		this.mEdge = (int) (degree / 2);
		this.build();
	}
	
	/**
	 * �G�[�W�F���g����degree��^����R���X�g���N�^�D
	 * @param nAgents
	 * @param degree
	 */
	public StaticBANetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticBANetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}
}
