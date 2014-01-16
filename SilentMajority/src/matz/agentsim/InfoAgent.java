package matz.agentsim;

import java.util.ArrayList;

public class InfoAgent {
	
	private String agentName;
	private int agentIndex;
	private ArrayList<String> followingNameList;
	private ArrayList<String> followedNameList;
	private ArrayList<Integer> followingIndexList;
	private ArrayList<Integer> followedIndexList;
	private boolean isSilent = false;
	private int Opinion;
	/**情報エージェントの文字列名を取得．
	 * @return
	 */
	public String getAgentName() {
		return this.agentName;
	}
	/**情報エージェントの文字列名を指定．
	 * 
	 * @param agentName
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	/**情報エージェントの整数識別番号を取得．
	 * @return
	 */
	public int getAgentIndex() {
		return this.agentIndex;
	}
	/**情報エージェントの整数識別番号を指定．
	 * @param agentIndex
	 */
	public void setAgentIndex(int agentIndex) {
		this.agentIndex = agentIndex;
	}
	/**情報エージェントが参照しているエージェントの文字列名リストを取得．
	 * @return
	 */
	public ArrayList<String> getFollowingNameList() {
		return this.followingNameList;
	}
	/**情報エージェントが参照しているエージェントの文字列名リストを空のリストに初期化．
	 */
	public void initFollowingNameList() {
		this.followingNameList = new ArrayList<String>();
	}
	/**情報エージェントが参照しているエージェントの文字列名リストに新たなエージェントの文字列名を追加．
	 * @param name
	 */
	public void appendFollowingNameList(String name) {
		this.followingNameList.add(name);
	}
	/**情報エージェントが参照されているエージェントの文字列名リストを取得．
	 * @return
	 */
	public ArrayList<String> getFollowedNameList() {
		return followedNameList;
	}
	/**情報エージェントが参照されているエージェントの文字列名リストを空のリストに初期化．
	 * 
	 */
	public void initFollowedNameList() {
		this.followedNameList = new ArrayList<String>();
	}
	/**情報エージェントが参照されているエージェントの文字列名リストに新たなエージェントの文字列名を追加．
	 * @param followedNameList
	 */
	public void appendFollowedNameList(String name) {
		this.followedNameList.add(name);
	}
	/**情報エージェントが参照しているエージェントの整数インデックスリストを取得。
	 * @return followingIndexList
	 */
	public ArrayList<Integer> getFollowingIndexList() {
		return followingIndexList;
	}
	/**情報エージェントが参照しているエージェントの整数インデックスリストを空のリストに初期化。
	 * @param followingIndexList セットする followingIndexList
	 */
	public void initFollowingIndexList() {
		this.followingIndexList = new ArrayList<Integer>();
	}
	/**情報エージェントが参照しているエージェントの整数インデックスリストに新たなエージェントの整数インデックスを追加。
	 * @param followingIndexList セットする followingIndexList
	 */
	public void initFollowingIndexList(int index) {
		this.followingIndexList.add(index);
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストを取得。
	 * @return followedIndexList
	 */
	public ArrayList<Integer> getFollowedIndexList() {
		return followedIndexList;
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストを空のリストに初期化。
	 * @param followedIndexList セットする followedIndexList
	 */
	public void initFollowedIndexList() {
		this.followedIndexList = new ArrayList<Integer>();
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストに新たなエージェントの整数インデックスを追加。
	 * @param followedIndexList セットする followedIndexList
	 */
	public void appendFollowedIndexList(int index) {
		this.followedIndexList.add(index);
	}
	/**情報エージェントがサイレントであればtrue，ヴォーカルであればfalseを返す<br />
	 * デフォルトはfalse
	 * @return isSilent
	 */
	public boolean isSilent() {
		return isSilent;
	}
	/**情報エージェントをサイレントにする。
	 */
	public void muzzle() {
		this.isSilent = true;
	}
	/**情報エージェントをヴォーカルにする。
	 */
	public void unmuzzle() {
		this.isSilent = false;
	}
	/**情報エージェントの現在の意見を取得する。<br />
	 * エージェントがサイレントである場合は取得できない。null値を返す
	 * @return opinion
	 */
	public int getOpinion() {
		if (!this.isSilent()) {
			return Opinion;
		} else {
			return (Integer)null;
		}
	}
	/**情報エージェントの意見を指定する。
	 * @param opinion セットする opinion
	 */
	public void setOpinion(int opinion) {
		Opinion = opinion;
	}
	
	

}
