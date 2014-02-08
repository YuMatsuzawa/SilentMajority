package matz.agentsim;

import java.util.Date;

import matz.basics.MatzExecutor;

/**
 * 情報伝播ネットワークにおけるサイレント・ユーザの影響を分析するシミュレーション。<br>
 * このクラスはmain関数をホストし、コマンドライン引数を処理するエントリポイントである。<br>
 * パラメータを変更させて連続シミュレーションするために、basicsパッケージのMatzExecutorをインスタンス化して使用すること。<br>
 * MatzExecutorはマルチスレッド処理のためのフレームワークであるExecutorServiceを実装している。<br>
 * 個々のシミュレーションにおける実際の処理内容はRunnable（あるいはCallable）を実装してタスクオブジェクトを定義し、<br>
 * これをMatzExecutorインスタンスにexecute（あるいはsubmit）して使用する。
 * 
 * @author Romancer
 *
 */
public final class SilentMajority {
	
	public static final void main(String[] args) {
		MatzExecutor _E = null;
		
		//引数はコア数のみ．Corei7以上なら8を指定していい．Corei5,i3,Core2 Quadなら4，Core2 Duoなら2.
		if (args.length > 0) {
			for (String arg : args) {
				try {
					int numThreads = Integer.parseInt(arg);
					_E = new MatzExecutor(numThreads);
				} catch (NumberFormatException e) {
					_E = new MatzExecutor();
				}
			}
		} else {
			_E = new MatzExecutor();
		}
		
		_E.SimExecLogger.info("Starting "+ _E.getClass().getName() +". NumThreads = " + _E.getNumThreads());
		//パラメータを変更させながらシミュレーションするイテレータ．
		//nIterは同一条件でのシミュレーションを何回ずつ行うか指定する．
		//シミュレーションの解像度はパラメータごとのResolで指定する．
		@SuppressWarnings("unused")
		Date date = new Date();
		int nIter = 1, sRatioResol = 10, mRatioResol = 1;
		int nAgents = 500;
		for (int k = 0; k < mRatioResol; k++) {
			//double mRatio = k * 0.10;
			double mRatio = 0.50;
			for (int j = 0; j < sRatioResol; j++) {
				double sRatio = j * 0.10;
				for (int i = 0; i < nIter; i++) {
					//SimulationTask rn = new SimulationTask(String.valueOf(date.getTime()), "condition" + j + "-" + i, 500, sRatio, mRatio);
					SimulationTask rn = new SimulationTask("condition" + j + "-" + i, nAgents, sRatio, mRatio);
						//コンストラクト時に時刻を与えないと、"recent"以下に結果が上書き出力される。
					_E.execute(rn);
					//TODO ここでエージェントを初期化・ネットワーク生成
					_E.SimExecLogger.info("Submitted: " + rn.getInstanceName());
				}
			}
		}
		
		_E.safeShutdown();
		_E.closeLogFileHandler();
	}
}
