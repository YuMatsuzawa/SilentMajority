package matz.agentsim;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**出力から面グラフを生成し、画像ファイルを作るクラス。<br />
 * 特定形式の出力を受け取るコンストラクタを持ち、そこからグラフを作り、画像に出力するメソッドgenerateGraph()をもつ。<br />
 * generateGraph()は出力先ディレクトリと出力ファイル名を引数に取る。
 * 
 * @author Yu
 *
 */
public class AreaChartGenerator {
	
	public final int SUM_INDEX = 0, UPDATE_INDEX = 1,
			TOTAL_INDEX = 0, SILENT_INDEX = 1, VOCAL_INDEX = 2,
			NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2, NULL_INDEX = 3;
	private JFreeChart stackedAreaChart = null;
	//private CombinedRangeXYPlot combinedPlot = new CombinedRangeXYPlot();
	private CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
	private String[] scopeType = {"Total", "Silent", "Vocal"};
	private String[] opinionType = {"Neutral", "Positive", "Negative", "Undecided"};
	
	/**SilentMajoritySimulatorの結果を用いたコンストラクタ。<br />
	 * 4次元Integer配列を引数に取る。それぞれの次元は、「ステップ数」「記録の種類（累積/更新）」「記録のスコープ（全体/サイレント/ヴォーカル）」「意見（中立/肯定/否定/未定義）」である。
	 * 
	 * @param records
	 */
	public AreaChartGenerator(Integer[][][][] records) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		DefaultTableXYDataset totalDataset = new DefaultTableXYDataset(),
						silentDataset = new DefaultTableXYDataset(),
						vocalDataset = new DefaultTableXYDataset();
		XYSeries[][] datasetSeries = new XYSeries[3][4];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				datasetSeries[i][j] = new XYSeries(j+"-"+opinionType[j], false, false);
				//datasetSeries[i][j] = new XYSeries(Math.random()+"-"+opinionType[j], false, false);
			}
		}
		//累積記録（SUM_INDEX以下）について
		for (int j = 0; j < records[0][SUM_INDEX].length; j++) { //スコープごとに時系列を作る
			/* Xの値が重複しているデータの存在を許すか否かという違いがあり、
			 * StackedXYAreaChartでは重複を許さないので、明示的なコンストラクタを使う
			 */
			//DefaultTableXYDataset tmpDataset = (j == TOTAL_INDEX)? totalDataset : (j == SILENT_INDEX)? silentDataset : vocalDataset;
			
//			XYSeries neuSeries = new XYSeries(scopeType[j]+"-"+opinionType[NEU_INDEX], false, false),
//					posSeries = new XYSeries(scopeType[j]+"-"+opinionType[POS_INDEX], false, false),
//					negSeries = new XYSeries(scopeType[j]+"-"+opinionType[NEG_INDEX], false, false),
//					nullSeries = new XYSeries(scopeType[j]+"-"+opinionType[NULL_INDEX], false, false);
			
			for (int i = 0; i < records.length; i++) {
//				neuSeries.add(i, records[i][SUM_INDEX][j][NEU_INDEX]);
//				posSeries.add(i, records[i][SUM_INDEX][j][POS_INDEX]);
//				negSeries.add(i, records[i][SUM_INDEX][j][NEG_INDEX]);
//				nullSeries.add(i, records[i][SUM_INDEX][j][NULL_INDEX]);
				for (int k = 0; k < 4; k++) {
					datasetSeries[j][k].add(i, records[i][SUM_INDEX][j][k]);
				}
			}
//			tmpDataset.addSeries(neuSeries);
//			tmpDataset.addSeries(posSeries);
//			tmpDataset.addSeries(negSeries);
//			tmpDataset.addSeries(nullSeries);
			
//			if (j == TOTAL_INDEX) totalDataset = tmpDataset;
//			else if (j == SILENT_INDEX) silentDataset = tmpDataset;
//			else vocalDataset = tmpDataset;
			
			if (j == TOTAL_INDEX) for(int k = 0; k < 4; k++) totalDataset.addSeries(datasetSeries[j][k]);
			else if (j == SILENT_INDEX) for(int k = 0; k < 4; k++) silentDataset.addSeries(datasetSeries[j][k]);
			else for(int k = 0; k < 4; k++) vocalDataset.addSeries(datasetSeries[j][k]);
		}
		
		NumberAxis rangeAxis = new NumberAxis("Ratios of Opinions");
		ValueAxis domainAxis = new NumberAxis("Timesteps");
//		domainAxis.setTickUnit(new NumberTickUnit(5.0), true, false);
		StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
		//renderer.setBaseSeriesVisibleInLegend(false);
		//renderer.set
		//StackedXYAreaRenderer renderer2 = new StackedXYAreaRenderer();
		
		
		XYPlot totalPlot = new XYPlot(totalDataset, domainAxis, new NumberAxis("Ratios (Total)"),renderer),
				silentPlot = new XYPlot(silentDataset, domainAxis, new NumberAxis("Ratios (Silent)"),renderer),
				vocalPlot = new XYPlot(vocalDataset, domainAxis, new NumberAxis("Ratios (Vocal)"),renderer);
		//totalPlot.
		combinedPlot.add(totalPlot);
		combinedPlot.add(silentPlot);
		combinedPlot.add(vocalPlot);
		LegendItemCollection lic = new LegendItemCollection();
		for (int i=0; i < totalPlot.getSeriesCount(); i++) {
			lic.add(new LegendItem(opinionType[i], renderer.getSeriesFillPaint(i))); //FIXME cause NullError
		}
		combinedPlot.setFixedLegendItems(lic);
		this.stackedAreaChart = new JFreeChart("Ratios of Opinions", combinedPlot);
	}
	
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir, outFile), this.stackedAreaChart, 300, 600);
		
	}
}
