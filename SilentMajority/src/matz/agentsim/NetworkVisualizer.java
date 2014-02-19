package matz.agentsim;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

/**
 * InfoAgent配列からなるエージェントネットワークを可視化するクラス．<br>
 * とりあえず無向グラフ，次数によるノードサイズ調整，意見によるノードの色調整を盛り込む．<br>
 * 一応エッジにはのちのち影響力を反映できるように実装しておく．
 * @author Matsuzawa
 *
 */
public class NetworkVisualizer {
	
	private static final Integer 
			NULL_OPINION = null,
			NEU_OPINION = 0,
			POS_OPINION = 1,
			NEG_OPINION = 2;
	
	private Graph<InfoAgent,Edge> graph = new UndirectedSparseGraph<InfoAgent, Edge>();
	private InfoAgent[] infoAgentArray = null;
	private Dimension figureSize = null;
	private FRLayout<InfoAgent,Edge> layout = null;
	private VisualizationImageServer<InfoAgent,Edge> vis = null;
	
	public NetworkVisualizer(InfoAgent[] infoAgentArray) {
		this(infoAgentArray, null);
	}
	
	public NetworkVisualizer(InfoAgent[] infoAgentArray, int width, int height) {
		this(infoAgentArray, new Dimension(width, height));
	}
	
	public NetworkVisualizer(InfoAgent[] infoAgentArray, Dimension figureSize) {
		this.infoAgentArray = infoAgentArray;
		this.figureSize = (figureSize != null)? figureSize : new Dimension(600,600); //デフォルトサイズ
		this.constructGraph();
		this.configureRenderer();
	}
	
	private void constructGraph() {
		for (InfoAgent subjectAgent : this.infoAgentArray) {
			if (!graph.containsVertex(subjectAgent)) graph.addVertex(subjectAgent);
			for (int objectAgentIndex : subjectAgent.getUndirectedList()) {
				InfoAgent objectAgent = this.infoAgentArray[objectAgentIndex];
				if (!graph.containsVertex(objectAgent)) graph.addVertex(objectAgent);
				graph.addEdge(
						new Edge(subjectAgent.getAgentIndex() + "-" + objectAgent.getAgentIndex(),
								subjectAgent.getInfluence()),
								subjectAgent,
								objectAgent
						);
			}
		}
	}

	private void configureRenderer() {
		Transformer<InfoAgent,Paint> nodeFillColor = new Transformer<InfoAgent,Paint>() {
			//ノードの色をノードの属性依存で変えるためのTransformer
			@Override
			public Paint transform(InfoAgent arg0) {
				Paint retColor = Color.YELLOW;
				Integer opinion = arg0.forceGetOpinion();
				if (opinion == NULL_OPINION) retColor = Color.YELLOW;
				else if (opinion == NEU_OPINION) retColor = Color.RED;
				else if (opinion == POS_OPINION) retColor = Color.BLUE;
				else if (opinion == NEG_OPINION) retColor = Color.GREEN;
				return retColor;
			}
		};

		Transformer<InfoAgent,Shape> nodeShape = new Transformer<InfoAgent,Shape>() {
			//ノードのサイズをノードの属性依存で変えるためのTransformer
			@Override
			public Shape transform(InfoAgent arg0) {
				int degree = arg0.getDegree();
				boolean isSilent = arg0.isSilent();
				double size = 5 + 5 * (degree / 10);				
				Shape retShape = (isSilent)?
						new Rectangle2D.Double(-size/2, -size/2, size, size) :
						new Ellipse2D.Double(-size/2, -size/2, size, size);
				return retShape;
			}
		};

		//this.layout = new CircleLayout<InfoAgent,Edge>(this.graph);
		this.layout = new FRLayout<InfoAgent,Edge>(this.graph);
		this.layout.setMaxIterations(10);
		this.vis = new VisualizationImageServer<InfoAgent, NetworkVisualizer.Edge>(this.layout, this.figureSize);
		this.vis.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<InfoAgent, Edge>());
		this.vis.getRenderContext().setVertexFillPaintTransformer(nodeFillColor);
		this.vis.getRenderContext().setVertexShapeTransformer(nodeShape);
		this.vis.setBackground(Color.white);
		
	}

	public void generateGraph(File outDir, String outFile) throws IOException, InterruptedException {
		while(!this.layout.done()) Thread.sleep(1000);
		BufferedImage bi = (BufferedImage) this.vis.getImage(
				new Point2D.Double(layout.getSize().getWidth() / 2, layout.getSize().getHeight() / 2),
				this.figureSize);
		ImageIO.write(bi, "png", new File(outDir, outFile));
	}
	
	public class Edge {
		private String label = new String();
		private double influence = 0.0;
		
		public Edge(String label, double influence) {
			this.setLabel(label);
			this.setInfluence(influence);
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public void setInfluence(double influence) {
			this.influence = influence;
		}
		
		public double getInfluence() {
			return this.influence;
		}
		
		@Override
		public String toString() {
			return this.getLabel();
		}
	}
}
