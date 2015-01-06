package matz.basics.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.Map.Entry;

import matz.basics.ScatterPlotGenerator;

/**
 * 外部から参照可能な静的ネットワークマップを生成し，保持するクラス．<br>
 * このクラスは抽象クラスなので、extendして具体的なグラフ生成モデル(WSモデルなど)に基づき、実装する。<br>
 * コンストラクト時の必須引数はintのエージェント数.一般に引数なしコンストラクタは用意するべきでない。<br>
 * その他に指向性orientation, 記録用にモデル名ntwkNameも与える。<br>
 * 次数を指定して生成するモデルのためにdegreeも指定できる。nullを許容させるためにDoubleとしている。<br>
 * ネットワークマップを生成するbuild()メソッドを実装する必要がある．<br>
 * ネットワークマップはArrayListの配列で管理する．この方式は総称型の配列となるので扱いに注意を要する。<br>
 * @author Matsuzawa
 *
 */
public abstract class StaticNetwork {

	protected static int FOLLOWING_INDEX = 0, FOLLOWED_INDEX = 1;
	protected static boolean DIRECTED = true, UNDIRECTED = false;
	protected static Double DEGREE_DEFAULT = 6.0;
	
	/**
	 * 静的ネットワークを保持するArrayListの配列。<br>
	 * 総称型の配列なので扱いに注意する．意味論的に使いやすいのでこうしているが，本来あまりやらないほうがいいらしい<br>
	 */
	protected List<Integer>[][] networkList = null;
	protected String ntwkName;
	protected boolean orientation = UNDIRECTED;
	protected int nAgents;
	protected double givenDegree;
	//protected boolean degreeGiven = false;

	protected TreeMap<Integer, Integer> nFollowedFreqMap = new TreeMap<Integer,Integer>(); //TreeMapはKeyを昇順に順序付けするので、
	protected TreeMap<Integer, Integer> nFollowingFreqMap = new TreeMap<Integer,Integer>();
	
	protected Random localRNG = new Random();
	
