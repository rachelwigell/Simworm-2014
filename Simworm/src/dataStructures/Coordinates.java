package dataStructures;

public class Coordinates {
	private float x; //smaller numbers are more anterior, larger numbers are more posterior when used for locations
	private float y; //smaller numbers are more dorsal, larger numbers are more ventral when used for locations
	private float z; //smaller numbers are more right, larger numbers are more left when used for locations
	
	private Compartment AP;
	private Compartment DV;
	private Compartment LR;
	
	//used to indicate location	
	public Coordinates(float x, float y, float z){
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
	
	//getters
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
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
	
	public float getSmallest(){
		float smallest = this.x;
		if(this.y < smallest){
			smallest = this.y;
		}
		if(this.z < smallest){
			smallest = this.z;
		}
		return smallest;
	}
	
	public Coordinates lengthsToScale(){
		float smallest = this.getSmallest();
		return new Coordinates(this.x/(smallest*2), this.y/(smallest*2), this.z/(smallest*2)); //divide by two to convert diameter to radius
	}
}