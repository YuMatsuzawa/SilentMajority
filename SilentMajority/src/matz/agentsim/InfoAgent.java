package matz.agentsim;

import java.util.ArrayList;

public class InfoAgent {
	
	private String agentName;
	private int agentIndex;
	private ArrayList<String> followingNameList;
	private ArrayList<String> followedNameList;
	private ArrayList<Integer> followingIndexList;
	private ArrayList<Integer> followedIndexList;
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
	
	

}
