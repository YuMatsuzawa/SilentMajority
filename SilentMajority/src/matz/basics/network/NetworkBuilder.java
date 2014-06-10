package matz.basics.network;

import matz.agentsim.InfoAgent;

/**���G�[�W�F���g����Ȃ�l�b�g���[�N�𒣂邽�߂̃r���_�E�C���^�[�t�F�[�X�D<br>
 * ���G�[�W�F���g�z����󂯎��C�e�G�[�W�F���g�ɗאڃ��X�g��^�������̂�Ԃ��悤�ȃ��\�b�hbuild()����������D
 * @author Matsuzawa
 *
 */
public interface NetworkBuilder {
	final boolean DIRECTED = true;
	final boolean UNDIRECTED = false;
	InfoAgent[] build(InfoAgent[] infoAgentsArray);
}