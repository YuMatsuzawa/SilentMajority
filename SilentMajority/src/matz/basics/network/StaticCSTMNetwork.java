package matz.basics.network;

import java.io.*;
import java.util.ArrayList;

/**所与のネットワークファイルを読み込んでネットワークインスタンスを構築するクラス．<br>
 * ファイル形式は鈴村研データのCSV形式に従う．即ち，一行の中身が，<br>
 * <code>userID,#followed,#following,follower csv,followee csv</code><br>
 * となっているテキストデータを入力とする．#followed,#followingに負の整数が代入されている場合，鍵付きユーザを指すので除外される．<br>
 * nAgentsを与えるコンストラクタでは，ランダムサンプリングによって指定数ノードに絞り込む．<br>
 * @author YuMatsuzawa
 *
 */
public class StaticCSTMNetwork extends StaticNetwork {
	
	private static ArrayList<String[]> customNetworkList = null;

	/* 指定されたネットワークファイルを読み込み，
	 * (非 Javadoc)
	 * @see matz.basics.network.StaticNetwork#build()
	 */
	@Override
	public void build() {
		// TODO 自動生成されたメソッド・スタブ

	}


	/**基本コンストラクタ．ただし，外部から使用されない．
	 * @param ntwkName
	 * @param nAgents
	 * @param orientation
	 * @param degree
	 */
	private StaticCSTMNetwork(String ntwkName, int nAgents, boolean orientation, Double degree) {
		super(ntwkName, nAgents, orientation, degree);
		customNetworkList = new ArrayList<String[]>();
	}

	/**デフォルトコンストラクタ．nAgentsは必ず指定される．まず指定のnAgentsサイズのネットワークをsuper側でインスタンス化する．<br>
	 * 次いでネットワークファイルを読み込み，StringのListに保存しておく．<br>
	 * その後ファイルの読込ListをフィルタリングしてnAgentsと一致するサイズに絞り込んだ上で，buildメソッドでネットワークリストに反映する．
	 * @param customNetworkPath
	 * @param nAgents
	 * @throws FileNotFoundException, 
	 */
	public StaticCSTMNetwork(String customNetworkPath, int nAgents) throws FileNotFoundException, IOException {
		//指定のnAgentsサイズでネットワークインスタンスを初期化する．
		this("CSTM", nAgents, DIRECTED, null);
		
		//ファイルを読み込み，Listに保存する．その後，鍵付きユーザを除外したランダムサンプリングを行い，Listサイズを指定のnAgentsサイズと一致させておく．
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(customNetworkPath))));
		String line = "";
		while((line = br.readLine()) != null) {
			customNetworkList.add(line.split(","));
		}
		br.close();
		//TODO

		//Listに取り込んだ元データはノードをLong型のUserIDで管理しているため，そのままだとInteger型のネットワークリストにマッチしない．
		//StaticNetworkの派生クラス，及びInfoAgentクラスでは，ノード（エージェント）の識別をint整数のindexで行っており，このindexはネットワークリストのindexと対応しているからである．
		//従ってbuildメソッドでネットワークリストに書き込みを行う際，Longで管理されているUserIDを，Listのindexに置換しながらネットワークリストへ書き込んでいく．
		this.build();
	}
}
