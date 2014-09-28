package dataStructures;


public class Coordinate{
	private float x; //smaller numbers are more anterior, larger numbers are more posterior when used for locations
	private float y; //smaller numbers are more dorsal, larger numbers are more ventral when used for locations
	private float z; //smaller numbers are more right, larger numbers are more left when used for locations

	private Compartment AP;
	private Compartment DV;
	private Compartment LR;

	private Coordinate xAdjacent;
	private Coordinate yAdjacent;
	private Coordinate zAdjacent;

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

	public Coordinate(Coordinate toDup){
		this.AP = toDup.AP;
		this.DV = toDup.DV;
		this.LR = toDup.LR;
		this.x = toDup.x;
		this.y = toDup.y;
		this.z = toDup.z;
	}

	public Coordinate(float x, float y, float z, Coordinate xAdjacent, Coordinate yAdjacent, Coordinate zAdjacent){
		this.x = x;
		this.y = y;
		this.z = z;

		this.xAdjacent = xAdjacent;
		this.yAdjacent = yAdjacent;
		this.zAdjacent = zAdjacent;

		xAdjacent.xAdjacent = this;
		yAdjacent.yAdjacent = this;
		zAdjacent.zAdjacent = this;
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

	public Coordinate getxAdjacent() {
		return xAdjacent;
	}

	public Coordinate getyAdjacent() {
		return yAdjacent;
	}

	public Coordinate getzAdjacent() {
		return zAdjacent;
	}

	public float getDistance() {
		return distance;
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

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setxAdjacent(Coordinate xAdjacent) {
		this.xAdjacent = xAdjacent;
	}

	public void setyAdjacent(Coordinate yAdjacent) {
		this.yAdjacent = yAdjacent;
	}

	public void setzAdjacent(Coordinate zAdjacent) {
		this.zAdjacent = zAdjacent;
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
		float distance = (float) (to.x - this.x) * (to.x-this.x) + (to.y - this.y) * (to.y - this.y) + (to.z - this.z) * (to.z - this.z);
		return distance;
	}
	
	public boolean samePoint(Coordinate as){
		return this.x == as.x && this.y == as.y && this.z == as.z;
	}
	
	public float fourthPower(Coordinate to){
		return (float) Math.pow(squareDistance(to), 2);
	}
}