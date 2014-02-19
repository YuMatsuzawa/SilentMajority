package matz.agentsim;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Jung���C�u������p�����O���t�`��̃e�X�g�N���X�D<br>
 * 
 * @author Matsuzawa
 *
 */
public class GraphGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//�e�X�g�l�b�g���[�N�̐���
		int nAgents = 1000;
		StaticCNNNetwork ntwk = new StaticCNNNetwork(nAgents);
		
		//�O���t�̍쐬
		Graph<Integer,String> graph = new UndirectedSparseGraph<Integer,String>();
		for (int subjectIndex = 0; subjectIndex < nAgents; subjectIndex++) {
			if (!graph.containsVertex(subjectIndex)) graph.addVertex(subjectIndex);
			for (int objectIndex : ntwk.getUndirectedListOf(subjectIndex)) {
				if (!graph.containsVertex(objectIndex)) graph.addVertex(objectIndex);
				graph.addEdge(subjectIndex + "-" + objectIndex, subjectIndex, objectIndex);
			}
		}
		
		Transformer<Integer,Paint> nodeFillColor = new Transformer<Integer,Paint>() {
			//�m�[�h�̐F���m�[�h�̑����ˑ��ŕς��邽�߂�Transformer
			@Override
			public Paint transform(Integer arg0) {
				Integer opinion = arg0 % 3;
				Paint retColor = Color.GRAY;
				if (opinion == 0) {
					retColor = Color.WHITE;
				} else if (opinion == 1) {
					retColor = Color.BLUE;
				} else if (opinion == 2) {
					retColor = Color.RED;
				}
				return retColor;
			}
		};
		
		Transformer<Integer,Shape> nodeShape = new Transformer<Integer,Shape>() {
			//�m�[�h�̃T�C�Y���m�[�h�̑����ˑ��ŕς��邽�߂�Transformer
			@Override
			public Shape transform(Integer arg0) {
				Integer degree = 1000 - arg0;
				Integer size = 2 + 2 * (degree / 100);
				Shape retShape = new Ellipse2D.Double(-size/2, -size/2, size, size);
				return retShape;
			}
			
		};
		
		//�`��̈�E�`��C���X�^���X����
		Dimension viewArea = new Dimension(600, 600);
		//Layout<Integer,String> layout = new CircleLayout<Integer,String>(graph);
		Layout<Integer,String> layout = new FRLayout<Integer,String>(graph, viewArea);
		VisualizationImageServer<Integer,String> panel = new VisualizationImageServer<Integer,String>(layout, viewArea);
		panel.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Integer,String>());
		//panel.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		//panel.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		panel.getRenderContext().setVertexFillPaintTransformer(nodeFillColor);
		panel.getRenderContext().setVertexShapeTransformer(nodeShape);
		panel.setBackground(Color.WHITE);
		
		//�摜�f���o��
		BufferedImage bi = (BufferedImage) panel.getImage(
				new Point2D.Double(layout.getSize().getWidth() / 2, layout.getSize().getWidth() /2), 
				viewArea);
		try {
			ImageIO.write(bi, "png", new File("graph.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//�E�B���h�E�����E�\��
		JFrame frame = new JFrame("Grapth view: "+layout.getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

}