	public abstract void build();
	/**
	 * 基本コンストラクタ.エージェント数を与えないコンストラクタは作らない.<br>
	 * 
	 * @param nAgents
	 * @param orientation - 有向ならtrue,無向ならfalse
	 */
	@SuppressWarnings("unchecked")
	public StaticNetwork(String ntwkName, int nAgents, boolean orientation, Double degree) {
		this.setNtwkName(ntwkName);
		this.setnAgents(nAgents);
		this.setOrientation(orientation);
		this.networkList = new ArrayList[nAgents][2];
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < 2; j++) this.networkList[i][j] = new ArrayList<Integer>();
		}
		if (degree == null) this.setGivenDegree(DEGREE_DEFAULT);
		else {
			this.setGivenDegree(degree);
		}
	}
	
	/**
	 * エージェント数のみ与えて無向グラフを作るコンストラクタ。
	 * @param nAgents
	 */
	public StaticNetwork(String ntwkName, int nAgents) {
		this(ntwkName, nAgents, UNDIRECTED, null);
	}
	
	// TODO ネットワークデータを取得できる場合，それを元にコンストラクトできるような実装
	
	/**
	 * @return ntwkName
	 */
	public String getNtwkName() {
		return ntwkName;
	}
	/**
	 * @param ntwkName セットする ntwkName
	 */
	public void setNtwkName(String ntwkName) {
		this.ntwkName = ntwkName;
	}
	/**
	 * エージェント数を取得する。
	 * @return nAgents
	 */
	public int getnAgents() {
		return this.nAgents;
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
		return this.orientation;
	}

	/**
	 * ネットワークの指向性を指定
	 * @param orientation セットする orientation
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return givenDegree
	 */
	public double getGivenDegree() {
		return givenDegree;
	}
	/**
	 * @param givenDegree セットする givenDegree
	 */
	public void setGivenDegree(Double givenDegree) {
		this.givenDegree = givenDegree;
	}	
	
	/**
	 * subjectの被参照リストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendToFollowedListOf(int subject, int object) {
		this.networkList[subject][FOLLOWED_INDEX].add(object);
	}
	/**
	 * subjectの参照リストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendToFollowingListOf(int subject, int object) {
		this.networkList[subject][FOLLOWING_INDEX].add(object);
	}
	/**
	 * 無向グラフで、subjectの両方向のリストにobjectを追加。
	 * @param subject
	 * @param object
	 */
	public void appendToUndirectedListOf(int subject, int object) {
		this.appendToFollowedListOf(subject, object);
		this.appendToFollowingListOf(subject, object);
	}
	/**
	 * subjectの被参照リストからobjectを除去。
	 * @param subject
	 * @param object
	 */
	public void removeFromFollowedListOf(int subject, Integer object) { //remove(Object) and remove(int) must be differentiated
		this.networkList[subject][FOLLOWED_INDEX].remove(object);
	}
	/**
	 * subjectの参照リストからobjectを除去。
	 * @param subject
	 * @param object
	 */
	public void removeFromFollowingListOf(int subject, Integer object) { //remove(Object) and remove(int) must be differentiated
		this.networkList[subject][FOLLOWING_INDEX].remove(object);
	}
	/**
	 * 無向グラフで、subjectの両方向のリストからobjectを除去。
	 * @param subject
	 * @param object
	 */
	public void removeFromUndirectedListOf(int subject, Integer object) { //remove(Object) and remove(int) must be differentiated
		this.removeFromFollowedListOf(subject, object);
		this.removeFromFollowingListOf(subject, object);
	}
	/**
	 * 有向グラフにおける被参照リストを返す.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowedListOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX];
	}
	
	/**
	 * 有向グラフにおける参照リストを返す.
	 * @param index
	 * @return
	 */
	public List<Integer> getFollowingListOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX];
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
	
	public void countDegreeFreq() {
		for (int i = 0; i < this.getnAgents(); i++) {
			int nFollowed = this.getnFollowedOf(i), nFollowing = this.getnFollowingOf(i);
			if (this.nFollowedFreqMap.containsKey(nFollowed)) {
				int val = this.nFollowedFreqMap.get(nFollowed);
				this.nFollowedFreqMap.put(nFollowed, ++val);
			} else this.nFollowedFreqMap.put(nFollowed, 1);
			if (this.nFollowingFreqMap.containsKey(nFollowing)) {
				int val = this.nFollowingFreqMap.get(nFollowing);
				this.nFollowingFreqMap.put(nFollowing, ++val);
			} else this.nFollowingFreqMap.put(nFollowing, 1);
		}
	}
	
	public double getAvgDegree() {
		double avgDegree = 0.0;
		for (Entry<Integer,Integer> entry : this.getnFollowedFreq().entrySet()) {
			avgDegree += (double)entry.getKey() * (double)entry.getValue() / (double)this.getnAgents();
		}
		return avgDegree;
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
	
	/**
	 * チェックのためにネットワークの情報をファイルや画像に出力する．
	 * @param outDir
	 * @throws Exception 
	 */
	public void dumpNetwork(File outDir) throws Exception {
		if (!outDir.isDirectory()) outDir.mkdirs();
		
		//全エージェントの隣接リストをソートする。コメントアウトしてしまってもいい。
		for (List<Integer>[] agentLists : networkList) {
			Collections.sort(agentLists[FOLLOWED_INDEX]);
			Collections.sort(agentLists[FOLLOWING_INDEX]);
		}
		//ネットワークの統計的性質をチェックする。
		this.countDegreeFreq(); //次数の頻度分布
		
		try {
			//隣接リスト吐き出し
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwk.dat"))));
			for (int i = 0; i < this.getnAgents(); i++) {
				bw.write(i + "(" + this.getnFollowedOf(i) + ")\t:\t");
				for (Object neighbor : this.getUndirectedListOf(i)) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			bw.close();
			
			//頻度分布吐き出し
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwkDegreeFreq.csv"))));
			for (Entry<Integer,Integer> entry : this.nFollowedFreqMap.entrySet()) {
				bw2.write(entry.getKey() + "," + entry.getValue());
				bw2.newLine();
			}
			bw2.close();
			ScatterPlotGenerator spg = new ScatterPlotGenerator(
					this.getNtwkName() + 
					",N=" + this.getnAgents() + 
					",Avg_D=" + String.format("%.2f", this.getAvgDegree()) ,this.nFollowedFreqMap);
			spg.generateGraph(outDir, "ntwkDegreeFreq.png");
	
		} catch(Exception e) {
			throw e;
		}
	}

}
