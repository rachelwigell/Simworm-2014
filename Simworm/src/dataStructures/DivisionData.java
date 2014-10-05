package dataStructures;

public class DivisionData {
	private String parent;
	private double d1Percentage;
	private Axes axis;
	private int time;
	private int generation;
	
	/**
	 * The constructor for a DivisionData object, which contains all the information that the cellDivision function needs to know in order to compute a division
	 * @param parent The name of the cell being divided
	 * @param d1Percentage The percentage of the volume that goes to d1 (between 0 and 1)
	 * @param axis The axis along which the cell is dividing
	 * @param time The time at which the division occurs
	 * @param generation The generation of the dividing cell
	 */
	public DivisionData(String parent, double d1Percentage, Axes axis, int time, int generation){
		this.parent = parent;
		this.d1Percentage = d1Percentage;
		this.axis = axis;
		this.time = time;
		this.generation = generation;
	}

	/**
	 * Duplication constructor for division data
	 * @param toDup the DivisionData to duplicate
	 */
	public DivisionData(DivisionData toDup){
		this.parent = toDup.parent;
		this.d1Percentage = toDup.d1Percentage;
		this.axis = toDup.axis;
		this.time = toDup.time;
		this.generation = toDup.generation;
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

	//setters
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