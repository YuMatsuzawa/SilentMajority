package matz.basics.network;


public class StaticBANetwork extends StaticNetwork {
	
	@Override
	public void build() {
		
	}
	
	public StaticBANetwork(int nAgents, boolean orientation, Double degree) {
		super("WS", nAgents, orientation, degree);
		this.build();
	}
	
	public StaticBANetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticBANetwork(int nAgents) {
		this(nAgents, null);
	}
}
