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

/**�o�͂���ʃO���t�𐶐����A�摜�t�@�C�������N���X�B<br>
 * ����`���̏o�͂��󂯎��R���X�g���N�^�������A��������O���t�����A�摜�ɏo�͂��郁�\�b�hgenerateGraph()�����B<br>
 * generateGraph()�͏o�͐�f�B���N�g���Əo�̓t�@�C�����������Ɏ��B
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
	 * SilentMajoritySimulator�̌��ʂ�p�����R���X�g���N�^�B<br>
	 * 3����Integer�z���ArrayList�������Ɏ��B<br>
	 * ���ꂼ��̎����́A�u�X�e�b�v���v�u�L�^�̎�ށi�ݐ�/�X�V�j�v�u�L�^�̃X�R�[�v�i�S��/�T�C�����g/���H�[�J���j�v�u�ӌ��i����/�m��/�ے�/����`�j�v�ł���B
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
		//�ݐϋL�^�iSUM_INDEX�ȉ��j�ɂ���
		for (int j = 0; j < records.get(0)[SUM_INDEX].length; j++) { //�X�R�[�v���ƂɎ��n������
			/* X�̒l���d�����Ă���f�[�^�̑��݂��������ۂ��Ƃ����Ⴂ������A
			 * StackedXYAreaChart�ł͏d���������Ȃ��̂ŁA�����I�ȃR���X�g���N�^���g��
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
