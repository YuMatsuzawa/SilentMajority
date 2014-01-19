package matz.agentsim;

import java.util.ArrayList;
import java.util.Random;

import matz.agentsim.RunnableSimulator.InfoNetworkBuilder;



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
		
		while (includedAgents < nAgents) {
			double roll = localRNG.nextDouble();
			if (roll < this.getP_nn()){
				this.connectPotential(style);
			} else {
				this.includeAgent(style);
			}
			
		}
		
		return tmpAgentsArray;
	}
	
	/**���ݓI�����N�����ۂɐڑ�����D
	 * @param style
	 */
	private void connectPotential(int style) {
		if (style == NAME_BASED) {
			
		} else if (style == INDEX_BASED) {
			
		}
		
	}
	/**�܂��ڑ�����Ă��Ȃ��G�[�W�F���g�������_���ɉ�����D
	 * @param style
	 */
	private void includeAgent(int style) {
		
		if (style == NAME_BASED) {
			
		} else if (style == INDEX_BASED) {
			
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

}