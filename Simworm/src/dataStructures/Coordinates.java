package dataStructures;

public class Coordinates {
	private double x; //smaller numbers are more anterior, larger numbers are more posterior
	private double y; //smaller numbers are more dorsal, larger numbers are more ventral
	private double z; //smaller numbers are more right, larger numbers are more left
	
	private Compartment AP;
	private Compartment DV;
	private Compartment LR;
	
	//used to indicate location	
	public Coordinates(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//used in genes to indicate compartment
	public Coordinates(Compartment AP, Compartment DV, Compartment LR){
		this.AP = AP;
		this.DV = DV;
		this.LR = LR;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Compartment getAP() {
		return AP;
	}

	public Compartment getDV() {
		return DV;
	}

	public Compartment getLR() {
		return LR;
	}
}
