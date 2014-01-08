package matz;

import java.lang.reflect.Field;

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
		RunnableSimulator rs = new RunnableSimulator();
		
		System.out.println("Running simulation with following parameters:");
		for (Field field : rs.getClass().getDeclaredFields()) {
			try {
				System.out.println(field.getName()+" = "+field.get(this).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
