package dataStructures;

import java.util.LinkedList;

import picking.Line;
import picking.VersionException;

public class Coordinates{
	private float x; //smaller numbers are more anterior, larger numbers are more posterior when used for locations
	private float y; //smaller numbers are more dorsal, larger numbers are more ventral when used for locations
	private float z; //smaller numbers are more right, larger numbers are more left when used for locations

	private Compartment AP;
	private Compartment DV;
	private Compartment LR;

	private Coordinates xAdjacent;
	private Coordinates yAdjacent;
	private Coordinates zAdjacent;

	private float distance;

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

	public Coordinates(float x, float y, float z, Coordinates xAdjacent, Coordinates yAdjacent, Coordinates zAdjacent){
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

	public Coordinates getxAdjacent() {
		return xAdjacent;
	}

	public Coordinates getyAdjacent() {
		return yAdjacent;
	}

	public Coordinates getzAdjacent() {
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

	public void setxAdjacent(Coordinates xAdjacent) {
		this.xAdjacent = xAdjacent;
	}

	public void setyAdjacent(Coordinates yAdjacent) {
		this.yAdjacent = yAdjacent;
	}

	public void setzAdjacent(Coordinates zAdjacent) {
		this.zAdjacent = zAdjacent;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public Coordinates lengthsToScale(){
		float smallest = this.getSmallest();
		return new Coordinates(this.x/(smallest*2), this.y/(smallest*2), this.z/(smallest*2)); //divide by two to convert diameter to radius
	}

	/**
	 * the distance between two points in 3D space
	 * @param to the points whose distance from "this" point is computed
	 * @return the distance
	 */
	public float distanceBetween(Coordinates to){
		float distance = (float) Math.sqrt((to.x - this.x) * (to.x-this.x) + (to.y - this.y) * (to.y - this.y) + (to.z - this.z) * (to.z - this.z));
		//System.out.println("distance to " + to.name + " is " + distance);
		return distance;
	}

	/**
	 * this recursive function orders sets of points for polygons with 4 points; the order in which the points must be connected to form 1 closed polygon
	 * this is used in object picking to form the bounding polygon
	 * @param pathTaken a list that collects the path taken around the polygon previously, which helps determine what path should be taken in the future
	 * @param pointsLeft the points within the polygon that still remain to be ordered
	 * @param orderSoFar the order that has been accumulated so far
	 * @return the list of coordinates in order
	 */
	public LinkedList<Coordinates> determinePathFour(LinkedList<Integer> pathTaken, LinkedList<Coordinates> pointsLeft, LinkedList<Coordinates> orderSoFar){
		if(pathTaken.size() == 0){ //if we're just starting out, pick a direction at random
			if(pointsLeft.contains(this.xAdjacent)){ //we're still working with a 3D cube at this point. if the point that is connected to the current point along the x-aligned edge is one of the points that has been determined to be in the outer polygon, go ahead and add it to the order
				orderSoFar.add(this.xAdjacent); //add it to the ordered points
				pointsLeft.remove(this.xAdjacent); //remove it from the points left to be ordered
				pathTaken.add(0); //indicate that we went in the x direction with a 0 in pathTaken. 1 means y, 2 means z
			}
			else if(pointsLeft.contains(this.yAdjacent)){
				orderSoFar.add(this.yAdjacent);
				pointsLeft.remove(this.yAdjacent);
				pathTaken.add(1);
			}
			else if(pointsLeft.contains(this.zAdjacent)){
				orderSoFar.add(this.zAdjacent);
				pointsLeft.remove(this.zAdjacent);
				pathTaken.add(2);
			}
		}
		else if(pathTaken.size() == 1){ //if we've already got one edge determined, the next point will depend on what the last point was
			switch(pathTaken.getFirst()){ //what's the last path we took?
			case 0: //if we went in the x direction first, we need to go in y or z next. check for availability and add
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 1:
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 2:
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			}
		}
		else if(pathTaken.size() == 2){ //if we've got two points, we know we now need to go in the same direction as we first went. this is only true for ordering points on four sided polygons. if you're confused, draw a picture
			switch(pathTaken.getFirst()){ //which way did we go the first time?
			case 0: //if we went x first, go x now. etc.
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				break;
			case 1:
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			case 2:
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			}
		}
		if(pointsLeft.size() == 0){ //if there are no more points left to be ordered, we can return now
			return orderSoFar;
		}
		else{ //otherwise, recursively call the function again with the new lists as parameters
			return orderSoFar.getLast().determinePathFour(pathTaken, pointsLeft, orderSoFar);
		}
	}

	/**
	 * this recursive function orders sets of points for polygons with 6 points; the order in which the points must be connected to form 1 closed polygon. this is more complex than its 4 point counterpart.
	 * this is used in object picking to form the bounding polygon
	 * note that the bounding polygon necessarily contains 4 or 6 points (draw a picture)
	 * @param version which version of the function is being run; this will be explained later
	 * @param prevSize the size of the ordered list on the last iteration; this will indicate if an iteration completes without making any progress, in which case we need to throw a versionException because we are stuck.
	 * @param pathTaken a list that collects the path taken around the polygon previously, which helps determine what path should be taken in the future
	 * @param pointsLeft the points within the polygon that still remain to be ordered
	 * @param orderSoFar the order that has been accumulated so far
	 * @return the list of coordinates in order
	 * @throws VersionException if one version of the function doesn't work, a different one will have to be used.
	 */
	public LinkedList<Coordinates> determinePathSix(int version, int prevSize, LinkedList<Integer> pathTaken, LinkedList<Coordinates> pointsLeft, LinkedList<Coordinates> orderSoFar) throws VersionException{
		if(pathTaken.size() == 0 || pathTaken.size() == 3){ //if we are just starting out, or if we have three points ordered already, there is no way to know which direction will be productive next, so choose randomly.
			//the order in which directions are considered depends on the version used, and in fact this is the only difference between versions.
			switch(version){
			case 0: //in version 0 we try to move in the x direction first, in version 1 y, etc.
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 1:
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				else if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				break;
			case 2:
				if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				else if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			}
		}
		else if(pathTaken.size() == 1 || pathTaken.size() == 4){ //if we've already moved in one direction (or 4, since you essentially "start over" at 3), we have a little more information on which direction to go next
			switch(pathTaken.getLast()){ //it depends on which direction we moved last time
			case 0: //if we went x, we should only consider y or z next. etc.
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 1:
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 2:
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			}
		}
		else if(pathTaken.size() == 2){ //once we've got 2 points done, we want to add whatever is available now. most likely, it will only be whichever one we haven't done yet, but it is possible that other directions will be required in edge cases, so they are considered secondarily
			switch(pathTaken.getFirst() + pathTaken.getLast()){ // the sum of the previous two directions is unique and indicates where we've gone previously. 0+1 is x and y (order doesn't matter), 1+2 is y and z, 0+2 is x and z. it's impossible to go the same direction twice during the first two iterations.
			case 1:
				if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				break;
			case 2:
				if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				else if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				break;
			case 3:
				if(pointsLeft.contains(this.xAdjacent)){
					orderSoFar.add(this.xAdjacent);
					pointsLeft.remove(this.xAdjacent);
					pathTaken.add(0);
				}
				else if(pointsLeft.contains(this.zAdjacent)){
					orderSoFar.add(this.zAdjacent);
					pointsLeft.remove(this.zAdjacent);
					pathTaken.add(2);
				}
				else if(pointsLeft.contains(this.yAdjacent)){
					orderSoFar.add(this.yAdjacent);
					pointsLeft.remove(this.yAdjacent);
					pathTaken.add(1);
				}
				break;
			}
		}

		if(pointsLeft.size() == 0){ //if there are no points left to order, we're done
			return orderSoFar;
		}
		else if(orderSoFar.size() == prevSize){ //if we didn't make any progress during the last iteration, we're stuck. we need to throw a versionException and try again with a different version of the function.
			throw new VersionException();
		}
		else{ //otherwise, we've made progress so we need to add onto prevSize and call the function recursively with the updated parameters.
			prevSize++;
			return orderSoFar.getLast().determinePathSix(version, prevSize, pathTaken, pointsLeft, orderSoFar);
		}
	}

	/**
	 * determines whether the angles of all the lines radiating from "this" point sum to 360.
	 * this is used in object picking to determine which points in the bounding cube are along the outside, forming the bounding polygon
	 * pass in coordinates in screen space only!
	 * @param xAdj the point adjacent to this point along the x direction
	 * @param yAdj the point adjacent to this point along the y direction
	 * @param zAdj the point adjacent to this point along the z direction
	 * @return true if the angles between these lines sum to 360
	 */
	public boolean sum360(Coordinates xAdj, Coordinates yAdj, Coordinates zAdj){		
		Line x = new Line(this, xAdj); //create a line object between this point and each of its adjacent points
		Line y = new Line(this, yAdj);
		Line z = new Line(this, zAdj);

		float xy = x.angleBetween(y); //get the angle between the x and y line, etc.
		float yz = y.angleBetween(z);
		float zx = z.angleBetween(x);

		if(xy + yz + zx > 359.8) return true; //leave a little leeway for floating point rounding errors
		else return false;
	}
}
