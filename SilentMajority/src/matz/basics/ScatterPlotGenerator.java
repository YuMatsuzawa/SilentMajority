package matz.basics;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class ScatterPlotGenerator implements ChartGenerator {
	
	private final int X_INDEX = 0, Y_INDEX = 1;
	
	private JFreeChart scatterPlot = null;
	
	public ScatterPlotGenerator(String title, Map<Integer, Integer> freqMap) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] data = new double[2][freqMap.size()];
		int index = 0;
		for (Entry<Integer,Integer> entry : freqMap.entrySet()) {
			data[X_INDEX][index] = entry.getKey();
			data[Y_INDEX][index] = entry.getValue();
			index++;
		}
		dataset.addSeries(title, data);

		NumberTickUnit logUnit = new NumberTickUnit(10);
		LogarithmicAxis xAxis = new LogarithmicAxis("Degree");
		xAxis.setUpperMargin(0.0);
		xAxis.setLowerMargin(0.0);
		xAxis.setTickUnit(logUnit);
		xAxis.setAutoRangeNextLogFlag(true);
		LogarithmicAxis yAxis = new LogarithmicAxis("Frequency");
		yAxis.setUpperMargin(0.0);
		yAxis.setLowerMargin(0.0);
		yAxis.setTickUnit(logUnit);
		xAxis.setAutoRangeNextLogFlag(true);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseLinesVisible(false);
		//renderer.setSeriesShape(0, new Ellipse2D.Double(-2D, -2D, 4D, 4D));
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		
		this.scatterPlot = new JFreeChart("Freq. of Degree", plot);
		this.scatterPlot.setBackgroundPaint(Color.WHITE);
	}
	@Override
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir, outFile), this.scatterPlot, 300, 300);
	}

}
