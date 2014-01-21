package matz.agentsim;

import java.util.ArrayList;
import java.util.Random;


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
		
		Integer[] initpLink = {0, 1};
		this.appendPotentialLinks(initpLink, style);
		
		this.includedAgents = 3;
		
		while (this.includedAgents < nAgents) {
			double roll = this.localRNG.nextDouble();
			if (roll < this.getP_nn()){
				this.connectPotential(tmpAgentsArray);
			} else {
				this.includeAgent(tmpAgentsArray);
			}
		}
		
		return tmpAgentsArray;
	}
	
	/**潜在的リンクを実際に接続する．
	 * @param tmpAgentsArray
	 */
	private void connectPotential(InfoAgent[] tmpAgentsArray) {
		int listSize = this.getPotentialLinksLength(); 
		if (listSize == 0) return;
		
		int roll = this.localRNG.nextInt(listSize);
		Integer[] pLink = this.PotentialLinksByIndex.get(roll);
		/*int index1,index2;
		index1 = this.PotentialLinksByIndex.get(roll)[0];
		index2 = this.PotentialLinksByIndex.get(roll)[1];
		this.PotentialLinksByIndex.remove(roll);
		Integer[] pLink = {index1, index2};*/
		Integer[] rLink = {pLink[1], pLink[0]};
		while (this.PotentialLinksByIndex.contains(pLink) || this.PotentialLinksByIndex.contains(rLink)) {
			int index = this.PotentialLinksByIndex.indexOf(pLink);
			if (index == -1) index = this.PotentialLinksByIndex.indexOf(rLink);
			this.PotentialLinksByIndex.remove(index);
		}
			//rollで適当なポテンシャルリンクを選び出し，これをエッジに変換する．
			//ちょっと問題あるが，もし逆表記された同じポテンシャルリンクがあったならこれも同時に掃除する．
			//TODO 時間がないので，Indexベースの方のみ作る．後でNameベースの方にも対応するよう整合性を取る．

		this.safeAppendIndexPotentialLink(pLink[0], pLink[1], tmpAgentsArray);
		this.safeAppendIndexPotentialLink(pLink[1], pLink[0], tmpAgentsArray);
		if (!tmpAgentsArray[pLink[0]].getIndirectedList().contains(pLink[1])) tmpAgentsArray[pLink[0]].appendIndirectedList(pLink[1]);
			//FIXME どうしてか，隣接ノード追加が重複することがある．これは対処療法なので原因を探る
		if (!tmpAgentsArray[pLink[1]].getIndirectedList().contains(pLink[0])) tmpAgentsArray[pLink[1]].appendIndirectedList(pLink[0]);
	}
	/**まだ接続されていないエージェントをランダムに加える．
	 * @param tmpAgentsArray
	 */
	private void includeAgent(InfoAgent[] tmpAgentsArray) {	
		int target = this.localRNG.nextInt(this.includedAgents);
		int newComer = this.includedAgents++;

		this.safeAppendIndexPotentialLink(newComer, target, tmpAgentsArray);
		if (!tmpAgentsArray[target].getIndirectedList().contains(newComer)) tmpAgentsArray[target].appendIndirectedList(newComer);
		if (!tmpAgentsArray[newComer].getIndirectedList().contains(target)) tmpAgentsArray[newComer].appendIndirectedList(target);
	}
	/**index2の隣接リストを走査し，index1との間にポテンシャルリンクを張る．
	 * @param index1
	 * @param index2
	 * @param tmpAgentsArray
	 */
	private void safeAppendIndexPotentialLink(int index1, int index2, InfoAgent[] tmpAgentsArray) {
		for (Object pObj : tmpAgentsArray[index2].getIndirectedList()) {
			int pIndex = (Integer) pObj;
			if (pIndex == index1 || tmpAgentsArray[index1].getIndirectedList().contains(pIndex)) continue;
			Integer[] pLink = {index1, pIndex};
			Integer[] rLink = {pIndex, index1};	//追加の際，逆向きに表記された等価なリンクがないことも確認する．
			if (this.PotentialLinksByIndex.contains(pLink) || this.PotentialLinksByIndex.contains(rLink)) continue;
			this.appendPotentialLinks(pLink, INDEX_BASED);
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
	/**現在使用されている方のポテンシャルリンクのサイズを返す．
	 * @return
	 */
	private int getPotentialLinksLength() {
		int length = 0;
		length = (this.PotentialLinksByName.size() > 0)? this.PotentialLinksByName.size() : length;
		length = (this.PotentialLinksByIndex.size() > 0)? this.PotentialLinksByIndex.size() : length;
		return length;
	}
	/**ポテンシャルリンクのリストに追加する．スタイルを判別する．
	 * @param nameOrIndex
	 */
	public <SorI> void appendPotentialLinks (SorI[] nameOrIndex, int style) {
		if (style == NAME_BASED) {
			this.PotentialLinksByName.add((String[]) nameOrIndex);
		} else {
			this.PotentialLinksByIndex.add((Integer[]) nameOrIndex);
		}
	}

}