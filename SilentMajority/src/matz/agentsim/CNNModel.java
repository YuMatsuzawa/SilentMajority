package matz.agentsim;

import java.util.ArrayList;
import java.util.Random;

import matz.agentsim.RunnableSimulator.InfoNetworkBuilder;



/**InfoAgentクラスで作られたエージェント間にリンクを張り,その参照関係を各エージェントの持つリストに記録していく.
 * パラメータとして，あるタイムステップで「友達の友達」間にリンクを張るか,「全く無関係or遠い関係の二者」間に張るかの選択閾値を持つ．
 * @param infoAgentsArray
 */
public class CNNModel implements InfoNetworkBuilder {
	private double p_nn;
	private final double P_NN_DEFAULT = 0.667;
	private ArrayList<String[]> PotentialLinksByName = new ArrayList<String[]>();
	private ArrayList<Integer[]> PotentialLinksByIndex = new ArrayList<Integer[]>();
	private Random localRNG = new Random();
	private int includedAgents;
//	private InfoAgent[] infoAgentsArray;
	
	/**CNNモデルでネットワーク生成するクラス．確率パラメータを与えるコンストラクタ．
	 * build()メソッドがメインとなる生成イテレータ．
	 * @param infoAgentsArray
	 */
	public CNNModel(double p_nn) {
		this.setP_nn(p_nn);
	}
	/**デフォルトの確率パラメータでコンストラクト．
	 * 
	 */
	public CNNModel() {
		this.setP_nn(this.P_NN_DEFAULT);
	}
	
	public InfoAgent[] build(InfoAgent[] infoAgentsArray) {
		InfoAgent[] tmpAgentsArray = infoAgentsArray;
		int style = infoAgentsArray[0].getStyle();
		int nAgents = infoAgentsArray.length;
		
		//ネットワークの種を作る（とりあえず無向グラフ）
		tmpAgentsArray[0].appendIndirectedList(2);
		tmpAgentsArray[1].appendIndirectedList(2);

		tmpAgentsArray[2].appendIndirectedList(0);
		tmpAgentsArray[2].appendIndirectedList(1);
		
		while (includedAgents < nAgents) {
			double roll = localRNG.nextDouble();
			if (roll < this.getP_nn()){
				this.connectPotential(style);
			} else {
				this.includeAgent(style);
			}
			
		}
		
		return tmpAgentsArray;
	}
	
	/**潜在的リンクを実際に接続する．
	 * @param style
	 */
	private void connectPotential(int style) {
		if (style == NAME_BASED) {
			
		} else if (style == INDEX_BASED) {
			
		}
		
	}
	/**まだ接続されていないエージェントをランダムに加える．
	 * @param style
	 */
	private void includeAgent(int style) {
		
		if (style == NAME_BASED) {
			
		} else if (style == INDEX_BASED) {
			
		}
		
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

}