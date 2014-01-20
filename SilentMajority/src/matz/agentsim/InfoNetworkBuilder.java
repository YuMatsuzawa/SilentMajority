package matz.agentsim;

/**情報エージェントからなるネットワークを張るためのビルダ・インターフェース．<br />
 * 情報エージェント配列を受け取り，各エージェントに隣接リストを与えたものを返すようなメソッドbuild()を実装する．
 * @author Matsuzawa
 *
 */
public interface InfoNetworkBuilder {
	final int NAME_BASED = 0;
	final int INDEX_BASED = 1;
	InfoAgent[] build(InfoAgent[] infoAgentsArray);
}