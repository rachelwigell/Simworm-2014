package picking;

import dataStructures.Coordinates;

public class Line {
	Coordinates tail;
	Coordinates tip;
	
	public Line(Coordinates tail, Coordinates tip){
		this.tail = tail;
		this.tip = tip;
	}
	
	/**
	 * @return the magnitude of the x component of a line (vector)
	 */
	public float vectorX(){
		return this.tip.getX() - this.tail.getX();
	}
	
	/**
	 * @return the magnitude of the y component of a line (vector)
	 */
	public float vectorY(){
		return this.tip.getY() - this.tail.getY();
	}
	
	/**
	 * @return the length of a line; this is always positive
	 */
	public float length(){
		return tip.distanceBetween(tail);
	}
	
	/**
	 * the dot product between two lines
	 * @param with the line whose dot product with "this" line is computed
	 * @return the dot product
	 */
	public float dotProduct(Line with){
		return (this.vectorX() * with.vectorX() + this.vectorY() * with.vectorY());
	}
	
	/**
	 * finds the angle between two lines
	 * @param with the line whose angle between "this" line is computed
	 * @return the angle between the two lines, in degrees. output ranges from 0-180.
	 */
	public float angleBetween(Line with){
		float dotProduct = this.dotProduct(with);
		float cos = dotProduct / (this.length() * with.length());
		return (float) Math.toDegrees(Math.acos(cos));
	}
}