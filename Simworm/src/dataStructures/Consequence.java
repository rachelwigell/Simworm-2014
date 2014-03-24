package dataStructures;

public class Consequence /*implements Comparable<Consequence>*/ {
	private Gene[] antecedents;
	private Gene consequence;
	private int startStage;
	private int endStage;
	
	/**
	 * Constructor for a consequence object
	 * @param antecedents All of the gene names and their states (see simple gene constructor) required for this consequence to occur
	 * @param consequence The gene and its state that will be set if the antecedents are fulfilled
	 * @param startStage The cell stage (number of cells present) at which this rule starts being considered
	 * @param endStage The cell stage at which this rule stops being considered
	 */
	public Consequence(Gene[] antecedents, Gene consequence, int startStage, int endStage){
		this.antecedents = antecedents;
		this.consequence = consequence;
		this.startStage = startStage;
		this.endStage = endStage;
	}
	
	//getters
	public Gene[] getAntecedents() {
		return antecedents;
	}
	
	public Gene getConsequence() {
		return consequence;
	}
	
	public int getStartStage() {
		return startStage;
	}

	public int getEndStage() {
		return endStage;
	}

	//uncomment if concentration data becomes known
	/*public int compareTo(Consequence c){
		int thisTotal = 0;
		for(Gene g: this.antecedents){
			thisTotal += g.getConcentration();
		}
		int cTotal = 0;
		for(Gene g: c.antecedents){
			cTotal += g.getConcentration();
		}
		if(thisTotal > cTotal) return 1;
		else if(thisTotal == cTotal) return 0;
		else return -1;
	}*/
}
