package matz;

public class RunnableSimulator implements Runnable {

	private double SilentAgentsRatio;
	private double ModelReferenceRatio;
	
	public void setSilentAgentsRatio(double silentAgentsRatio) {
		SilentAgentsRatio = silentAgentsRatio;
	}

	public double getSilentAgentsRatio() {
		return SilentAgentsRatio;
	}

	public double getModelReferenceRatio() {
		return ModelReferenceRatio;
	}

	public void setModelReferenceRatio(double modelReferenceRatio) {
		ModelReferenceRatio = modelReferenceRatio;
	}

	public RunnableSimulator() {
		setSilentAgentsRatio(Math.random());
		setModelReferenceRatio(Math.random());
	}
	
	public RunnableSimulator(double silentAgentsRatio, double modelReferenceRatio) {
		setSilentAgentsRatio(silentAgentsRatio);
		setModelReferenceRatio(modelReferenceRatio);
	}
	
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println();
		
	}

}
