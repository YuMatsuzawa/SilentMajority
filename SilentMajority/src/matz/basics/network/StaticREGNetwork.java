package matz.basics.network;

import java.util.ArrayList;

public class StaticREGNetwork extends StaticNetwork {

	@Override
	public void build() {
		if (this.getOrientation() == UNDIRECTED) {
			for (int subject = 0; subject < this.getnAgents(); subject++) {
				for (int object : this.getObjectsOf(subject)) {
					this.constructLink(subject, object);
				}
			}
		}
		
	}

	/**
	 * subjectから見て、indexの下流側（昇順側）D/2人分のエージェントのインデックスをArrayList形式で取得する．<br>
	 * Dはコンストラクト時にgivenDegreeで指定される．指定されてなければデフォルト値の6が使われる．<br>
	 * Dが奇数だった場合，2で割った際の端数はこの実装では切り捨てられるので，Dには偶数を入れるべき．
	 * @param subject
	 * @return
	 */
	private ArrayList<Integer> getObjectsOf(int subject) {
		int sideBound = (int)(this.getGivenDegree() / 2);
		ArrayList<Integer> objects = new ArrayList<Integer>();
		for (int d = 1; d <= sideBound; d++) {
			int upperObject = subject + d;
			if (upperObject < this.getnAgents()) objects.add(upperObject);
			else {
				upperObject -= this.getnAgents();
				objects.add(upperObject);
			}
		}
		
		return objects;
	}

	/**
	 * subjectからobject、objectからsubjectにリンクを張る．<br>
	 * 二重登録がないようにチェックするが、片側のみ（indexの下流側のみ）に向かって接続していけば二重登録は起こりえない。
	 * @param subject
	 * @param object
	 */
	protected void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) this.appendToUndirectedListOf(subject, object);
		if (!this.getUndirectedListOf(object).contains(subject)) this.appendToUndirectedListOf(object, subject);
	}
	
	/**
	 * 基本コンストラクタ。WSモデルでも使えるようにしてある。
	 * @param ntwkName
	 * @param nAgents
	 * @param orientation
	 * @param degree
	 */
	protected StaticREGNetwork(String ntwkName, int nAgents, boolean orientation, Double degree) {
		super(ntwkName, nAgents, orientation, degree);
		this.build();
	}
	
	public StaticREGNetwork(int nAgents, boolean orientation, Double degree) {
		this("REG", nAgents, orientation, degree);
	}
	
	public StaticREGNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticREGNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}

}
