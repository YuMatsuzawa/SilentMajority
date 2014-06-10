package matz.agentsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import matz.basics.network.NetworkBuilder;

/**InfoAgentクラスで作られたエージェント間にリンクを張り,その参照関係を各エージェントの持つリストに記録していく.
 * パラメータとして，あるタイムステップで「友達の友達」間にリンクを張るか,「全く無関係or遠い関係の二者」間に張るかの選択閾値を持つ．
 * @param infoAgentsArray
 */
public class CNNNetworkBuilder implements NetworkBuilder {
	private double p_nn;
	private static final double P_NN_DEFAULT = 0.666667;
	private ArrayList<Integer[]> potentialLinks = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
	private int includedAgents;
	private boolean isDirected;
	
	public InfoAgent[] build(InfoAgent[] infoAgentsArray) {
		InfoAgent[] tmpAgentsArray = infoAgentsArray;
		int nAgents = infoAgentsArray.length;
		
		if (this.getOrientation() == UNDIRECTED) {
			/*ネットワークの種を作る（とりあえず無向グラフ）
			 * 
			 * 0----2----1
			 * 
			 */
			tmpAgentsArray[0].appendUndirectedList(2);
			tmpAgentsArray[1].appendUndirectedList(2);

			tmpAgentsArray[2].appendUndirectedList(0);
			tmpAgentsArray[2].appendUndirectedList(1);

			Integer[] firstPLink = {0, 1};
			this.potentialLinks.add(firstPLink);

			this.includedAgents = 3;

			while (this.includedAgents < nAgents) {
				double roll = this.localRNG.nextDouble();
				if (roll < this.getP_nn()){
					this.connectPotential(tmpAgentsArray);
				} else {
					this.includeAgent(tmpAgentsArray);
				}
			}

			//チェックのために，全エージェントの隣接リストをソートする．
			for (InfoAgent agent : tmpAgentsArray) {
				agent.sortLists();
			}
		}
		
		return tmpAgentsArray;
	}
	
	/**ポテンシャルリンクを実際に接続する．
	 * @param tmpAgentsArray
	 */
	private void connectPotential(InfoAgent[] tmpAgentsArray) {
		int listSize = this.getPotentialLinksLength();
		if (listSize == 0) return;
		
		int roll = this.localRNG.nextInt(listSize);
		Integer[] pLink = this.potentialLinks.get(roll);
		this.potentialLinks.remove(roll);
			//rollで適当なポテンシャルリンクを選び出し，これをエッジに変換する．

		this.constructLink(pLink[0], pLink[1], tmpAgentsArray);
	}
	/**まだ接続されていないエージェントをランダムに加える．
	 * @param tmpAgentsArray
	 */
	private void includeAgent(InfoAgent[] tmpAgentsArray) {	
		int target = this.localRNG.nextInt(this.includedAgents);
		int newcomer = this.includedAgents++;

		this.constructLink(newcomer, target, tmpAgentsArray);
	}
	/**targetとnewcomoerとの間にリンクを張り，生じるポテンシャルリンクを登録する.<br>
	 * 二重登録がないよう細かくチェックする．<br>
	 * 互いのインデックスを一度に互いの隣接リストに漏れ無く登録するので，このメソッドを引数を逆にして二度呼ぶ必要はないし，呼んではならない．
	 * @param newcomer
	 * @param target
	 * @param tmpAgentsArray
	 */
	private void constructLink(int newcomer, int target, InfoAgent[] tmpAgentsArray) {
		tmpAgentsArray[newcomer].appendUndirectedList(target);
		tmpAgentsArray[target].appendUndirectedList(newcomer);
		safeAppendPotentialLink(newcomer,target,tmpAgentsArray);
		safeAppendPotentialLink(target,newcomer,tmpAgentsArray);
	}
	/**重複がないよう確認しながらポテンシャルリンクを追加する．<br>
	 * 自分からみた際のポテンシャルリンクを登録するために一度，相手から見た際のポテンシャルリンクを登録するためにインデックス引数を逆にしてもう一度呼ぶ必要がある．
	 * @param newcomer
	 * @param target
	 * @param tmpAgentsArray
	 */
	private void safeAppendPotentialLink(int newcomer, int target, InfoAgent[] tmpAgentsArray) {
		for(int pIndex : tmpAgentsArray[target].getUndirectedList()) {
			if (pIndex != newcomer && !tmpAgentsArray[newcomer].getUndirectedList().contains(pIndex)) {
				Integer[] pLink = {pIndex, newcomer};
				Integer[] rLink = {newcomer, pIndex};
				boolean isNew = true;
				for(Integer[] link : this.potentialLinks) { //ArrayList<Integer[]>での重複回避はこういった方法でないとダメ.ノート参照
					if (Arrays.equals(pLink, link) || Arrays.equals(rLink, link)) {
						isNew = false;
						break;
					}
				}
				if (isNew) this.potentialLinks.add(pLink);
			}
		}
	}
	
	/**確率パラメータと指向性を与えてネットワークをコンストラクト．
	 * 
	 * @param infoAgentsArray
	 */
	public CNNNetworkBuilder(double p_nn, boolean isDirected) {
		this.setP_nn(p_nn);
		this.setOrientation(isDirected);
	}

	/**指向性を与えてネットワークをコンストラクト．<br>
	 * 2/3の確率で「友達の友達」，1/3の確率でそれ以外をリンクする．
	 */
	public CNNNetworkBuilder(boolean isDirected) {
		this.setP_nn(P_NN_DEFAULT);
		this.setOrientation(UNDIRECTED);
	}

	/**デフォルトの確率パラメータで無向ネットワークをコンストラクト．<br>
	 * 2/3の確率で「友達の友達」，1/3の確率でそれ以外をリンクする．
	 */
	public CNNNetworkBuilder() {
		this.setP_nn(P_NN_DEFAULT);
		this.setOrientation(UNDIRECTED);
	}
	
	/**確率パラメータを取得．
	 * @return p_nn
	 */
	public double getP_nn() {
		return p_nn;
	}
	/**確率パラメータを指定．
	 * @param p_nn セットする p_nn
	 */
	public void setP_nn(double p_nn) {
		this.p_nn = p_nn;
	}
	/**有向か無向かを取得。
	 * 
	 * @param isDirected
	 */
	public boolean getOrientation() {
		return this.isDirected;
		
	}
	/**有向か無向かを指定。
	 * 
	 * @param isDirected
	 */
	public void setOrientation(boolean isDirected) {
		this.isDirected = isDirected;
		
	}
	/**現在使用されている方のポテンシャルリンクのサイズを返す．
	 * @return
	 */
	private int getPotentialLinksLength() {
		int length = 0;
		length = (this.potentialLinks.size() > 0)? this.potentialLinks.size() : length;
		return length;
	}

}