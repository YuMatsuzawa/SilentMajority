package matz.basics.network;

import java.util.ArrayList;

/**
 * �i�q�l�b�g���[�N�𐶐�����N���X�B<br>
 * �����̎��͂́A����degree�Ŏw�肳�ꂽ�l���Ƃ̊ԂɃ����N�𒣂�B���E�͎������E�ł���B<br>
 * degree����̏ꍇ(�����ɓ����̃����N��\��Ƃ��������̓s����)�A�ŋߖT�̏����ȋ����Ɋۂ߂���B<br>
 * @author Yu
 *
 */
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
	 * subject���猩�āAindex�̉������i�������jD/2�l���̃G�[�W�F���g�̃C���f�b�N�X��ArrayList�`���Ŏ擾����D<br>
	 * D�̓R���X�g���N�g����givenDegree�Ŏw�肳���D�w�肳��ĂȂ���΃f�t�H���g�l��6���g����D<br>
	 * D����������ꍇ�C2�Ŋ������ۂ̒[���͂��̎����ł͐؂�̂Ă���̂ŁCD�ɂ͋���������ׂ��D
	 * @param subject
	 * @return
	 */
	private ArrayList<Integer> getObjectsOf(int subject) {
		int sideBound = (int)(this.getGivenDegree() / 2);
		ArrayList<Integer> objects = new ArrayList<Integer>();
		for (int d = 1; d <= sideBound; d++) {
			int upperObject = subject + d;
			if (upperObject < this.getnAgents()) objects.add(upperObject);
			else {
				upperObject -= this.getnAgents();
				objects.add(upperObject);
			}
		}
		
		return objects;
	}

	/**
	 * subject����object�Aobject����subject�Ƀ����N�𒣂�D<br>
	 * ��d�o�^���Ȃ��悤�Ƀ`�F�b�N���邪�A�Б��̂݁iindex�̉������̂݁j�Ɍ������Đڑ����Ă����Γ�d�o�^�͋N���肦�Ȃ��B
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}
	
	/**
	 * ��{�R���X�g���N�^�BWS���f���ł��g����悤�ɂ��Ă���B
	 * @param ntwkName
	 * @param nAgents
	 * @param orientation
	 * @param degree
	 */
	protected StaticREGNetwork(String ntwkName, int nAgents, boolean orientation, Double degree) {
		super(ntwkName, nAgents, orientation, degree);
		this.build();
	}
	
	public StaticREGNetwork(int nAgents, boolean orientation, Double degree) {
		this("REG", nAgents, orientation, degree);
	}
	
	/**
	 * �G�[�W�F���g����degree��^����R���X�g���N�^�D
	 * @param nAgents
	 * @param degree
	 */
	public StaticREGNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticREGNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}

}
