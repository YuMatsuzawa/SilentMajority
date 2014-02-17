package matz.agentsim;

import java.io.File;
import java.io.IOException;

import matz.basics.ChartGenerator;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.DefaultXYZDataset;

public class ContourGenerator implements ChartGenerator {

	public final int VT_INDEX = 0, ST_INDEX = 1;
	private JFreeChart contour = null;
	private CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
	
	public ContourGenerator(String[] title, double[][][] data) {
		
		DefaultXYZDataset vtContourDataset = new DefaultXYZDataset(),
						stContourDataset = new DefaultXYZDataset();
		vtContourDataset.addSeries(title[VT_INDEX], data[VT_INDEX]);
		stContourDataset.addSeries(title[ST_INDEX], data[ST_INDEX]);
		
		XYBlockRenderer renderer = new XYBlockRenderer();
		XYPlot vtContourPlot = new XYPlot(vtContourDataset,null,null,renderer),
				stContourPlot = new XYPlot(stContourDataset,null,null,renderer);
		
		combinedPlot.add(vtContourPlot);
		combinedPlot.add(stContourPlot);
		
		this.contour = new JFreeChart("Opinion Divergence", combinedPlot);
	}

	@Override
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir,outFile), contour, 300, 600);

	}

}
