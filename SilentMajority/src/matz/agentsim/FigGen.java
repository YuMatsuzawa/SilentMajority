package matz.agentsim;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.general.DefaultPieDataset;

/**
 * JFreeChart��p�����O���t�����̃T���v���B<br>
 * �P�̂œ��삷��̂�eclipse���ł̃e�X�g�E�f�o�b�O���͋N���\�����쐬���邱�ƁB<br>
 * JFreeChart�̃��C�u������libs�ȉ��Ɏ��߂Ă���B
 * @author Yu
 *
 */
public class FigGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    DefaultPieDataset data = new DefaultPieDataset();

	    data.setValue("�A�T�q", 37);
	    data.setValue("Kirin", 36);
	    data.setValue("Suntory", 13);
	    data.setValue("Sapporo", 12);
	    data.setValue("Others", 2);

	    ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
	    	//���܂��Ȃ��BChartTheme�Ńt�H���g���ׂ����ݒ�ł��邪�A�ŋ߂̃o�[�W�����̃f�t�H���g�ݒ肾�Ɠ��{�ꂪ������������悤���B
	    	//������LegacyTheme�Ƃ����̂�K�p���Ă��Ɛ���ɕ\�������B
	    
	    JFreeChart chart = 
	      ChartFactory.createPieChart("�T���v��", data, true, false, false);

	    File file = new File("./chart.png");
	    try {
	    	ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

}
