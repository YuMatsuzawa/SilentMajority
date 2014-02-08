package matz.agentsim;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.general.DefaultPieDataset;

/**
 * JFreeChartを用いたグラフ生成のサンプル。<br>
 * 単体で動作するのでeclipse等でのテスト・デバッグ時は起動構成を作成すること。<br>
 * JFreeChartのライブラリはlibs以下に収めてある。
 * @author Yu
 *
 */
public class FigGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    DefaultPieDataset data = new DefaultPieDataset();

	    data.setValue("アサヒ", 37);
	    data.setValue("Kirin", 36);
	    data.setValue("Suntory", 13);
	    data.setValue("Sapporo", 12);
	    data.setValue("Others", 2);

	    ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
	    	//おまじない。ChartThemeでフォント等細かく設定できるが、最近のバージョンのデフォルト設定だと日本語が文字化けするようだ。
	    	//そこでLegacyThemeというのを適用してやると正常に表示される。
	    
	    JFreeChart chart = 
	      ChartFactory.createPieChart("サンプル", data, true, false, false);

	    File file = new File("./chart.png");
	    try {
	    	ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

}
