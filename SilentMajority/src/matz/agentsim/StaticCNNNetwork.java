package matz.agentsim;

import java.util.*;

import matz.basics.StaticNetwork;

public class StaticCNNNetwork extends StaticNetwork {
	
	private double p_nn;
	private static final double P_NN_DEFAULT = 0.666667;
	private List<Integer[]> potentialLinks = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
	private int includedAgents;

	@Override
	public void build() {
		//とりあえず無向グラフ
		if (this.getOrientation() == UNDIRECTED) {
			//ネットワークの種を作る
			this.appendUndirectedListOf(0, 2);
			this.appendUndirectedListOf(1, 2);
			this.appendUndirectedListOf(2, 0);
			this.appendUndirectedListOf(2, 1);
			Integer[] firstPLink = {0, 1};
			this.potentialLinks.add(firstPLink);
			this.includedAgents = 3;
			
			//指定された数のエージェントからなるネットワークが出来るまでイテレート
			while(this.includedAgents < this.getnAgents()) {
				double roll = this.localRNG.nextDouble();
				if (roll < this.p_nn) {
					this.connectPotential();
				} else {
					this.includeAgent();
				}
			}
			
			//チェックのために、全エージェントの隣接リストをソートする。
			for (List<Integer>[] agentLists : networkList) {
				Collections.sort(agentLists[FOLLOWED_INDEX]);
				Collections.sort(agentLists[FOLLOWING_INDEX]);
			}
		}
	}

	/**
	 * ポテンシャルリンクをリンクにする。
	 */
	private void connectPotential() {
		int listSize = this.potentialLinks.size();
		if (listSize == 0) return;
		
		int roll = this.localRNG.nextInt(listSize);
		Integer[] pLink = this.potentialLinks.get(roll);
		this.potentialLinks.remove(roll);
			//rollでランダムなポテンシャルリンクを選び出し
		this.constructLink(pLink[0], pLink[2]);
			//それをエッジに変換
		
	}

	/**
	 * 新規エージェントをランダムに加える。
	 */
	private void includeAgent() {
		int target = this.localRNG.nextInt(this.includedAgents);
		int newcomer = this.includedAgents++;
		this.constructLink(newcomer, target);
	}

	/**
	 * subjectとobjectの間にリンクを張り、生じるポテンシャルリンクを登録する。<br>
	 * 二重登録が内容細かくチェックする<br>
	 * 互いを互いのリストに漏れ無く登録するので、引数を逆にしてこのメソッドを2回呼ぶ必要はない
	 * @param subject
	 * @param object
	 */
	private void constructLink(int subject, int object) {
		this.appendUndirectedListOf(subject, object);
		this.appendUndirectedListOf(object, subject);
		this.safeAppendPotentialLink(subject, object);
		this.safeAppendPotentialLink(object, subject);
	}

	/**
	 * 重複なきよう確認しながらポテンシャルリンクを追加。<br>
	 * subjectから見た場合と、objectから見た場合、両方のポテンシャルリンクを登録するために、<br>
	 * 引数を逆にして2回呼ぶ必要がある。
	 * @param subject
	 * @param object
	 */
	private void safeAppendPotentialLink(int subject, int object) {
		for(int pIndex : this.getUndirectedListOf(object)) { 
			if (pIndex != subject &&
				!this.getUndirectedListOf(subject).contains(pIndex)) {
				Integer[] pLink = {pIndex, subject};
				Integer[] rLink = {subject, pIndex};
				boolean isNew = true;
				for(Integer[] link : this.potentialLinks) {
					if (Arrays.equals(pLink, link) ||
							Arrays.equals(rLink, link)) { //両方向ないことを確認し、
						isNew = false;
						break;
					}
				}
				if (isNew) this.potentialLinks.add(pLink); //一方向だけ登録。
			}
		}
	}

	/**
	 * エージェント数、確率パラメータ、指向性を与えるコンストラクタ。
	 * @param nAgents
	 */
	public StaticCNNNetwork(int nAgents, double p_nn, boolean orientation) {
		super(nAgents);
		this.p_nn = p_nn;
		this.setOrientation(orientation);
		this.build();
	}
	
	/**
	 * エージェント数のみ与えて無向グラフを作るコンストラクタ。
	 * @param nAgents
	 */
	public StaticCNNNetwork(int nAgents) {
		this(nAgents, P_NN_DEFAULT, UNDIRECTED);
		this.build();
	}
}
