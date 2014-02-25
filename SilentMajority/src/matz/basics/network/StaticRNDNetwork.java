package matz.basics.network;



public class StaticRNDNetwork extends StaticNetwork {
	
	@Override
	public void build() {
		
	}
	
	public StaticRNDNetwork(int nAgents, boolean orientation, Double degree) {
		super("RND", nAgents, orientation, degree);
		this.build();
	}
	
	public StaticRNDNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticRNDNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}
}
