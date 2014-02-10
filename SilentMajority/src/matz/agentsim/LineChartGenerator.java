package matz.agentsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import matz.basics.ChartGenerator;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

public class LineChartGenerator implements ChartGenerator {
	
	public final int SUM_INDEX = 0, UPDATE_INDEX = 1,
			TOTAL_INDEX = 0, SILENT_INDEX = 1, VOCAL_INDEX = 2,
			NEU_INDEX = 0, POS_INDEX = 1, NEG_INDEX = 2, NULL_INDEX = 3;
	private JFreeChart lineChart = null;
	private CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
	@SuppressWarnings("unused")
	private String[] scopeType = {"Total", "Silent", "Vocal"};
	private String[] opinionType = {"Neutral", "Positive", "Negative", "Undecided"};
	
	/**
	 * SilentMajoritySimulator�̌��ʂ�p�����R���X�g���N�^�B<br>
	 * 4����Integer�z��������Ɏ��B���ꂼ��̎����́A�u�X�e�b�v���v�u�L�^�̎�ށi�ݐ�/�X�V�j�v�u�L�^�̃X�R�[�v�i�S��/�T�C�����g/���H�[�J���j�v�u�ӌ��i����/�m��/�ے�/����`�j�v�ł���B
	 * 
	 * @param records
	 */
	public LineChartGenerator(ArrayList<Integer[][][]> records) {
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
		//�ݐϋL�^�iSUM_INDEX�ȉ��j�ɂ���
		for (int j = 0; j < records.get(0)[UPDATE_INDEX].length; j++) { //�X�R�[�v���ƂɎ��n������
			/* X�̒l���d�����Ă���f�[�^�̑��݂��������ۂ��Ƃ����Ⴂ������A
			 * StackedXYAreaChart�ł͏d���������Ȃ��̂ŁA�����I�ȃR���X�g���N�^���g��
			 */			
			for (int i = 0; i < records.size(); i++) {
				for (int k = 0; k < 4; k++) {
					datasetSeries[j][k].add(i, records.get(i)[UPDATE_INDEX][j][k]);
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
		
		MarginlessNumberAxis totalAxis = new MarginlessNumberAxis("Updates (Total)"), 
				silentAxis = new MarginlessNumberAxis("Updates (Silent)"),
				vocalAxis = new MarginlessNumberAxis("Updates (Vocal)"),
				domainAxis = new MarginlessNumberAxis("Timesteps");
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
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
		
		this.lineChart = new JFreeChart("# of Opinion Updates", combinedPlot);
	}

	@Override
	public void generateGraph(File outDir, String outFile) throws IOException {
		ChartUtilities.saveChartAsPNG(new File(outDir, outFile), this.lineChart, 300, 600);
	}

}
