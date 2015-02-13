package matz.basics.network;

import matz.agentsim.InfoAgent;

/**情報エージェントからなるネットワークを張るためのビルダ・インターフェース．<br>
 * 情報エージェント配列を受け取り，各エージェントに隣接リストを与えたものを返すようなメソッドbuild()を実装する．
 * @author Matsuzawa
 *
 */
public interface NetworkBuilder {
	final boolean DIRECTED = true;
	final boolean UNDIRECTED = false;
	InfoAgent[] build(InfoAgent[] infoAgentsArray);
}