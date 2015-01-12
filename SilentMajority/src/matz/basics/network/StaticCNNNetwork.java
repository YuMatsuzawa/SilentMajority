package matz.basics.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * VazquezのCNNモデルに基づき、現実やSNSでの友人関係に近いネットワークを生成するクラス。<br>
 * 確率p_nnで「友人の友人」という関係(潜在リンク)をランダムに選んで接続する操作、<br>
 * 確率1-p_nnで新しいエージェントをランダムに追加する操作を行うアルゴリズム。<br>
 * p_nnはパラメータであるが、与えられていない場合はデフォルト値{@value #P_NN_DEFAULT}を用いる。<br>
 * アルゴリズムから、確率1-p_nnでエージェント数が1増え、次数の総和が2増える一方、<br>
 * p_nnで次数の総和だけが2増えることがわかる。<br>
 * 従って特定の次数degreeに漸近するネットワークにしたい場合は、{@code p_nn = 1 - 2/(degree)}とすれば良い。<br>
 * ただし、エージェント数が十分に大きくない場合は必ずしも良い近似とならない。
 * 
 * @author Yu
 *
 */
public class StaticCNNNetwork extends StaticNetwork {
	
	private double p_nn;
	private static final double P_NN_DEFAULT = 0.666667;
	//private static final double P_NN_DEFAULT = 0.75;
//	private List<Integer[]> potentialLinks = new ArrayList<Integer[]>();
//	private List<Edge> potentialLinks = new ArrayList<Edge>();
//	private Set<Integer[]> potentialLinks = new HashSet<Integer[]>();
	private Set<Edge> potentialLinks = new HashSet<Edge>();
	
	private int numNodes = 0;
	private int numEdges = 0;

	@Override
	public void build() {
		
		//test
//		Edge test1 = new Edge(0, 1);
//		Edge test2 = new Edge(0, 1);
//		System.out.println((test1.equals(test2))? "true" : "false");
//		HashSet<Edge> testSet = new HashSet<StaticNetwork.Edge>();
//		System.out.println((testSet.add(test1))? "added" : "not");
//		System.out.println((testSet.add(test2))? "added" : "not");
		
		//とりあえず無向グラフ. CNNは有向グラフを作るのが難しい．
		if (this.getOrientation() == UNDIRECTED) {
			//ネットワークの種を作る
			this.constructLink(0, 2);
			this.constructLink(1, 2);
			this.numNodes = 3;
			this.numEdges = 2;
			
			//指定された数のエージェントからなるネットワークが出来るまでイテレート
			while(this.numNodes < this.getnAgents()) {
				double roll = this.localRNG.nextDouble();
				if (roll < this.p_nn) {
					this.connectPotential();
				} else {
					this.addNode();
				}
			}
			
		}
	}

	/**
	 * ポテンシャルリンクをリンクにする。
	 */
	private void connectPotential() {
		int listSize = this.potentialLinks.size();
		if (listSize == 0) return;

//		Collections.shuffle(this.potentialLinks);
//		int roll = this.localRNG.nextInt(listSize);
//		Integer[] pLink = this.potentialLinks.get(roll);
//		Edge pLink = this.potentialLinks.get(roll);
//		Edge pLink = this.potentialLinks.get(0);
//		this.potentialLinks.remove(roll);		//rollでランダムなポテンシャルリンクを選び出して，リストから除去
		
//		ArrayList<Integer[]> asList = new ArrayList<Integer[]>(this.potentialLinks);	//サンプリングするために，SetをListに
		ArrayList<Edge> asList = new ArrayList<Edge>(this.potentialLinks);	//サンプリングするために，SetをListに.→ここが多分くっそ遅い?
		Collections.shuffle(asList);
		Edge pLink = asList.get(0);		//shuffleして順番をランダムにし，最初の1つを取得．
//		Edge pLink = asList.get(roll);		//shuffleして順番をランダムにし，最初の1つを取得．
//		Edge pLink = null;
//		for (int i = 0; i <= roll; i++) {
//			pLink = this.potentialLinks.iterator().next();
//		}
		this.potentialLinks.remove(pLink);
		
		this.constructLink(pLink.littleEnd, pLink.bigEnd);		//それをエッジに変換
		asList.clear();
	}

	/**
	 * 新規エージェントをランダムに加える。
	 */
	private void addNode() {
		int target = this.localRNG.nextInt(this.numNodes);
		int newcomer = this.numNodes++;
		this.constructLink(newcomer, target);
		System.out.println(this.numNodes + " " + this.numEdges);
	}

	/**
	 * subjectとobjectの間にリンクを張り、生じるポテンシャルリンクを登録する。<br>
	 * 二重登録がないように細かくチェックする<br>
	 * 互いを互いのリストに漏れ無く登録するので、引数を逆にしてこのメソッドを2回呼ぶ必要はない
	 * @param subject
	 * @param object
	 */
	private void constructLink(int subject, int object) {
		this.appendToUndirectedListOf(subject, object);
		this.appendToUndirectedListOf(object, subject);
		this.numEdges++;
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
//				Integer[] pLink = {pIndex, subject};
//				Integer[] rLink = {subject, pIndex};
				Edge pLink = new Edge(pIndex, subject);
//				boolean isNew = true;
//				for(Integer[] link : this.potentialLinks) {
//					if (Arrays.equals(pLink, link) ||
//							Arrays.equals(rLink, link)) { //両方向ないことを確認し、
//						isNew = false;
//						break;
//					}
//				}
//				if (isNew) this.potentialLinks.add(pLink); //一方向だけ登録。
				
				this.potentialLinks.add(pLink);
				/*potentialLinksがSet系Collectionなら，単にaddしていい．重複はない．
				 * List系なら，重複判定しなければならない．
				 * 一般に，HashSetなどのSetはHash値に基づく階層型の格納構造を持っているので，
				 * List系よりも重複判定は高速である． 
				 */
			}
		}
	}
	
	/**
	 * 操作の選択率を与える基本コンストラクタ。
	 * @param nAgents -エージェント数
	 * @param p_nn -ポテンシャルリンク接続の選択率
	 * @param orientation -指向性
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation, double p_nn) {
		super("CNN", nAgents, orientation, null);
		this.p_nn = p_nn;
		this.build();
	}
	
	/**
	 * 目標平均次数を与える基本コンストラクタ。
	 * @param nAgents
	 * @param orientation
	 * @param degree
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation, Double degree) {
		super("CNN", nAgents, orientation, degree);
		this.p_nn = 1.0 - 2.0 / (this.getGivenDegree());
		this.build();
	}
	/**
	 * デフォルトのp_nnを用いるコンストラクタ。
	 * @param nAgents
	 * @param orientation
	 */
	public StaticCNNNetwork(int nAgents, boolean orientation) {
		this(nAgents, orientation, P_NN_DEFAULT);
	}
	
	/**
	 * エージェント数と平均次数を与えるコンストラクタ．無向グラフ．p_nnは平均次数がdegreeに漸近するよう設定される．
	 * @param nAgents
	 * @param degree
	 */
	public StaticCNNNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	/**
	 * エージェント数を与えて無向グラフを作るコンストラクタ。
	 * @param nAgents
	 */
	public StaticCNNNetwork(int nAgents) {
		this(nAgents, UNDIRECTED);
	}
}
