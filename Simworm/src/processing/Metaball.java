package processing;

import dataStructures.Coordinate;
import dataStructures.RGB;

public class Metaball {
	private Coordinate center;
	private float charge;
	private RGB color;
	private float radiusOfInfluence;
	
	/**
	 * Constructor for a metaball
	 * @param x x coordinate of its center point
	 * @param y y coordinate of its center point
	 * @param z z coordinate of its center point
	 * @param charge charge of the metaball
	 * @param r r value of its color
	 * @param g g value of its color
	 * @param b b value of its color
	 */
	public Metaball(float x, float y, float z, float charge, int r, int g, int b){
		this.center = new Coordinate(x, y, z);
		this.charge = charge;
		this.color = new RGB(r, g, b);
		this.radiusOfInfluence = (float) (Math.sqrt(charge))/500.0f;
	}
	
	/**
	 * duplication constructor for Metaballs
	 * @param toDup the Metaball to duplicate
	 */
	public Metaball(Metaball toDup){
		this.center = new Coordinate(toDup.getCenter());
		this.charge = toDup.charge;
		this.color = new RGB(toDup.color);
		this.radiusOfInfluence = toDup.radiusOfInfluence;
	}
	
	public Coordinate getCenter(){
		return center;
	}
	
	public float getCharge(){
		return charge;
	}
	
	public RGB getColor(){
		return color;
	}
	
	public float getRadiusOfInfluence(){
		return radiusOfInfluence;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	/**
	 * Changes the location of the metaball by setting its center point
	 * @param to Coordinate to move to
	 */
	public void move(Coordinate to){
		this.center = to;
	}
	
	/**
	 * The distance between a coordinate and the center of this metaball
	 * @param at the coordinate we're measuring between
	 * @return the distance
	 */
	public float distanceFromCenter(Coordinate at){
		return this.center.distanceBetween(at);
	}
	
	/**
	 * The square of the distance between a coordinate and the center of this metaball
	 * for efficiency, because this value is frequently used and the sqrt function is slow
	 * @param at the coordinate we're measuing between
	 * @return the squared distance
	 */
	public float squaredDistanceFromCenter(Coordinate at){
		return this.center.squareDistance(at);
	}
	
	/**
	 * The charge caused by this metaball at the given point
	 * @param at the point at which we're calculating charge
	 * @return the charge
	 */
	public float chargeFrom(Coordinate at){
		float rad = this.squaredDistanceFromCenter(at);
		return this.charge/rad;
	}
	
	/**
	 * The charge caused by this metaball at the given point
	 * when using the new fourth-power equation
	 * @param at the point at which we're calculating charge
	 * @return the charge
	 */
	public float fourthPowerChargeFrom(Coordinate at){
		return this.charge / this.center.fourthPower(at);
	}
}
