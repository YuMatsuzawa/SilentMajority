package matz.basics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 外部から参照可能な静的ネットワークマップを生成し，保持するクラス．<br>
 * エージェント数をintで与えてコンストラクトする．<br>
 * ネットワークマップを生成するbuild()メソッドと、<br>
 * ネットワークの各種データをファイル・画像等に書き出すdumpNetwork()を実装する．<br>
 * ネットワークマップはArrayListの配列で管理する．<br>
 * ArrayListの配列から以下のgetterで値やリストを取得できる．<br>
 * getUndirectedListOf(int),getFollowedListOf(int),getFollowingListOf(int),<br>
 * getDegreeOf(int),getnFollowedOf(int),getnFollowingOf(int)
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	protected static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	protected static boolean DIRECTED = true, UNDIRECTED = false; 
	
	/**
	 * 静的ネットワークを保持するArrayListの配列。<br>
	 * 総称型の配列なので扱いに注意する．意味論的に使いやすいのでこうしているが，本来あまりやらないほうがいいらしい<br>
	 */
	protected List<Integer> networkList[][] = null;
	protected boolean orientation = UNDIRECTED;
	private int nAgents;

	protected TreeMap<Integer, Integer> nFollowedFreqMap = new TreeMap<Integer,Integer>(); //TreeMapはKeyを昇順に順序付けするので、
	protected TreeMap<Integer, Integer> nFollowingFreqMap = new TreeMap<Integer,Integer>();
	
	public abstract void build();
	public abstract void dumpNetwork(File outDir);
	
	/**
	 * エージェント数と指向性を与えるコンストラクタ.エージェント数を与えないコンストラクタはない.<br>
	 * 
	 * @param nAgents
	 * @param orientation - 有向ならtrue,無向ならfalse
	 */
	@SuppressWarnings("unchecked")
	public StaticNetwork(int nAgents, boolean orientation) {
		this.setnAgents(nAgents);
		this.setOrientation(orientation);
		networkList = new ArrayList[nAgents][2];
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) networkList[i][j] = new ArrayList<Integer>();
		}
	}
	
	/**
	 * エージェント数のみ与えて無向グラフを作るコンストラクタ。
	 * @param nAgents
	 */
	public StaticNetwork(int nAgents) {
		this(nAgents, UNDIRECTED);
	}
	
	// TODO ネットワークデータを取得できる場合，それを元にコンストラクトできるような実装
	
	/**
	 * エージェント数を取得する。
	 * @return nAgents
	 */
	public int getnAgents() {
		return nAgents;
	}

	/**
	 * エージェント数を指定する。
	 * @param nAgents セットする nAgents
	 */
	public void setnAgents(int nAgents) {
		this.nAgents = nAgents;
	}

	/**
	 * ネットワークの指向性を取得。
	 * @return orientation
	 */
	public boolean getOrientation() {
		return orientation;
	}

	/**
	 * ネットワークの指向性を指定
	 * @param orientation セットする orientation
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

	/**
	 * subjectの被参照リストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendFollowedListOf(int subject, int object) {
		networkList[subject][FOLLOWED_INDEX].add(object);
	}
	/**
	 * subjectの参照リストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendFollowingListOf(int subject, int object) {
		networkList[subject][FOLLOWING_INDEX].add(object);
	}
	/**
	 * 無向グラフで、subjectの両方向のリストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendUndirectedListOf(int subject, int object) {
		this.appendFollowedListOf(subject, object);
		this.appendFollowingListOf(subject, object);
	}
	/**
	 * 有向グラフにおける被参照リストを返す.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowedListOf(int index) {
		return networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * 有向グラフにおける参照リストを返す.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowingListOf(int index) {
		return networkList[index][FOLLOWING_INDEX];
	}
	
	/**
	 * 無向グラフにおける隣接リストを返す．内部的には有向グラフの被参照リストと同じものを返す．
	 * @param index
	 * @return
	 */
	public List<Integer> getUndirectedListOf(int index) {
		return this.getFollowedListOf(index);
	}
	
	/**
	 * 有向グラフにおける被参照数（入次数）を返す．
	 * @param index
	 * @return
	 */
	public int getnFollowedOf(int index) {
		return networkList[index][FOLLOWED_INDEX].size();
	}
	
	/**
	 * 有向グラフにおける参照数（出次数）を返す．
	 * @param index
	 * @return
	 */
	public int getnFollowingOf(int index) {
		return networkList[index][FOLLOWING_INDEX].size();
	}
	
	/**
	 * 無向グラフにおける次数を返す．内部的には有向グラフの被参照数と同じものを返す．
	 * @param index
	 * @return
	 */
	public int getDegreeOf(int index) {
		return this.getnFollowedOf(index);
	}
	
	public void countDegreeFreq() {
		for (int i = 0; i < this.getnAgents(); i++) {
			int nFollowed = this.getnFollowedOf(i), nFollowing = this.getnFollowingOf(i);
			if (nFollowedFreqMap.containsKey(nFollowed)) {
				int val = nFollowedFreqMap.get(nFollowed);
				nFollowedFreqMap.put(nFollowed, ++val);
			} else nFollowedFreqMap.put(nFollowed, 1);
			if (nFollowingFreqMap.containsKey(nFollowing)) {
				int val = nFollowingFreqMap.get(nFollowing);
				nFollowingFreqMap.put(nFollowing, ++val);
			} else nFollowingFreqMap.put(nFollowing, 1);
		}
	}
	public TreeMap<Integer,Integer> getnFollowedFreq() {
		return this.nFollowedFreqMap;
	}
	
	public TreeMap<Integer,Integer> getnFollowingFreq() {
		return this.nFollowingFreqMap;
	}
	
	public TreeMap<Integer,Integer> getDegreeFreq() {
		return this.getnFollowedFreq();
	}
	
	public int getnFollowedFreqOf(int index) {
		return this.nFollowedFreqMap.get(this.getnFollowedOf(index));
	}
	
	public int getnFollowingFreqOf(int index) {
		return this.nFollowingFreqMap.get(this.getnFollowingOf(index));
	}
	
	public int getDegreeFreqOf(int index) {
		return this.getnFollowedFreqOf(index);
	}

}
