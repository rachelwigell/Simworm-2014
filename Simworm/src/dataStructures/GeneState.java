package dataStructures;

public class GeneState {
	private boolean on;
	private boolean unknown;
	private double firstUsed; // the time at which the gene is first known to be active
	
	public GeneState(boolean on){
		this.on = on;
		this.unknown = false;
	}
	
	public GeneState(double firstUsed){
		this.firstUsed = firstUsed;
		this.unknown = true;
	}

	//getters
	public boolean isOn() {
		return on;
	}

	public boolean isUnknown() {
		return unknown;
	}

	public double getFirstUsed() {
		return firstUsed;
	}
}
