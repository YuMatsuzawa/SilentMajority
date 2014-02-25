package matz.basics.network;

public class StaticWSNetwork extends StaticNetwork {
	
	@Override
	public void build() {
		
	}
	
	public StaticWSNetwork(int nAgents, boolean orientation, Double degree) {
		super("WS", nAgents, orientation, degree);
		this.build();
	}
	
	public StaticWSNetwork(int nAgents, Double degree) {
		this(nAgents, UNDIRECTED, degree);
	}
	
	public StaticWSNetwork(int nAgents) {
		this(nAgents, DEGREE_DEFAULT);
	}

}
