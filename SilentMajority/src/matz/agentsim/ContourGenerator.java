package matz.agentsim;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import matz.basics.ChartGenerator;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;

public class ContourGenerator implements ChartGenerator {

	private JFreeChart contour = null;
	
	public ContourGenerator(String title, double[][] data) {
		
		DefaultXYZDataset contourDataset = new DefaultXYZDataset();
		contourDataset.addSeries(title, data);
		double lowerBound = Double.MAX_VALUE, upperBound = Double.MIN_VALUE;
		for (double zValue : data[2]) {
			if (zValue > upperBound) upperBound = zValue;
			if (zValue < lowerBound) lowerBound = zValue;
		}
		
        NumberAxis xAxis = new NumberAxis("sRatio"); 
        //xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
        xAxis.setLowerMargin(0.0); 
        xAxis.setUpperMargin(0.0); 
        xAxis.setRange(0.2, 0.9);
        xAxis.setAxisLineVisible(true);
        NumberAxis yAxis = new NumberAxis("mRatio"); 
        //yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
        yAxis.setLowerMargin(0.0); 
        yAxis.setUpperMargin(0.0); 
        yAxis.setRange(0.0, 1.0);
        yAxis.setAxisLineVisible(true);
        
		XYBlockRenderer renderer = new XYBlockRenderer();
        PaintScale scale = new GrayPaintScale(lowerBound, upperBound);
        renderer.setPaintScale(scale);
        NumberAxis scaleAxis = new NumberAxis("scale");
        scaleAxis.setRange(lowerBound, upperBound);
        PaintScaleLegend scaleLegend = new PaintScaleLegend(scale, scaleAxis);
        
		XYPlot contourPlot = new XYPlot(contourDataset,xAxis,yAxis,renderer);
        contourPlot.setRangeGridlinePaint(Color.white);
				
		this.contour = new JFreeChart("Opinion Divergence", contourPlot);
		contour.addSubtitle(scaleLegend);
	}

	@Override
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir,outFile), contour, 600, 600);

	}

}
