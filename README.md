<a name="SilentMajority"></a>
# SilentMajority

**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [SilentMajority](#SilentMajority)
	- [Summary](#Summary)
	- [Usage](#Usage)
		- [Result](#Result)
		- [Build](#Build)
	- [Configuration](#Configuration)
	- [Simulation Types](#SimulationTypes)
		- [HighD](#HighD)
		- [BiasedV](#BiasedV)
		- [CtrlTH](#CtrlTH)
		- [Others](#Others)
	- [Other Classes](#OtherClasses)

<a name="Summary"></a>
## Summary

拡張LTモデルによる意見発信のシミュレーションを行うプロジェクト及びパッケージ．

論文のモデルと実験はSilentMajorityLTクラスが取り扱います．
モデルそのものの実装はSimulationTaskLTクラス内でなされています．
SilentMajorityLTクラスは，ExecutorServiceを起動し，ジョブ投入と集計を行うためのエントリポイントです．

ExecutorServiceはマルチスレッド化のためのものです．
もっともシミュレーション自体はすぐ終わります．ネットワーク生成・読込がボトルネックです．その部分はマルチスレッド化されていません．
1回の実行につき，ネットワーク生成は1回行われ，その上で意見配置等をランダマイズして同条件の実験を複数回行い，平均しています．

実験内容・パラメータ・最大スレッド数などの必要事項は設定XMLファイルで与えます．

Eclipse上でそのまま実行するほか，実行可能Jarに固めてサーバ上で使用もできます．

<a name="Usage"></a>
## Usage

1. conf\_template.xmlを適当に書き換えて，別名で保存し，設定ファイルとします．(例：conf\_srv.xml)
2. Eclipseで実行するか，実行可能Jarにエクスポートして，サーバで実行します．
	- Eclipse上で使う場合，実行構成の引数欄に設定ファイル名を入力して実行して下さい．
	- サーバで実行する場合は，`$ java -jar <jarname>.jar conf_srv.xml`のように入力して実行して下さい．
3. 出力はresultsディレクトリ，ログはlogディレクトリに格納されます．

<a name="Result"></a>
### Result

結果出力のうち，最も見るべき部分は，制御変数の変化に対するヴォーカルな意見割合の変化です．
これは出力フォルダに含まれるCSVファイル（`PosNegRatio<epocMillisec>.csv`）に自動で集計され，まとめられます．
グラフ化は行っていないので，Excel等を使用して下さい．

個々のサブディレクトリは各制御変数における試行結果を収束まで逐一出力したもので，細かい挙動の確認が必要となった時に用います．

ntwkで始まるファイルはネットワークダンプです．鈴村研のフォローネットワークデータと同形式で出力したCSVファイルと次数分布集計ファイルからなります．

<a name="Build"></a>
### Build

Eclipseの場合，実行可能Jarへのエクスポートを利用して下さい．通常のJarでも同じだとは思いますが，その場合必要なライブラリを-cpオプションで指定する必要があります．多分面倒です．

ビルド情報はAntビルドファイルにしておくと容易です．研究室ネットワークであれば，STR33のホームディレクトリをエクスポート先にしておけば，ビルド後即コマンド実行できて楽です．
起動構成として`SilentMajorityLT`というクラスを選択しておいて下さい．こちらが論文内容に対応しています．
エクスポートウィザードの起動構成選択からSilentMajorityLTが選べない場合，一旦キャンセルして実行構成の画面に行き，新規実行構成を作成しておけば多分できます．

既存のbuild.xmlは松澤の個人フォルダに向けてエクスポートしようとするので，設定を変更して下さい．

<a name="Configuration"></a>
## Configuration

設定ファイルの構造は以下のようになっています．

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    <properties>
    <comment>Configuration</comment>
    <entry key="nCore">10<!-- スレッド数.個人PCなら，コア数-1が安定．最もシミュレーション自体は軽い． --></entry>
    <entry key="simType">0<!--0=HighD,高次数占有, 1=BiasedV,初期Vocal偏り, 2=Relief,緩和付き, 3=CtrlTH,少数派閾値下げ, 4=SepTH,少数派閾値下げ&多数派上げ, 5=Normal,何も起こらない--></entry>
    <entry key="initSilentRatio">0.90<!-- 初期サイレントの割合． --></entry>
    <entry key="totalPosRatio">0.4<!-- 全体少数派の割合． --></entry>
    <entry key="nIter">10<!-- 同条件での実験回数． --></entry>
    <entry key="lowerBound">0.0<!-- 制御変数の最小値．たいてい0.0 --></entry>
    <entry key="controlPitch">0.02<!-- 制御変数の刻み幅．0～1を20分割するなら0.05となる． --></entry>
    <entry key="controlResol">21<!--  分割数．1加えた数を入力する(20分割なら21) --></entry>
    <entry key="nAgents">1000<!-- 0以上の値．10000程度までが現実的．カスタムネットワークの場合，-1とすることで元ネットワークファイルに含まれる全てのノードを使って実験できますが，量によっては非常に重い． --></entry>
    <entry key="ntwkType">BA<!-- CNN/BA/WS/RND/REG/CSTM --></entry>
    <entry key="degree">10<!-- 生成ネットワークの平均次数 --></entry>
    <entry key="pRewire"><!-- WSモデルでのつなぎ変え確率．0～1.空欄可.1に近いほどランダムモデルに近づく. --></entry>
    <entry key="noiseEnabled">0<!-- ノイズ(ランダムに周囲の影響と無関係にヴォーカルになる効果)を加えるか否か.0→false --></entry>
    <entry key="ntwkFig">0<!-- 生成ネットワークの模式図を出力するか否か.Jungライブラリを使い可視化.重いので非推奨.0→false --></entry>
    <entry key="customNetworkPath">ARG<!-- カスタムネットワークを使う場合の,元ネットワークファイルパス or "ARG"(その都度コマンドラインから入力したい場合) --></entry>
    </properties>

単純なXMLになっており，各KeyとそのValueでシミュレーションのパラメータを与えます．


実装されている実験それぞれについてただ1つずつの制御変数があり，この制御変数に下限と刻み幅，分割数を与えることで，特定条件についてシミュレーションが走ります．
制御変数の各値について，指定された回数（`nIter`）ずつ実験を行い，結果は平均されます．

<a name="SimulationTypes"></a>
## Simulation Types

<a name="HighD"></a>
### HighD

高次数エージェントに少数派意見を保持させる条件の実験．simType=0

制御変数は，少数派意見を保持させる次数上位エージェントの比率で,[0.0,totalPosRatio]の範囲で動かせます.

totalPosRatioは全体に対する少数派の割合なので,制御変数とtotalPosRatioが一致した場合,全ての少数派エージェントが次数上位エージェントとなります．

<a name="BiasedV"></a>
### BiasedV

初期にヴォーカルになるエージェントが少数派意見である条件の実験.simType=1

制御変数は,初期ヴォーカルエージェントにおける少数派意見エージェントの割合で,[0,1]の範囲で動かせます.


<a name="CtrlTH"></a>
### CtrlTH

少数派意見エージェントの多数派認識に関する閾値を下げる条件の実験.simType=3

制御変数は,「周囲のヴォーカルエージェントのうち，自身と一致する意見の割合が多数派と認識する閾値」の分布上限の下げ幅で，［0,1］の範囲で動かせます．

0の時は分布上限を下げないので，閾値は0～1で一様分布し，平常条件です．1の時は少数派エージェントは全て閾値0をもつことになるので，
自身と一致する意見が周囲に誰もいなくても，ヴォーカルになり（自身の意見が多数派であると認識し）ます．社会的影響に対し頑健である状態です．

<a name="Others"></a>
### Others

論文に採用しなかった実験的なタイプが残っていますが，そのうちRelief（時間が経つとサイレントになる）はSilentMajorityLTの方には恐らく対応していません．

SepTHは多数派の閾値を上げてみるという効果を加えたものです．

<a name="OtherClasses"></a>
## Other Classes

様々なクラスが含まれていますが，多くはネットワーク生成や可視化，マルチスレッド化のためのクラスです．

また，SilentMajority（LTでない）の方には，Independent Cascadeモデルベースのシミュレーション（松澤がM1時に試行していた旧モデル）なども含まれています．