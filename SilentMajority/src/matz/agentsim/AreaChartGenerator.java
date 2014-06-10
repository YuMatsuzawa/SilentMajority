package matz.agentsim;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import matz.basics.ChartGenerator;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**出力から面グラフを生成し、画像ファイルを作るクラス。<br>
 * 特定形式の出力を受け取るコンストラクタを持ち、そこからグラフを作り、画像に出力するメソッドgenerateGraph()をもつ。<br>
 * generateGraph()は出力先ディレクトリと出力ファイル名を引数に取る。
 * 
 * @author Yu
 *
 */
public class AreaChartGenerator implements ChartGenerator {
	
	public final int SUM_INDEX = 0, UPDATE_INDEX = 1,
			TOTAL_INDEX = 0, SILENT_INDEX = 1, VOCAL_INDEX = 2,
			NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2, NULL_INDEX = 3;
	private JFreeChart stackedAreaChart = null;
	private CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
	@SuppressWarnings("unused")
	private String[] scopeType = {"Total", "Silent", "Vocal"};
	private String[] opinionType = {"Neutral", "Positive", "Negative", "Undecided"};
	
	/**
	 * SilentMajoritySimulatorの結果を用いたコンストラクタ。<br>
	 * 3次元Integer配列のArrayListを引数に取る。<br>
	 * それぞれの次元は、「ステップ数」「記録の種類（累積/更新）」「記録のスコープ（全体/サイレント/ヴォーカル）」「意見（中立/肯定/否定/未定義）」である。
	 * @param records
	 */
	public AreaChartGenerator(ArrayList<Integer[][][]> records) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		DefaultTableXYDataset totalDataset = new DefaultTableXYDataset(),
						silentDataset = new DefaultTableXYDataset(),
						vocalDataset = new DefaultTableXYDataset();
		XYSeries[][] datasetSeries = new XYSeries[3][4];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				datasetSeries[i][j] = new XYSeries(opinionType[j], false, false);
			}
		}
		//累積記録（SUM_INDEX以下）について
		for (int j = 0; j < records.get(0)[SUM_INDEX].length; j++) { //スコープごとに時系列を作る
			/* Xの値が重複しているデータの存在を許すか否かという違いがあり、
			 * StackedXYAreaChartでは重複を許さないので、明示的なコンストラクタを使う
			 */			
			for (int i = 0; i < records.size(); i++) {
				for (int k = 0; k < 4; k++) {
					datasetSeries[j][k].add(i, records.get(i)[SUM_INDEX][j][k]);
				}
			}
			
			if (j == TOTAL_INDEX) for(int k = 0; k < 4; k++) totalDataset.addSeries(datasetSeries[j][k]);
			else if (j == SILENT_INDEX) for(int k = 0; k < 4; k++) silentDataset.addSeries(datasetSeries[j][k]);
			else for(int k = 0; k < 4; k++) vocalDataset.addSeries(datasetSeries[j][k]);
		}
		
		class MarginlessNumberAxis extends NumberAxis {
			public MarginlessNumberAxis(String label) {
				super(label);
				this.setUpperMargin(0.0);
				this.setStandardTickUnits(MarginlessNumberAxis.createIntegerTickUnits());
			}
		}
		
		MarginlessNumberAxis totalAxis = new MarginlessNumberAxis("Ratios (Total)"), 
				silentAxis = new MarginlessNumberAxis("Ratios (Silent)"),
				vocalAxis = new MarginlessNumberAxis("Ratios (Vocal)"),
				domainAxis = new MarginlessNumberAxis("Timesteps");
		StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
		//renderer.setBaseSeriesVisibleInLegend(false);
		
		combinedPlot.setDomainAxis(domainAxis);
		XYPlot totalPlot = new XYPlot(totalDataset, domainAxis, totalAxis, renderer),
				silentPlot = new XYPlot(silentDataset, domainAxis, silentAxis, renderer),
				vocalPlot = new XYPlot(vocalDataset, domainAxis, vocalAxis, renderer);
		combinedPlot.add(totalPlot);
		combinedPlot.add(silentPlot);
		combinedPlot.add(vocalPlot);
		LegendItemCollection lic = totalPlot.getLegendItems();
		combinedPlot.setFixedLegendItems(lic);
		
		this.stackedAreaChart = new JFreeChart("Ratios of Opinions", combinedPlot);
		this.stackedAreaChart.setBackgroundPaint(Color.WHITE);
	}
	
	@Override
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir, outFile), this.stackedAreaChart, 300, 600);
	}
}
