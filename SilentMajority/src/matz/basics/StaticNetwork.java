package matz.basics;

import java.util.ArrayList;

/**
 * 外部から参照可能な静的ネットワークマップを生成し，保持するクラス．<br>
 * エージェント数をintで与えてコンストラクトする．<br>
 * ネットワークマップを生成するbuild()メソッドを実装する．<br>
 * ネットワークマップはArrayListの配列で管理する．<br>
 * ArrayListの配列から以下のgetterで値やリストを取得できる．<br>
 * getUndirectedListOf(int),getFollowedListOf(int),getFollowingListOf(int),<br>
 * getDegreeOf(int),getnFollowedOf(int),getnFollowingOf(int)
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	
	private ArrayList<Integer> networkList[][] = null;
		// 総称型の配列なので扱いに注意する．意味論的に使いやすいのでこうしているが，本来あまりやらないほうがいいらしい
	
	public abstract void build();
	
	/**
	 * エージェント数を与えるコンストラクタ.エージェント数を与えないコンストラクタはない.
	 * @param nAgents
	 */
	@SuppressWarnings("unchecked")
	public StaticNetwork(int nAgents) {
		networkList = new ArrayList[nAgents][2];
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) networkList[i][j] = new ArrayList<Integer>();
		}
	}
	
	// TODO ネットワークデータを取得できる場合，それを元にコンストラクトできるような実装
	
	/**
	 * 有向グラフにおける被参照リストを返す.
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getFollowedListOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * 有向グラフにおける参照リストを返す.
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getFollowingListOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX];
	}
	
	/**
	 * 無向グラフにおける隣接リストを返す．内部的には有向グラフの被参照リストと同じものを返す．
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getUndirectedListOf(int index) {
		return this.getFollowedListOf(index);
	}
	
	/**
	 * 有向グラフにおける被参照数（入次数）を返す．
	 * @param index
	 * @return
	 */
	public int getnFollowedOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX].size();
	}
	
	/**
	 * 有向グラフにおける参照数（出次数）を返す．
	 * @param index
	 * @return
	 */
	public int getnFollowingOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX].size();
	}
	
	/**
	 * 無向グラフにおける次数を返す．内部的には有向グラフの被参照数と同じものを返す．
	 * @param index
	 * @return
	 */
	public int getDegreeOf(int index) {
		return this.getnFollowedOf(index);
	}
}
