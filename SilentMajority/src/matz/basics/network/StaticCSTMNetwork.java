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
	private final static int USERID_INDEX = 0, NUM_FOLLOWED_INDEX = 1, NUM_FOLLOWING_INDEX = 2;
	
	private static ArrayList<String[]> customNetworkList = null;

	/* インスタンス化の際にファイルから読み込んで作成し，さらにランダムサンプリングで要素数を削ったリストをシーケンシャルに読み，各要素内のCSVから，存在しないリンクへのノードを無視しつつ，ネットワークリストに追加していく．
	 * (非 Javadoc)
	 * @see matz.basics.network.StaticNetwork#build()
	 */
	@Override
	public void build() {
		for (int index = 0; index < nAgents; index++) {
			String[] csv = customNetworkList.get(index);
			for (int cursor = 3; cursor <= 2 + Integer.valueOf(csv[NUM_FOLLOWED_INDEX]); cursor++) {	//FollowedListをIntegerIndexに変換してアペンド
				for (int innerIndex = 0; innerIndex < nAgents; innerIndex++) {
					if (customNetworkList.get(innerIndex)[USERID_INDEX].equals(csv[cursor])) {		//ノードのListにあるuserIDがサンプル全体Listに含まれているか判定
						this.appendToFollowedListOf(index, innerIndex);		 //含まれていれば，対象userIDのノードが格納されているindexを，自ノードindexのFollowedListにアペンド．
						break;	//見つかったならループは閉じる．見つからなかった場合は単に無視して次に行く．
					}
				}
			}
			for (int cursor = 3 + Integer.valueOf(csv[NUM_FOLLOWING_INDEX]); cursor < csv.length; cursor++) {	//FollowingListをIntegerIndexに変換してアペンド
				for (int innerIndex = 0; innerIndex < nAgents; innerIndex++) {
					if (customNetworkList.get(innerIndex)[USERID_INDEX].equals(csv[cursor])) {		//ノードのListにあるuserIDがサンプル全体Listに含まれているか判定
						this.appendToFollowingListOf(index, innerIndex);		 //含まれていれば，対象userIDのノードが格納されているindexを，自ノードindexのFollowingListにアペンド．
						break;	//見つかったならループは閉じる．見つからなかった場合は単に無視して次に行く．
					}
				}
			}
		}
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
			String[] keyval = line.split("\t");		//元ネットワークファイルはuserid-csv pair
			String[] csv = keyval[1].split(",");
			if (csv[NUM_FOLLOWED_INDEX].equals("-1") || csv[NUM_FOLLOWING_INDEX].equals("-1")) continue;	//鍵付きユーザを除外
			
			customNetworkList.add(csv);
		}
		br.close();
		
		while (customNetworkList.size() > nAgents) {//sampling
			int index = localRNG.nextInt(customNetworkList.size());
			customNetworkList.remove(index);
		}

		//Listに取り込んだ元データはノードをLong型のUserIDで管理しているため，そのままだとInteger型のネットワークリストにマッチしない．
		//StaticNetworkの派生クラス，及びInfoAgentクラスでは，ノード（エージェント）の識別をint整数のindexで行っており，このindexはネットワークリストのindexと対応しているからである．
		//従ってbuildメソッドでネットワークリストに書き込みを行う際，Longで管理されているUserIDを，Listのindexに置換しながらネットワークリストへ書き込んでいく．
		this.build();
	}
}
