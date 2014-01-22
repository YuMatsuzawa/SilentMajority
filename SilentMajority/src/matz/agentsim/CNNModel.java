package matz.agentsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**InfoAgent�N���X�ō��ꂽ�G�[�W�F���g�ԂɃ����N�𒣂�,���̎Q�Ɗ֌W���e�G�[�W�F���g�̎����X�g�ɋL�^���Ă���.
 * �p�����[�^�Ƃ��āC����^�C���X�e�b�v�Łu�F�B�̗F�B�v�ԂɃ����N�𒣂邩,�u�S�����֌Wor�����֌W�̓�ҁv�Ԃɒ��邩�̑I��臒l�����D
 * @param infoAgentsArray
 */
public class CNNModel implements InfoNetworkBuilder {
	private double p_nn;
	private static final double P_NN_DEFAULT = 0.666667;
	private ArrayList<Integer[]> potentialLinks = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
	private int includedAgents;
	
	public InfoAgent[] build(InfoAgent[] infoAgentsArray) {
		InfoAgent[] tmpAgentsArray = infoAgentsArray;
		int nAgents = infoAgentsArray.length;
		
		/*�l�b�g���[�N�̎�����i�Ƃ肠���������O���t�j
		 * 
		 * 0----2----1
		 * 
		 */
		tmpAgentsArray[0].appendIndirectedList(2);
		tmpAgentsArray[1].appendIndirectedList(2);

		tmpAgentsArray[2].appendIndirectedList(0);
		tmpAgentsArray[2].appendIndirectedList(1);
		
		Integer[] firstPLink = {0, 1};
		this.potentialLinks.add(firstPLink);
		
		this.includedAgents = 3;
		
		while (this.includedAgents < nAgents) {
			double roll = this.localRNG.nextDouble();
			if (roll < this.getP_nn()){
				this.connectPotential(tmpAgentsArray);
			} else {
				this.includeAgent(tmpAgentsArray);
			}
		}
		
		//�`�F�b�N�̂��߂ɁC�S�G�[�W�F���g�̗אڃ��X�g���\�[�g����D
		for (InfoAgent agent : tmpAgentsArray) {
			agent.sortLists();
		}
		
		return tmpAgentsArray;
	}
	
	/**���ݓI�����N�����ۂɐڑ�����D
	 * @param tmpAgentsArray
	 */
	private void connectPotential(InfoAgent[] tmpAgentsArray) {
		int listSize = this.getPotentialLinksLength();
		if (listSize == 0) return;
		
		int roll = this.localRNG.nextInt(listSize);
		Integer[] pLink = this.potentialLinks.get(roll);
		this.potentialLinks.remove(roll);
			//roll�œK���ȃ|�e���V���������N��I�яo���C������G�b�W�ɕϊ�����D

		this.constructLink(pLink[0], pLink[1], tmpAgentsArray);
	}
	/**�܂��ڑ�����Ă��Ȃ��G�[�W�F���g�������_���ɉ�����D
	 * @param tmpAgentsArray
	 */
	private void includeAgent(InfoAgent[] tmpAgentsArray) {	
		int target = this.localRNG.nextInt(this.includedAgents);
		int newcomer = this.includedAgents++;

		this.constructLink(newcomer, target, tmpAgentsArray);
	}
	/**target��newcomoer�Ƃ̊ԂɃ����N�𒣂�C������|�e���V���������N��o�^����.<br />
	 * ��d�o�^���Ȃ��悤�ׂ����`�F�b�N����D<br />
	 * �݂��̃C���f�b�N�X����x�Ɍ݂��̗אڃ��X�g�ɘR�ꖳ���o�^����̂ŁC���̃��\�b�h���������t�ɂ��ē�x�ĂԕK�v�͂Ȃ����C�Ă�ł͂Ȃ�Ȃ��D
	 * @param newcomer
	 * @param target
	 * @param tmpAgentsArray
	 */
	private void constructLink(int newcomer, int target, InfoAgent[] tmpAgentsArray) {
		tmpAgentsArray[newcomer].appendIndirectedList(target);
		tmpAgentsArray[target].appendIndirectedList(newcomer);
		safeAppendPotentialLink(newcomer,target,tmpAgentsArray);
		safeAppendPotentialLink(target,newcomer,tmpAgentsArray);
	}
	/**�d�����Ȃ��悤�m�F���Ȃ���|�e���V���������N��ǉ�����D<br />
	 * ��������݂��ۂ̃|�e���V���������N��o�^���邽�߂Ɉ�x�C���肩�猩���ۂ̃|�e���V���������N��o�^���邽�߂ɃC���f�b�N�X�������t�ɂ��Ă�����x�ĂԕK�v������D
	 * @param newcomer
	 * @param target
	 * @param tmpAgentsArray
	 */
	private void safeAppendPotentialLink(int newcomer, int target, InfoAgent[] tmpAgentsArray) {
		for(int pIndex : tmpAgentsArray[target].getIndirectedList()) {
			if (pIndex != newcomer && !tmpAgentsArray[newcomer].getIndirectedList().contains(pIndex)) {
				Integer[] pLink = {pIndex, newcomer};
				Integer[] rLink = {newcomer, pIndex};
				boolean isNew = true;
				for(Integer[] link : this.potentialLinks) { //ArrayList<Integer[]>�ł̏d������͂������������@�łȂ��ƃ_��.�m�[�g�Q��
					if (Arrays.equals(pLink, link) || Arrays.equals(rLink, link)) {
						isNew = false;
						break;
					}
				}
				if (isNew) this.potentialLinks.add(pLink);
			}
		}
	}
	
	/**CNN���f���Ńl�b�g���[�N��������N���X�D�m���p�����[�^��^����R���X�g���N�^�D
	 * build()���\�b�h�����C���ƂȂ鐶���C�e���[�^�D
	 * @param infoAgentsArray
	 */
	public CNNModel(double p_nn) {
		this.setP_nn(p_nn);
	}
	/**�f�t�H���g�̊m���p�����[�^�ŃR���X�g���N�g�D<br />
	 * 2/3�̊m���Łu�F�B�̗F�B�v�C1/3�̊m���ł���ȊO�������N����D
	 */
	public CNNModel() {
		this.setP_nn(P_NN_DEFAULT);
	}
	
	/**�m���p�����[�^���擾�D
	 * @return p_nn
	 */
	public double getP_nn() {
		return p_nn;
	}
	/**�m���p�����[�^���w��D
	 * @param p_nn �Z�b�g���� p_nn
	 */
	public void setP_nn(double p_nn) {
		this.p_nn = p_nn;
	}
	/**���ݎg�p����Ă�����̃|�e���V���������N�̃T�C�Y��Ԃ��D
	 * @return
	 */
	private int getPotentialLinksLength() {
		int length = 0;
		length = (this.potentialLinks.size() > 0)? this.potentialLinks.size() : length;
		return length;
	}

}