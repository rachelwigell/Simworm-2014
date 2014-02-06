package dataStructures;

public class DivisionData {
	private String parent;
	private double d1Percentage;
	private Axes axis;
	private int time;
	
	
	public DivisionData(String parent, double d1Percentage, Axes axis, int time){
		this.parent = parent;
		this.d1Percentage = d1Percentage;
		this.axis = axis;
		this.time = time;
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
}
