package dataStructures;

public class DivisionData {
	private String parent;
	private double d1Percentage;
	private Axes axis;
	private int time;
	private int generation;
	
	
	public DivisionData(String parent, double d1Percentage, Axes axis, int time, int generation){
		this.parent = parent;
		this.d1Percentage = d1Percentage;
		this.axis = axis;
		this.time = time;
		this.generation = generation;
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

	public DivisionData setD1Percentage(double d1Percentage) {
		this.d1Percentage = d1Percentage;
		return this;
	}

	public DivisionData setTime(int time) {
		this.time = time;
		return this;
	}

	public int getGeneration() {
		return generation;
	}
}
