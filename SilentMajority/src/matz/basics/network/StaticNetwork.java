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

	protected TreeMap<Integer, Integer> numFollowedFreqMap = new TreeMap<Integer,Integer>(); //TreeMapはKeyを昇順に順序付けするので、低次数のエントリから順に並ぶ
	protected TreeMap<Integer, Integer> numFollowingFreqMap = new TreeMap<Integer,Integer>();
	
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
	 * 無向グラフにおける隣接リストを返す．内部的には有向グラフの<s>被</s>参照リストと同じものを返す．<br>
	 * 実験における利用の本質上，参照リストを用いることのほうが多いので，有向グラフ導入時の影響を緩和するために，参照リストを返すように変更した．15/01/07
	 * @param index
	 * @return
	 */
	public List<Integer> getUndirectedListOf(int index) {
		return this.getFollowingListOf(index);
	}
	
	/**
	 * 有向グラフにおいて，あるエージェントindexの被参照数（入次数）を返す．
	 * @param index
	 * @return
	 */
	public int getNumFollowedOf(int index) {
		return this.networkList[index][FOLLOWED_INDEX].size();
	}
	
	/**
	 * 有向グラフにおいて，あるエージェントindexの参照数（出次数）を返す．
	 * @param index
	 * @return
	 */
	public int getNumFollowingOf(int index) {
		return this.networkList[index][FOLLOWING_INDEX].size();
	}
	
	/**
	 * 無向グラフにおいて，あるエージェントの次数を返す．内部的には有向グラフの参照数と同じものを返す．
	 * @param index
	 * @return
	 */
	public int getDegreeOf(int index) {
		return this.getNumFollowingOf(index);
	}
	
	/**入次数，出次数それぞれについて頻度分布Mapを生成する．
	 * 
	 */
	public void countDegreeFreq() {
		for (int i = 0; i < this.getnAgents(); i++) {
			int nFollowed = this.getNumFollowedOf(i), nFollowing = this.getNumFollowingOf(i);
			if (this.numFollowedFreqMap.containsKey(nFollowed)) {
				int val = this.numFollowedFreqMap.get(nFollowed);
				this.numFollowedFreqMap.put(nFollowed, ++val);
			} else this.numFollowedFreqMap.put(nFollowed, 1);
			if (this.numFollowingFreqMap.containsKey(nFollowing)) {
				int val = this.numFollowingFreqMap.get(nFollowing);
				this.numFollowingFreqMap.put(nFollowing, ++val);
			} else this.numFollowingFreqMap.put(nFollowing, 1);
		}
	}
	
	/**頻度分布を元データとして，生成・構築したネットワークの実際の平均<strong>出次数</strong>を算出・取得する．
	 * @return
	 */
	public double getAvgNumFollowing() {
		double avgNumFollowing = 0.0;
		for (Entry<Integer,Integer> entry : this.getNumFollowingFreqMap().entrySet()) {
			avgNumFollowing += (double)entry.getKey() * (double)entry.getValue() / (double)this.getnAgents();
		}
		return avgNumFollowing;
	}
	
	/**頻度分布を元データとして，生成・構築したネットワークの実際の平均<strong>入次数</strong>を算出・取得する．
	 * @return
	 */
	public double getAvgNumFollowed() {
		double avgNumFollowed = 0.0;
		for (Entry<Integer,Integer> entry : this.getNumFollowedFreqMap().entrySet()) {
			avgNumFollowed += (double)entry.getKey() * (double)entry.getValue() / (double)this.getnAgents();
		}
		return avgNumFollowed;
	}
	
	/**頻度分布を元データとして，生成・構築したネットワークの実際の平均次数を算出・取得する．<br>
	 * 内部的には出次数（参照数）の平均を出力する．
	 * @return
	 */
	public double getAvgDegree() {
		return this.getAvgNumFollowing();
	}
	
	/**ネットワークの入次数頻度分布Mapを取得する．
	 * @return
	 */
	public TreeMap<Integer,Integer> getNumFollowedFreqMap() {
		return this.numFollowedFreqMap;
	}
	
	/**ネットワークの出次数頻度分布Mapを取得する．
	 * @return
	 */
	public TreeMap<Integer,Integer> getNumFollowingFreqMap() {
		return this.numFollowingFreqMap;
	}
	
	/**ネットワークの次数頻度分布Mapを取得する．内部的には出次数分布Mapを返している．
	 * @return
	 */
	public TreeMap<Integer,Integer> getDegreeFreqMap() {
		return this.getNumFollowingFreqMap();
	}
	
	/**あるノードindexの入次数が，ネットワーク全体ではどの程度の頻度で出現しているか取得する．頻度分布Mapの指定Keyに対するValueを返している．<br>
	 * 当該ノードの次数の，ネットワーク全体における順位を算出するために使用できる．
	 * @param index
	 * @return
	 */
	public int getNumFollowedFreqOf(int index) {
		return this.numFollowedFreqMap.get(this.getNumFollowedOf(index));
	}
	
	/**あるノードindexの出次数が，ネットワーク全体ではどの程度の頻度で出現しているか取得する．頻度分布Mapの指定Keyに対するValueを返している．<br>
	 * 当該ノードの次数の，ネットワーク全体における順位を算出するために使用できる．
	 * @param index
	 * @return
	 */
	public int getNumFollowingFreqOf(int index) {
		return this.numFollowingFreqMap.get(this.getNumFollowingOf(index));
	}
	
	/**無向グラフで，あるノードindexの次数が，ネットワーク全体ではどの程度の頻度で出現しているか取得する．内部的には出自数頻度分布Mapから取得している．<br>
	 * 当該ノードの次数の，ネットワーク全体における順位を算出するために使用できる．
	 * @param index
	 * @return
	 */
	public int getDegreeFreqOf(int index) {
		return this.getNumFollowingFreqOf(index);
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
			for (int i = 0; i < this.getnAgents(); i++) { //Undirectedを前提とした初期コード
				bw.write(i + "(" + this.getNumFollowedOf(i) + ")\t:\t");
				for (Object neighbor : this.getUndirectedListOf(i)) {
					bw.write((Integer)neighbor + ",");
				}
				bw.newLine();
			}
			
			// UserID,#followed,#following,followed csv,following csv
			for (int i = 0; i < this.getnAgents(); i++) { //Directedに対応した網羅的な出力コード．鈴村研データのCSV形式に合わせる．
				bw.write(i);
				bw.write("," + this.getNumFollowedOf(i));
				bw.write("," + this.getNumFollowingOf(i));
				for (Integer follower : this.getFollowedListOf(i)) {
					bw.write("," + follower);
				}
				for (Integer followee : this.getFollowingListOf(i)) {
					bw.write("," + followee);
				}
				bw.newLine();
			}
			bw.close();
			
			//頻度分布吐き出し
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outDir, "ntwkDegreeFreq.csv"))));
			bw2.write("numFollowed,freq");
			bw2.newLine();
			for (Entry<Integer,Integer> entry : this.numFollowedFreqMap.entrySet()) {
				bw2.write(entry.getKey() + "," + entry.getValue());
				bw2.newLine();
			}
			bw2.newLine();
			bw2.write("numFollowing,freq");
			bw2.newLine();
			for (Entry<Integer,Integer> entry : this.numFollowingFreqMap.entrySet()) {
				bw2.write(entry.getKey() + "," + entry.getValue());
				bw2.newLine();
			}
			bw2.close();
			
			ScatterPlotGenerator spg = new ScatterPlotGenerator(
					this.getNtwkName() + 
					",N=" + this.getnAgents() + 
					",Avg_InD=" + String.format("%.2f", this.getAvgNumFollowed()) ,this.numFollowedFreqMap);
			spg.generateGraph(outDir, "ntwkInDegreeFreq.png");
			if (this.getOrientation() == DIRECTED) {
				ScatterPlotGenerator spg2 = new ScatterPlotGenerator(
						this.getNtwkName() + 
						",N=" + this.getnAgents() + 
						",Avg_OutD=" + String.format("%.2f", this.getAvgNumFollowing()) ,this.numFollowingFreqMap);
				spg2.generateGraph(outDir, "ntwkOutDegreeFreq.png");
			}
	
		} catch(Exception e) {
			throw e;
		}
	}

}
