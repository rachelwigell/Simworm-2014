package dataStructures;


public class Coordinate{
	private float x; //smaller numbers are more anterior, larger numbers are more posterior when used for locations
	private float y; //smaller numbers are more dorsal, larger numbers are more ventral when used for locations
	private float z; //smaller numbers are more right, larger numbers are more left when used for locations

	private Compartment AP;
	private Compartment DV;
	private Compartment LR;

	private float distance;

	/**
	 * Constructor that indicates location
	 * @param x The x coordinate of the location
	 * @param y The y coordinate
	 * @param z The z coordinate
	 */
	public Coordinate(float x, float y, float z){
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
	public Coordinate(Compartment AP, Compartment DV, Compartment LR){
		this.AP = AP;
		this.DV = DV;
		this.LR = LR;
	}

	/**
	 * Duplication constructor for a Coordinate
	 * @param toDup Coordinate to duplicate
	 */
	public Coordinate(Coordinate toDup){
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

	public float getDistance() {
		return distance;
	}

	/**
	 * gets the smallest of the x, y, and z coordinates.
	 * Used for scaling cells back when they were represented as ellipsoids
	 * @return
	 */
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

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	
	/**
	 * returns a set of coordinates such that the smallest one will be .5 and the others will be proportionately larger
	 * @return scaled coordinates
	 */
	public Coordinate lengthsToScale(){
		float smallest = this.getSmallest();
		return new Coordinate(this.x/(smallest*2), this.y/(smallest*2), this.z/(smallest*2)); //divide by two to convert diameter to radius
	}

	/**
	 * the distance between two points in 3D space
	 * @param to the points whose distance from "this" point is computed
	 * @return the distance
	 */
	public float distanceBetween(Coordinate to){
		return (float) Math.sqrt((to.x - this.x) * (to.x-this.x) + (to.y - this.y) * (to.y - this.y) + (to.z - this.z) * (to.z - this.z));
	}
	
	/**
	 * Square of the distance formula for efficiency, since this value frequently gets squared
	 * and the square root operation is expensive
	 * @param to the points whose distance from "this" point is computed
	 * @return the distance
	 */
	public float squareDistance(Coordinate to){
		float distance = (to.x - this.x) * (to.x-this.x) + (to.y - this.y) * (to.y - this.y) + (to.z - this.z) * (to.z - this.z);
		return distance;
	}
	
	public boolean samePoint(Coordinate as){
		return this.x == as.x && this.y == as.y && this.z == as.z;
	}
	
	public float fourthPower(Coordinate to){
		return (float) Math.pow(squareDistance(to), 2);
	}
	
	public Coordinate plus(float amount){
		return new Coordinate(this.x + amount, this.y + amount, this.z + amount);
	}
	
	public void printCoordinate(String identifier){
		System.out.println(identifier + " x: " + x + " y: " + y + " z: " + z);
	}
}