package matz.agentsim;

import java.util.ArrayList;
import java.util.Random;


/**InfoAgent�N���X�ō��ꂽ�G�[�W�F���g�ԂɃ����N�𒣂�,���̎Q�Ɗ֌W���e�G�[�W�F���g�̎����X�g�ɋL�^���Ă���.
 * �p�����[�^�Ƃ��āC����^�C���X�e�b�v�Łu�F�B�̗F�B�v�ԂɃ����N�𒣂邩,�u�S�����֌Wor�����֌W�̓�ҁv�Ԃɒ��邩�̑I��臒l�����D
 * @param infoAgentsArray
 */
public class CNNModel implements InfoNetworkBuilder {
	private double p_nn;
	private final double P_NN_DEFAULT = 0.667;
	private ArrayList<String[]> PotentialLinksByName = new ArrayList<String[]>();
	private ArrayList<Integer[]> PotentialLinksByIndex = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
	private int includedAgents;
//	private InfoAgent[] infoAgentsArray;
	
	/**CNN���f���Ńl�b�g���[�N��������N���X�D�m���p�����[�^��^����R���X�g���N�^�D
	 * build()���\�b�h�����C���ƂȂ鐶���C�e���[�^�D
	 * @param infoAgentsArray
	 */
	public CNNModel(double p_nn) {
		this.setP_nn(p_nn);
	}
	/**�f�t�H���g�̊m���p�����[�^�ŃR���X�g���N�g�D
	 * 
	 */
	public CNNModel() {
		this.setP_nn(this.P_NN_DEFAULT);
	}
	
	public InfoAgent[] build(InfoAgent[] infoAgentsArray) {
		InfoAgent[] tmpAgentsArray = infoAgentsArray;
		int style = infoAgentsArray[0].getStyle();
		int nAgents = infoAgentsArray.length;
		
		//�l�b�g���[�N�̎�����i�Ƃ肠���������O���t�j
		tmpAgentsArray[0].appendIndirectedList(2);
		tmpAgentsArray[1].appendIndirectedList(2);

		tmpAgentsArray[2].appendIndirectedList(0);
		tmpAgentsArray[2].appendIndirectedList(1);
		
		Integer[] initpLink = {0, 1};
		this.appendPotentialLinks(initpLink, style);
		
		this.includedAgents = 3;
		
		while (this.includedAgents < nAgents) {
			double roll = this.localRNG.nextDouble();
			if (roll < this.getP_nn()){
				this.connectPotential(tmpAgentsArray);
			} else {
				this.includeAgent(tmpAgentsArray);
			}
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
		Integer[] pLink = this.PotentialLinksByIndex.get(roll);
		/*int index1,index2;
		index1 = this.PotentialLinksByIndex.get(roll)[0];
		index2 = this.PotentialLinksByIndex.get(roll)[1];
		this.PotentialLinksByIndex.remove(roll);
		Integer[] pLink = {index1, index2};*/
		Integer[] rLink = {pLink[1], pLink[0]};
		while (this.PotentialLinksByIndex.contains(pLink) || this.PotentialLinksByIndex.contains(rLink)) {
			int index = this.PotentialLinksByIndex.indexOf(pLink);
			if (index == -1) index = this.PotentialLinksByIndex.indexOf(rLink);
			this.PotentialLinksByIndex.remove(index);
		}
			//roll�œK���ȃ|�e���V���������N��I�яo���C������G�b�W�ɕϊ�����D
			//������Ɩ�肠�邪�C�����t�\�L���ꂽ�����|�e���V���������N���������Ȃ炱��������ɑ|������D
			//TODO ���Ԃ��Ȃ��̂ŁCIndex�x�[�X�̕��̂ݍ��D���Name�x�[�X�̕��ɂ��Ή�����悤�����������D

		this.safeAppendIndexPotentialLink(pLink[0], pLink[1], tmpAgentsArray);
		this.safeAppendIndexPotentialLink(pLink[1], pLink[0], tmpAgentsArray);
		if (!tmpAgentsArray[pLink[0]].getIndirectedList().contains(pLink[1])) tmpAgentsArray[pLink[0]].appendIndirectedList(pLink[1]);
			//FIXME �ǂ����Ă��C�אڃm�[�h�ǉ����d�����邱�Ƃ�����D����͑Ώ��Ö@�Ȃ̂Ō�����T��
		if (!tmpAgentsArray[pLink[1]].getIndirectedList().contains(pLink[0])) tmpAgentsArray[pLink[1]].appendIndirectedList(pLink[0]);
	}
	/**�܂��ڑ�����Ă��Ȃ��G�[�W�F���g�������_���ɉ�����D
	 * @param tmpAgentsArray
	 */
	private void includeAgent(InfoAgent[] tmpAgentsArray) {	
		int target = this.localRNG.nextInt(this.includedAgents);
		int newComer = this.includedAgents++;

		this.safeAppendIndexPotentialLink(newComer, target, tmpAgentsArray);
		if (!tmpAgentsArray[target].getIndirectedList().contains(newComer)) tmpAgentsArray[target].appendIndirectedList(newComer);
		if (!tmpAgentsArray[newComer].getIndirectedList().contains(target)) tmpAgentsArray[newComer].appendIndirectedList(target);
	}
	/**index2�̗אڃ��X�g�𑖍����Cindex1�Ƃ̊ԂɃ|�e���V���������N�𒣂�D
	 * @param index1
	 * @param index2
	 * @param tmpAgentsArray
	 */
	private void safeAppendIndexPotentialLink(int index1, int index2, InfoAgent[] tmpAgentsArray) {
		for (Object pObj : tmpAgentsArray[index2].getIndirectedList()) {
			int pIndex = (Integer) pObj;
			if (pIndex == index1 || tmpAgentsArray[index1].getIndirectedList().contains(pIndex)) continue;
			Integer[] pLink = {index1, pIndex};
			Integer[] rLink = {pIndex, index1};	//�ǉ��̍ہC�t�����ɕ\�L���ꂽ�����ȃ����N���Ȃ����Ƃ��m�F����D
			if (this.PotentialLinksByIndex.contains(pLink) || this.PotentialLinksByIndex.contains(rLink)) continue;
			this.appendPotentialLinks(pLink, INDEX_BASED);
		}
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
		length = (this.PotentialLinksByName.size() > 0)? this.PotentialLinksByName.size() : length;
		length = (this.PotentialLinksByIndex.size() > 0)? this.PotentialLinksByIndex.size() : length;
		return length;
	}
	/**�|�e���V���������N�̃��X�g�ɒǉ�����D�X�^�C���𔻕ʂ���D
	 * @param nameOrIndex
	 */
	public <SorI> void appendPotentialLinks (SorI[] nameOrIndex, int style) {
		if (style == NAME_BASED) {
			this.PotentialLinksByName.add((String[]) nameOrIndex);
		} else {
			this.PotentialLinksByIndex.add((Integer[]) nameOrIndex);
		}
	}

}