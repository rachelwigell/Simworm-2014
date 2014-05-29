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

	public float distanceBetween(Coordinates to){
		float distance = (float) Math.sqrt((to.x - this.x) * (to.x-this.x) + (to.y - this.y) * (to.y - this.y) + (to.z - this.z) * (to.z - this.z));
		//System.out.println("distance to " + to.name + " is " + distance);
		return distance;
	}

	public LinkedList<Coordinates> determinePathFour(LinkedList<Integer> pathTaken, LinkedList<Coordinates> pointsLeft, LinkedList<Coordinates> orderSoFar){
		if(pathTaken.size() == 0){
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
		}
		else if(pathTaken.size() == 1){
			switch(pathTaken.getFirst()){
			case 0:
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
		else if(pathTaken.size() == 2){
			switch(pathTaken.getFirst()){
			case 0:
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
		if(pointsLeft.size() == 0){
			return orderSoFar;
		}
		else{
			return orderSoFar.getLast().determinePathFour(pathTaken, pointsLeft, orderSoFar);
		}
	}

	public LinkedList<Coordinates> determinePathSix(int version, int prevSize, LinkedList<Integer> pathTaken, LinkedList<Coordinates> pointsLeft, LinkedList<Coordinates> orderSoFar) throws VersionException{
		if(pathTaken.size() == 0 || pathTaken.size() == 3){
			switch(version){
			case 0:
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
		else if(pathTaken.size() == 1 || pathTaken.size() == 4){
			switch(pathTaken.getLast()){
			case 0:
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
		else if(pathTaken.size() == 2){
			switch(pathTaken.getFirst() + pathTaken.getLast()){
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

		if(pointsLeft.size() == 0){
			return orderSoFar;
		}
		else if(orderSoFar.size() == prevSize){
			throw new VersionException();
		}
		else{
			prevSize++;
			return orderSoFar.getLast().determinePathSix(version, prevSize, pathTaken, pointsLeft, orderSoFar);
		}
	}

	//pass in points in screen space	
	public boolean sum360(Coordinates xAdj, Coordinates yAdj, Coordinates zAdj){		
		Line x = new Line(this, xAdj);
		Line y = new Line(this, yAdj);
		Line z = new Line(this, zAdj);

		float xy = x.angleBetween(y);
		float yz = y.angleBetween(z);
		float zx = z.angleBetween(x);

		if(xy + yz + zx > 359.8) return true;
		else return false;
	}
}
