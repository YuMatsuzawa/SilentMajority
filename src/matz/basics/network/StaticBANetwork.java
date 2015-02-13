package matz.basics.network;

import java.util.ArrayList;
import java.util.List;


/**
 * Barabasi-Albertモデルに基づき，スケールフリーネットワークを生成するクラス．<br>
 * パラメータは初期ノードの数m_0と，追加するノードのエッジ数m(<=m_0)である．<br>
 * このとき，十分に大きいNに対してはm_0は無視できるようになり，<br>
 * かつノードを1つ追加するとエッジがm本増えることから次数総和は2mずつ増えることになるため，<br>
 * 指定した平均次数degreeに漸近するネットワークを得られるようなmはm=degree/2となる．<br>
 * m_0はm_0=mとして与える．
 * @author Matsuzawa
 *
 */
public class StaticBANetwork extends StaticNetwork {
	
	protected int mEdge;
	
	@Override
	public void build() {
		//初期化のために，追加するノードのエッジ数以上のノードが必要．
		int initHub = this.mEdge;
		for (int initLeaf = 0; initLeaf < this.mEdge; initLeaf++) this.constructLink(initHub,initLeaf); //ネットワークの種
		//あとはPreferential attachment
		for(int subject = initHub + 1; subject < this.getnAgents(); subject++) {
			List<Integer> candidates = new ArrayList<Integer>();
			for (int candidate = 0; candidate < subject; candidate++) candidates.add(candidate);
			int attached = 0;
			while (attached < this.mEdge){
				double roll = this.localRNG.nextDouble();
				double sumCandDegree = 0.0; //分母になる．既に選択された候補ノードを取り除くたびに再計算する
				for (Integer candidate : candidates) sumCandDegree += this.getDegreeOf(candidate);
				double pAttached = 0.0;
				for (Integer candidate : candidates) {
					//ルーレットのポケットを1つずつ順番に見ていくイメージ
					//roll時点でボールの静止位置は確定しており，原点から順に次数の大きさに応じた大きさのポケットを各候補にあてがっていく．
					//ボールの静止位置にポケットを持っていた候補があたりとなる．
					pAttached += (double) this.getDegreeOf(candidate) / sumCandDegree; //この加算値がポケットの幅に相当する
					if (roll < pAttached) {
						this.constructLink(subject, candidate);
						candidates.remove(candidate); //あたりが確定した時点であたった候補を取り除く
						break;
					}
				}
				attached++;
			}
		}
	}

	/**
	 * subjectからobject、objectからsubjectにリンクを張る．<br>
	 * 二重登録がないようにチェックするが、既にリンクが張られたエージェントは接続候補から除かれているはずなので起こりえない．
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}

	public StaticBANetwork(int nAgents, boolean orientation, Double degree) {
		super("BA", nAgents, orientation, degree);
		this.mEdge = (int) (degree / 2);
		this.build();
	}
	
	/**
	 * エージェント数とdegreeを与えるコンストラクタ．
	 * @param nAgents
	 * @param degree
	 */
	public StaticBANetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticBANetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}
}
