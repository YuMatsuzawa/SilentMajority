package matz.agentsim;

import java.util.ArrayList;
import java.util.Collections;

public class InfoAgent {
	
	private int agentIndex;
	private ArrayList<Integer> followingList;
	private ArrayList<Integer> followedList;
	private boolean isSilent = false;
	private Integer tmpOpinion;
	/**抽象的な意見（情報）を表す状態変数．<br />
	 * プリミティブintではなくラッパ型のIntegerにしておき，nullを使えるようにする．
	 */
	private Integer opinion;
	private double influence = Math.random();
	private double threshold = 0.5;
	
	/**自分が中立的・あるいは未定義の状態にあるとき，肯定的・否定的問わず何らかの先進的意見に触れると，それに影響される．<br />
	 * 影響を受けるか否かは，相手の影響力の強さによる．<br />
	 * 隣接リストにいる人を順に参照していき、ヴォーカルな人の中で最も影響力の高い人から影響される。
	 * @return 変化があったらtrue
	 */
	public boolean IndependentCascade(InfoAgent[] infoAgentsArray) {
		if (!(this.getOpinion() == null || this.getOpinion() == 0)) return false;

		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		double influence = -1;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion();
			double neighborInfluence = infoAgentsArray[(Integer) neighbor].getInfluence();
			try {
				if (preOp == null || neighborOp > preOp) {
					if (neighborInfluence > influence) {
						tmpOp = neighborOp;
						influence = neighborInfluence;
					}
				}
			} catch(Exception e) {
				//このExecptionが、サイレントエージェントを参照した時のものである（getOpinionがNullを返してくるのでneighborOpとpreOpの比較時に例外）
				continue;
			}
		}
		this.setTmpOpinion(tmpOp);
		if (this.getTmpOpinion() != preOp) return true;
		return false;
	}

	/**隣接しているノードの中での多数派を知覚して，その影響を受ける．
	 * @param infoAgentsArray
	 * @return
	 */
	public boolean LinearThreashold(InfoAgent[] infoAgentsArray) {
		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		
		int sum = 0;
		Integer[] opinions = {0,0,0};
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion(); 
			if (neighborOp == null) continue;
			
			sum++;
			opinions[neighborOp]++;
		}
		if (sum == 0) return false;
		for (int opIndex = 0; opIndex < 3; opIndex++) {
			if (opinions[opIndex] / sum > this.threshold) tmpOp = opIndex;
		}
		this.setTmpOpinion(tmpOp);
		if (this.getTmpOpinion() != preOp) return true;
		return false;
	}
	
	/**中間意見を確定意見として適用する。イテレータの最後に呼ぶ。
	 * 
	 */
	public void applyOpinion() {
		if (this.tmpOpinion != null) this.setOpinion(this.tmpOpinion);
	}
	/**中間意見を取得する。意見変化をチェックするために使う。
	 * 
	 */
	public Integer getTmpOpinion() {
		return this.tmpOpinion;
	}
	/**一時的に意見を格納する。イテレータの中間データの保存に使う。
	 * @param tmpOp
	 */
	private void setTmpOpinion(Integer tmpOp) {
		this.tmpOpinion = tmpOp;
	}
	
	//文字列名を与えるスタイルはやめる.
	
	/**整数識別番号を与えて情報エージェントを初期化するコンストラクタ．
	 * リストも全て識別番号で取り扱う．
	 * @param index -整数の識別番号
	 * @param opinion -整数値の意見
	 * @param isSilent -サイレントであるか
	 */
	public InfoAgent(int index, Integer opinion, boolean isSilent) {
		this.setAgentIndex(index);
		this.initFollowingList();
		this.initFollowedList();
		this.setOpinion(opinion);
		this.setTmpOpinion(this.forceGetOpinion());
		if (isSilent) this.muzzle();	
	}
	
	/**整数識別番号を与えて情報エージェントを初期化するコンストラクタ．
	 * 全てサイレントでない（ヴォーカルである）ものとしている．
	 * @param index -整数の識別番号
	 * @param opinion -整数値の意見
	 */
	public InfoAgent(int index, Integer opinion) {
		this.setAgentIndex(index);
		this.initFollowingList();
		this.initFollowedList();
		this.setTmpOpinion(this.forceGetOpinion());
		this.setOpinion(opinion);
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
	
	/**以下，リスト関連のメソッド． 初期化メソッドはprivateとする．
	 */	
	
	/**情報エージェントが参照しているエージェントの整数インデックスリストを取得。
	 * @return followingIndexList
	 */
	public ArrayList<Integer> getFollowingList() {
		return this.followingList;
	}
	/**情報エージェントが参照しているエージェントの整数インデックスリストを空のリストに初期化。
	 * @param followingList セットする followingIndexList
	 */
	private void initFollowingList() {
		this.followingList = new ArrayList<Integer>();
	}
	/**情報エージェントが参照しているエージェントの整数インデックスリストに新たなエージェントの整数インデックスを追加。
	 * @param followingList セットする followingIndexList
	 */
	public void appendFollowingList(int index) {
		this.followingList.add(index);
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストを取得。
	 * @return followedIndexList
	 */
	public ArrayList<Integer> getFollowedList() {
		return this.followedList;
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストを空のリストに初期化。
	 * @param followedList セットする followedIndexList
	 */
	private void initFollowedList() {
		this.followedList = new ArrayList<Integer>();
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストに新たなエージェントの整数インデックスを追加。
	 * @param followedList セットする followedIndexList
	 */
	public void appendFollowedList(int index) {
		this.followedList.add(index);
	}

	
	/**リンクが対称であるような無向ネットワークの場合は参照リストと被参照リストどちらにも同時に追加されるので，そのためのメソッド．<br />
	 * 
	 * @param index
	 */
	public void appendUndirectedList (int index) {
		this.appendFollowedList(index);
		this.appendFollowingList(index);
	}
	/**リンクが対称であるような無向ネットワークの場合のリスト取得メソッド．<br />
	 * 追加時に両方に追加されているはずなので，どちらか取ってくればいい．
	 * @param nameOrIndex
	 */
	public ArrayList<Integer> getUndirectedList () {
		return this.getFollowedList();
	}
	
	/**Collection.sortを用いて，2つのリストをソートする．<br />
	 * ネットワーク生成の検証用であり，実際のシミュレーションでは呼ぶ必要はない．
	 * 
	 */
	public void sortLists () {
		Collections.sort(this.followedList);
		Collections.sort(this.followingList);
	}
	/**参照しているエージェントの数を返す。
	 * 
	 * @return
	 */
	public int getnFollowing () {
		return this.followingList.size();
	}
	/**参照されているエージェントの数を返す。
	 * 
	 * @return
	 */
	public int getnFollowed () {
		return this.followedList.size();
	}
	/**無向ネットワークでの接続次数を返す。
	 * 
	 * @return
	 */
	public int getDegree () {
		return this.followedList.size();
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
	public Integer getOpinion() {
		if (!this.isSilent()) {
			return this.opinion;
		} else {
			return null;
		}
	}
	/**サイレント如何を問わず意見を取得する。
	 * @return
	 */
	public Integer forceGetOpinion() {
		return this.opinion;
	}
	/**情報エージェントの意見を指定する。
	 * @param opinion セットする opinion
	 */
	public void setOpinion(Integer opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return influence
	 */
	public double getInfluence() {
		return influence;
	}

	/**
	 * @param influence セットする influence
	 */
	public void setInfluence(double influence) {
		this.influence = influence;
	}
	
}
