package dataStructures;

public class Coordinates {
	private float x; //smaller numbers are more anterior, larger numbers are more posterior when used for locations
	private float y; //smaller numbers are more dorsal, larger numbers are more ventral when used for locations
	private float z; //smaller numbers are more right, larger numbers are more left when used for locations
	
	private Compartment AP;
	private Compartment DV;
	private Compartment LR;
	
	/**
	 * Constructor that indicates location
	 * @param x The x coordinate of the location
	 * @param y The y coordinate
	 * @param z The z coordinate
	 */
	public Coordinates(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructor that indicates compartment
	 * @param AP The compartment on the anterior-posterior axis
	 * @param DV The compartment on the dorsal-ventral axis
	 * @param LR The compartment on the left-right axis
	 */
	public Coordinates(Compartment AP, Compartment DV, Compartment LR){
		this.AP = AP;
		this.DV = DV;
		this.LR = LR;
	}
	
	public Coordinates(Coordinates toDup){
		this.AP = toDup.AP;
		this.DV = toDup.DV;
		this.LR = toDup.LR;
		this.x = toDup.x;
		this.y = toDup.y;
		this.z = toDup.z;
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
	
	public void setAP(Compartment AP) {
		this.AP = AP;
	}
	
	public void setX(float x){
		this.x = x;
	}

	public Coordinates lengthsToScale(){
		float smallest = this.getSmallest();
		return new Coordinates(this.x/(smallest*2), this.y/(smallest*2), this.z/(smallest*2)); //divide by two to convert diameter to radius
	}
}
