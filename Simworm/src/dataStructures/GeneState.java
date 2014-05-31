package dataStructures;

public class GeneState {
	private boolean on;
	private boolean unknown;
	private double firstUsed; // the time at which the gene is first known to be active
	
	/**
	 * Constructor for a geneState whose state is known
	 * @param on True if the gene is active
	 */
	public GeneState(boolean on){
		this.on = on;
		this.unknown = false;
	}
	
	/**
	 * Constructor for a geneState whose state is unknown, but becomes known at a certain time
	 * @param firstUsed The time at which the state becomes known
	 */
	public GeneState(double firstUsed){
		this.firstUsed = firstUsed;
		this.unknown = true;
	}
	
	/**
	 * Constructor for a geneState that is unknown indefinitely
	 */
	public GeneState(){
		this.unknown = true;
	}
	
	public GeneState(GeneState toDup){
		this.on = toDup.on;
		this.firstUsed = toDup.firstUsed;
		this.unknown = toDup.unknown;
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

	public void setOn(boolean on) {
		this.on = on;
	}
}