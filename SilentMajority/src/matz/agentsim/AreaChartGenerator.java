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

/**�o�͂���ʃO���t�𐶐����A�摜�t�@�C�������N���X�B<br />
 * ����`���̏o�͂��󂯎��R���X�g���N�^�������A��������O���t�����A�摜�ɏo�͂��郁�\�b�hgenerateGraph()�����B<br />
 * generateGraph()�͏o�͐�f�B���N�g���Əo�̓t�@�C�����������Ɏ��B
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
	
	/**SilentMajoritySimulator�̌��ʂ�p�����R���X�g���N�^�B<br />
	 * 4����Integer�z��������Ɏ��B���ꂼ��̎����́A�u�X�e�b�v���v�u�L�^�̎�ށi�ݐ�/�X�V�j�v�u�L�^�̃X�R�[�v�i�S��/�T�C�����g/���H�[�J���j�v�u�ӌ��i����/�m��/�ے�/����`�j�v�ł���B
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
		//�ݐϋL�^�iSUM_INDEX�ȉ��j�ɂ���
		for (int j = 0; j < records[0][SUM_INDEX].length; j++) { //�X�R�[�v���ƂɎ��n������
			/* X�̒l���d�����Ă���f�[�^�̑��݂��������ۂ��Ƃ����Ⴂ������A
			 * StackedXYAreaChart�ł͏d���������Ȃ��̂ŁA�����I�ȃR���X�g���N�^���g��
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
