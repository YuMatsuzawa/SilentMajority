package matz.agentsim;

/**���G�[�W�F���g����Ȃ�l�b�g���[�N�𒣂邽�߂̃r���_�E�C���^�[�t�F�[�X�D<br />
 * ���G�[�W�F���g�z����󂯎��C�e�G�[�W�F���g�ɗאڃ��X�g��^�������̂�Ԃ��悤�ȃ��\�b�hbuild()����������D
 * @author Matsuzawa
 *
 */
public interface InfoNetworkBuilder {
	final int NAME_BASED = 0;
	final int INDEX_BASED = 1;
	InfoAgent[] build(InfoAgent[] infoAgentsArray);
}