package dataStructures;

public class GeneState {
	//	private boolean on;
	//	private boolean unknown;
	private double firstUsed; // the time at which the gene is first known to be active
	private GeneStates state;

	/**
	 * Constructor for a geneState whose state is known
	 * @param on True if the gene is active
	 */
	public GeneState(GeneStates state){
		this.state = state;
	}

	/**
	 * Constructor for a geneState whose state is unknown, but becomes known at a certain time
	 * @param firstUsed The time at which the state becomes known
	 */
	public GeneState(double firstUsed){
		this.state = GeneStates.UNKNOWN;
		this.firstUsed = firstUsed;
	}

	/**
	 * Constructor for a geneState that is unknown indefinitely
	 */
	public GeneState(){
		this.state = GeneStates.UNKNOWN;
	}

	/**
	 * Duplication constructor for GeneState objects
	 * @param toDup the GeneState to duplicate
	 */
	public GeneState(GeneState toDup){
		this.firstUsed = toDup.firstUsed;
		this.state = toDup.state;
	}

	//getters and setters

	public double getFirstUsed() {
		return firstUsed;
	}

	public GeneStates getState() {
		return state;
	}

	public void setState(GeneStates state) {
		this.state = state;
	}

	public boolean isOn(){
		return this.state == GeneStates.ACTIVE;
	}

	public boolean isUnknown(){
		return this.state == GeneStates.UNKNOWN;
	}

	public void setOn(boolean on){
		if(on){
			this.state = GeneStates.ACTIVE;
		}
		else{
			this.state = GeneStates.INACTIVE;
		}
	}
}