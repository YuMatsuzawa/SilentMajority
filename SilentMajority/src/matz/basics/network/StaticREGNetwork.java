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
	 * subjectから見て（片側D/2人分ずつ両方向に）周囲D人分のエージェントのインデックスをArrayList形式で取得する．<br>
	 * Dはコンストラクト時にgivenDegreeで指定される．指定されてなければデフォルト値の6が使われる．<br>
	 * Dが奇数だった場合，2で割った際の端数はこの実装では切り捨てられるので，Dには偶数を入れるべき．
	 * @param subject
	 * @return
	 */
	private ArrayList<Integer> getObjectsOf(int subject) {
		int sideBound = (int)(this.getGivenDegree() / 2);
		ArrayList<Integer> objects = new ArrayList<Integer>();
		for (int d = 1; d <= sideBound; d++) {
			int lowerObject = subject - d, upperObject = subject + d;
			if (lowerObject >= 0) objects.add(lowerObject);
			else {
				lowerObject += this.getnAgents();
				objects.add(lowerObject);
			}
			if (upperObject < this.getnAgents()) objects.add(upperObject);
			else {
				upperObject -= this.getnAgents();
				objects.add(upperObject);
			}
		}
		
		return objects;
	}

	/**
	 * subjectからobjectにリンクを張る．<br>
	 * REGでは自分の周囲に張るという性質上自分中心で見れば事足りるので，<br>
	 * subject->object方向の登録のみ行う．
	 * @param subject
	 * @param object
	 */
	private void constructLink(int subject, int object) {
		if (!this.getUndirectedListOf(subject).contains(object)) {
			this.appendUndirectedListOf(subject, object);
		}
	}
	
	public StaticREGNetwork(int nAgents, boolean orientation, Double degree) {
		super("REG", nAgents, orientation, degree);
		this.build();
	}
	
	public StaticREGNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticREGNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}

}
