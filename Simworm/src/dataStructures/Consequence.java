package dataStructures;

public class Consequence /*implements Comparable<Consequence>*/ {
	private Gene[] antecedents;
	private Gene consequence;
	private int startStage;
	private int endStage;
	
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
