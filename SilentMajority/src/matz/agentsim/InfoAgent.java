package matz.agentsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import matz.basics.network.StaticNetwork;

public class InfoAgent {
	
	private int agentIndex;
	private ArrayList<Integer> followingList;
	private ArrayList<Integer> followedList;
	private boolean isSilent;
	private boolean tmpSilent;
	/**抽象的な意見（情報）を表す状態変数．<br>
	 * プリミティブintではなくラッパ型のIntegerにしておき，nullを使えるようにする．
	 */
	private Integer opinion;
	private Integer tmpOpinion;
	private double influence = Math.random();
	private double threshold = Math.random(); //ランダムにする
	private double noiseRatio = 0.05;
	private double bendThreshold = 0.05; //reliefつきmuzzlingで少数派閾値の場合
	private StaticNetwork refNetwork = null;
	private boolean isNetworkStatic = false;
	
	/**自分が中立的・あるいは未定義の状態にあるとき，肯定的・否定的問わず何らかの先進的意見に触れると，それに影響される．<br>
	 * 影響を受けるか否かは，相手の影響力の強さによる．<br>
	 * 隣接リストにいる人を順に参照していき、ヴォーカルな人の中で最も影響力の高い人から影響される。
	 * @return 変化があったらtrue
	 */
	public boolean independentCascade(InfoAgent[] infoAgentsArray) {
		if (!(this.getOpinion() == null || this.getOpinion() == 0)) return false;

		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		double topInfluence = -1;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion();
			double neighborInfluence = infoAgentsArray[(Integer) neighbor].getInfluence();
				//サイレントエージェントのinfluenceは-1と返ってくる。
			//if (preOp == null || preOp == 0) { //自分が態度未決定であるときしかICの影響を受けない，という条件（なぜかついていた）
				if (neighborInfluence > topInfluence && neighborOp != null && neighborOp > 0) { //ここで、相手がサイレントなら不適
					tmpOp = neighborOp;
					topInfluence = neighborInfluence;
				} else {
					continue;
				}
			//}
		}
		this.setTmpOpinion(tmpOp);
		if (!this.getTmpOpinion().equals(preOp)) return true;
		return false;
	}

	/**隣接しているノードの中での多数派を知覚して，その影響を受ける．
	 * @param infoAgentsArray
	 * @return
	 */
	public boolean linearThreashold(InfoAgent[] infoAgentsArray) {
		Integer preOp = this.forceGetOpinion();
		Integer tmpOp = this.forceGetOpinion();
		
		int sum = 0;
		Integer[] opinions = {0,0,0};
		int NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2;
		for (Object neighbor : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[(Integer) neighbor].getOpinion(); 
			if (neighborOp == null) continue; //未定義の人・サイレントな人は勘定しない．
			
			sum++;
			opinions[neighborOp]++;
		}
		if (sum == 0) return false;
/*		for (int opIndex = 0; opIndex < 3; opIndex++) { //単純に，null以外の意見の比率を調べ，閾値を超えているものに付和雷同する
			if (opinions[opIndex] / sum > this.threshold) tmpOp = opIndex;
		}*/
		
		/*
		 * こちらは，1or2の意見がどちらも多数派を勝ち得ていなければ，中立派が多数派であってもなくても中立の立場を取る，というモデル．
		 */
		if (opinions[POS_INDEX] / sum > this.getThreshold()) tmpOp = POS_INDEX;
		else if (opinions[NEG_INDEX] / sum > this.getThreshold()) tmpOp = NEG_INDEX;
		else tmpOp = NEU_INDEX;
		
		this.setTmpOpinion(tmpOp);
		if (!this.getTmpOpinion().equals(preOp)) return true;
		return false;
	}

	/**
	 * 派生シミュレーションのためのメソッド．<br>
	 * LTモデルに基づき，隣接周囲の多数派に反応して，サイレント化したり，ヴォーカル化したりする．<br>
	 * ルール:自分と同じ意見が，<br>
	 * 1.自分の閾値を超える多数派ならヴォーカルになる<br>
	 * 2.自分の閾値を下回る少数派ならサイレントになる<br>
	 * 閾値はランダムに分布しているので，自分と同じ意見がたとえ少数派でもサイレントにならなかったり，<br>
	 * 逆に相当多数派でもヴォーカルにならなかったりするエージェントも発生する．<br>
	 * @param infoAgentsArray
	 * @return 変化すればtrue
	 */
	public boolean linearThreasholdMuzzling(InfoAgent[] infoAgentsArray) {
		boolean preSilent = (this.isSilent())? true : false;
		boolean tmpSilent = (this.isSilent())? true : false;
		
		int sumOfVocal = 0, sumOfVocalizedSameOpinion = 0;
		for (int neighborIndex : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[neighborIndex].getOpinion();
			if (neighborOp == null) continue; //サイレントは無視
			else {
				sumOfVocal++; //nullでないならヴォーカル
				if (neighborOp.equals(this.forceGetOpinion())) {
					sumOfVocalizedSameOpinion++; //自分の真の意見と同じ意見の人を数える
				}
			}
		}
		if (sumOfVocal == 0){
			this.tmpSilent = tmpSilent;
			return false;
		}
		if ((double)sumOfVocalizedSameOpinion / (double)sumOfVocal >= this.getThreshold()) tmpSilent = false;
		else tmpSilent = true;
		
		this.tmpSilent = tmpSilent;
		if (preSilent ^ this.tmpSilent) return true;
		return false;
	}

	/**
	 * 安堵Reliefあるいは他人任せSlackを導入した{@link #linearThreasholdMuzzling(InfoAgent[])}からの派生シミュレーション<br>
	 * 自分の意見が極めて少数派である場合サイレントになり，<br>
	 * ある程度市民権を得るとヴォーカルになり，<br>
	 * 更に長じて安心出来るだけの多数派が回りにいると感じるとまたサイレントになる．
	 * @param infoAgentsArray
	 */
	public boolean linearThresholdMuzzlingWithRelief(InfoAgent[] infoAgentsArray, double reliefRatio) {
		boolean preSilent = (this.isSilent())? true : false;
		boolean tmpSilent = (this.isSilent())? true : false;
		
		int sumOfVocal = 0, sumOfVocalizedSameOpinion = 0;
		for (int neighborIndex : this.getUndirectedList()) {
			Integer neighborOp = infoAgentsArray[neighborIndex].getOpinion();
			if (neighborOp == null) continue; //サイレントは無視
			else {
				sumOfVocal++; //nullでないならヴォーカル
				if (neighborOp.equals(this.forceGetOpinion())) sumOfVocalizedSameOpinion++;
					//自分の真の意見と同じ意見の人を数える
			}
		}
		if (sumOfVocal == 0) {
			this.tmpSilent = tmpSilent;
			return false;
		}
		if ((double)sumOfVocalizedSameOpinion / (double)sumOfVocal > this.bendThreshold &&
				(double)sumOfVocalizedSameOpinion / (double)sumOfVocal < reliefRatio) tmpSilent = false; //黙ってしまうほど少なくもないが，安心できるほど多くもないときにヴォーカルになる
		else tmpSilent = true;
		
		this.tmpSilent = tmpSilent;
		if (preSilent ^ this.tmpSilent) return true;
		return false;
	}

	/**
	 * 相互作用が起こらなかった場合に呼ばれる、ランダムな自発変化。<br>
	 * 要はノイズ項。事前状態を勘案せず、ランダムにサイレントかヴォーカルにする。<br>
	 * @return
	 */
	public boolean randomUpdate(Random localRNG) {
		boolean ret = false;
		double roll = localRNG.nextDouble();
		if (roll < this.noiseRatio) {
			int rollInt = localRNG.nextInt(2);
			if (rollInt == 0) this.tmpSilent = true;
			else this.tmpSilent = false;
			ret = true;
		}
		return ret;
	}
	
	/**
	 * 上の派生版．ランダムにヴォーカルにするがサイレントにしない．
	 * @param localRNG
	 * @return
	 */
	public boolean randomUnmuzzle(Random localRNG) {
		boolean ret = false;
		double roll = localRNG.nextDouble();
		if (roll < this.noiseRatio) {
			this.tmpSilent = false;
			ret = false;
		}
		return ret;
	}
	
	//文字列名を与えるスタイルはやめる.
	
	/**
	 * 基本コンストラクタ．
	 * @param index -整数の識別番号
	 * @param opinion -整数値の意見
	 * @param isSilent -サイレントであるか
	 * @param ntwk -参照する静的ネットワーク
	 */
	public InfoAgent(int index, Integer opinion, boolean isSilent, StaticNetwork ntwk) {
		this.setAgentIndex(index);
		if (ntwk == null) {
			this.isNetworkStatic = false;
			this.initFollowingList();
			this.initFollowedList();
		} else {
			this.isNetworkStatic = true;
			this.refNetwork = ntwk;
		}
		this.setOpinion(opinion);
		this.setTmpOpinion(this.forceGetOpinion());
		if (isSilent) this.muzzle();
	}
	
	/**
	 * すべてヴォーカルとし、静的ネットワークを与えるコンストラクタ。
	 * @param index
	 * @param opinion
	 * @param ntwk
	 */
	public InfoAgent(int index, Integer opinion, StaticNetwork ntwk) {
		this(index, opinion, false, ntwk);
	}
	
	/**
	 * すべてヴォーカルとし、シミュレーション個別のネットワークを使用するコンストラクタ。
	 * @param index -整数の識別番号
	 * @param opinion -整数値の意見
	 */
	public InfoAgent(int index, Integer opinion) {
		this(index, opinion, false, null);
	}
	
	/*
	 * 以下getter/setter及びその他のメソッド
	 * 
	 */
	
	
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
	
	/*
	 * 以下，リスト関連のメソッド． 初期化メソッドはprivateとする．
	 */	
	
	/**情報エージェントが参照しているエージェントの整数インデックスリストを取得。
	 * @return followingIndexList
	 */
	public List<Integer> getFollowingList() {
		return (this.isNetworkStatic)? this.refNetwork.getFollowingListOf(this.getAgentIndex()) : this.followingList;
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
	public List<Integer> getFollowedList() {
		return (this.isNetworkStatic)? this.refNetwork.getFollowedListOf(this.getAgentIndex()) : this.followedList;
	}
	/**情報エージェントが参照されているエージェントの整数インデックスリストを空のリストに初期化。
	 * @param followedList セットする followedIndexList
	 */
	private void initFollowedList() {
		this.followedList = new ArrayList<Integer>();
	}
	/**
	 * 情報エージェントが参照されているエージェントの整数インデックスリストに新たなエージェントの整数インデックスを追加。
	 * @param followedList セットする followedIndexList
	 */
	public void appendFollowedList(int index) {
		this.followedList.add(index);
	}

	
	/**
	 * リンクが対称であるような無向ネットワークの場合は参照リストと被参照リストどちらにも同時に追加されるので，そのためのメソッド．<br>
	 * @param index
	 */
	public void appendUndirectedList (int index) {
		this.appendFollowedList(index);
		this.appendFollowingList(index);
	}
	/**
	 * リンクが対称であるような無向ネットワークの場合のリスト取得メソッド．<br>
	 * 追加時に両方に追加されているはずなので，どちらか取ってくればいい．
	 * @param nameOrIndex
	 */
	public List<Integer> getUndirectedList () {
		return this.getFollowedList();
	}
	
	/**
	 * Collection.sortを用いて，2つのリストをソートする．<br>
	 * ネットワーク生成の検証用であり，実際のシミュレーションでは呼ぶ必要はない．
	 */
	public void sortLists () {
		Collections.sort(this.followedList);
		Collections.sort(this.followingList);
	}
	/**
	 * 参照しているエージェントの数を返す。
	 * @return
	 */
	public int getnFollowing () {
		return (this.isNetworkStatic)? this.refNetwork.getnFollowedOf(this.getAgentIndex()) : this.followingList.size();
	}
	/**
	 * 参照されているエージェントの数を返す。
	 * @return
	 */
	public int getnFollowed () {
		return (this.isNetworkStatic)? this.refNetwork.getnFollowedOf(this.getAgentIndex()) : this.followedList.size();
	}
	/**
	 * 無向ネットワークでの接続次数を返す。
	 * @return
	 */
	public int getDegree () {
		return (this.isNetworkStatic)? this.refNetwork.getDegreeOf(this.getAgentIndex()) : this.followedList.size();
	}
	
	
	/**
	 * 情報エージェントがサイレントであればtrue，ヴォーカルであればfalseを返す<br>
	 * デフォルトはfalse
	 * @return isSilent
	 */
	public boolean isSilent() {
		return this.isSilent;
	}
	/**
	 * 情報エージェントをサイレントにする。
	 */
	public void muzzle() {
		this.isSilent = true;
	}
	/**
	 * 情報エージェントをヴォーカルにする。
	 */
	public void unmuzzle() {
		this.isSilent = false;
	}
	/**
	 * 外から情報エージェントの現在の意見を取得する。シミュレーション用。<br>
	 * エージェントがサイレントである場合は取得できない。null値を返す
	 * @return opinion
	 */
	public Integer getOpinion() {
		if (this.isSilent()) return null;
		return this.forceGetOpinion();
	}
	/**
	 * サイレント如何を問わず意見を取得する。記録用。
	 * @return
	 */
	public Integer forceGetOpinion() {
		return this.opinion;
	}
	/**
	 * 情報エージェントの意見を指定する。
	 * @param opinion セットする opinion
	 */
	public void setOpinion(Integer opinion) {
		this.opinion = opinion;
	}
	/**
	 * 中間意見を確定意見として適用する。イテレータの最後に呼ぶ。
	 */
	public void applyOpinion() {
		if (this.tmpOpinion != null) this.setOpinion(this.tmpOpinion);
	}
	/**
	 * 中間意見を取得する。意見変化をチェックするために使う。
	 */
	public Integer getTmpOpinion() {
		return this.tmpOpinion;
	}
	/**
	 * 一時的に意見を格納する。イテレータの中間データの保存に使う。
	 * @param tmpOp
	 */
	private void setTmpOpinion(Integer tmpOp) {
		this.tmpOpinion = tmpOp;
	}

	/**
	 * 派生シミュレーションのためのメソッド．<br>
	 * {@link #linearThreasholdMuzzling(InfoAgent[])}で決定したmuzzlingの中間状態を本適用する．
	 */
	public void applyMuzzling() {
		if (this.tmpSilent) this.muzzle();
		else this.unmuzzle();
	}
	
	/**
	 * 影響力を取得する。サイレントなら-1を返す。この-1という数字はイテレータ側で影響力最大のエージェントを探す際の比較に引っかからないために指定されている。
	 * @return influence
	 */
	public double getInfluence() {
		return (this.isSilent())? -1 : this.influence;
	}

	/**
	 * 影響力を指定する。
	 * @param influence セットする influence
	 */
	public void setInfluence(double influence) {
		this.influence = influence;
	}

	/**
	 * 閾値を取得する．
	 * @return threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * 閾値を指定する．閾値を意見によって変化させるシミュレーションで用いる．
	 * @param threshold セットする threshold
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
