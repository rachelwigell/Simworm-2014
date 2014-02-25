package dataStructures;

import java.util.HashMap;

public class DivisionData {
	private String parent;
	private double d1Percentage;
	private Axes axis;
	private int time;
	private HashMap<String, Coordinates> geneCompartments; //genes that have changed compartments since the division
	
	
	public DivisionData(String parent, double d1Percentage, Axes axis, int time, HashMap<String, Coordinates> geneCompartments){
		this.parent = parent;
		this.d1Percentage = d1Percentage;
		this.axis = axis;
		this.time = time;
		this.geneCompartments = geneCompartments;
	}

	//getters
	public String getParent() {
		return parent;
	}


	public double getD1Percentage() {
		return d1Percentage;
	}

	public Axes getAxis() {
		return axis;
	}
	
	public int getTime(){
		return time;
	}

	public HashMap<String, Coordinates> getGeneCompartments() {
		return geneCompartments;
	}
}
