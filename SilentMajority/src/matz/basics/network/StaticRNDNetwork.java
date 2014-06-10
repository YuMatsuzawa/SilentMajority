package matz.basics.network;

import java.util.ArrayList;

/**
 * Erdos-RenyiモデルのG(n,p)方式に基づき、ランダムネットワークを生成するクラス。<br>
 * この方式ではエッジの有効化確率pを与える必要があるが、次数分布がPoisson分布になるという性質から<br>
 * 次数平均がn->inftyにおいてnpとなることがわかるので、指定された平均次数からpを逆算することが可能である。<br>
 * このクラスではその方式を用いるので、pを直接指定することはできないようにしている。<br>
 * もちろん、nがある程度大きいことは前提として必要である。
 * @author Yu
 *
 */
public class StaticRNDNetwork extends StaticNetwork {
	
	protected double pConnect;
	
	@Override
	public void build() {
		ArrayList<Integer[]> potentialLinks = new ArrayList<Integer[]>(); //可能なリンクを数え上げ。このリストはn^2オーダーで大きくなるので何か別の方法も考えた方がいい。
		for (int subject = 0; subject < this.getnAgents(); subject++) {
			for (int object = subject + 1; object < this.getnAgents(); object++) {
				Integer[] pLink = {subject, object};
				potentialLinks.add(pLink);
			}
		}
		
		for (Integer[] pLink : potentialLinks) {
			double roll = this.localRNG.nextDouble();
			if (roll < pConnect) {
				this.constructLink(pLink[0], pLink[1]);
			}
		}
	}

	/**
	 * subjectからobject、objectからsubjectにリンクを張る．<br>
	 * 二重登録がないようにチェックするが、potentialLinksに二重登録が無いようにしておけば起こりえない。
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}
	
	public StaticRNDNetwork(int nAgents, boolean orientation, Double degree) {
		super("RND", nAgents, orientation, degree);
		this.pConnect = this.getGivenDegree() / (double)this.getnAgents();
		this.build();
	}
	
	/**
	 * エージェント数とdegreeを与えるコンストラクタ．
	 * @param nAgents
	 * @param degree
	 */
	public StaticRNDNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticRNDNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}
}
