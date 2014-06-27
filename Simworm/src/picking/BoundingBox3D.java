package picking;

import dataStructures.Coordinates;
import processing.BasicVisual;

/**
 * a cube that bounds a cell along the three major axis, and preserves relationships between which points on the cube share an edge
 * this is important for object picking
 * @author Rachel
 *
 */
public class BoundingBox3D{
	BasicVisual window;
	public Coordinates leftbottomback;
	public Coordinates leftbottomfront;
	public Coordinates lefttopback;
	public Coordinates lefttopfront;
	public Coordinates rightbottomback;
	public Coordinates rightbottomfront;
	public Coordinates righttopback;
	public Coordinates righttopfront;
	
	public BoundingBox3D(Coordinates righttopfront, Coordinates leftbottomfront, Coordinates rightbottomback, Coordinates rightbottomfront, Coordinates lefttopfront, Coordinates righttopback, Coordinates leftbottomback, Coordinates lefttopback){
		this.righttopfront = righttopfront;
		this.leftbottomfront = leftbottomfront;
		this.rightbottomback = rightbottomback;
		this.rightbottomfront = new Coordinates(rightbottomfront.getX(), rightbottomfront.getY(), rightbottomfront.getZ(), this.leftbottomfront, this.righttopfront, this.rightbottomback);
		this.lefttopfront = lefttopfront;
		this.righttopback = righttopback;
		this.leftbottomback = leftbottomback;
		this.lefttopback = new Coordinates(lefttopback.getX(), lefttopback.getY(), lefttopback.getZ(), this.righttopback, this.leftbottomback, this.lefttopfront);
		this.righttopfront.setxAdjacent(this.lefttopfront);
		this.righttopfront.setzAdjacent(this.righttopback);
		this.leftbottomfront.setyAdjacent(this.lefttopfront);
		this.leftbottomfront.setzAdjacent(this.leftbottomback);
		this.rightbottomback.setxAdjacent(this.leftbottomback);
		this.rightbottomback.setyAdjacent(this.righttopback);
		this.lefttopfront.setxAdjacent(this.righttopfront);
		this.lefttopfront.setyAdjacent(this.leftbottomfront);
		this.righttopback.setyAdjacent(this.rightbottomback);
		this.righttopback.setzAdjacent(this.righttopfront);
		this.leftbottomback.setxAdjacent(this.rightbottomback);
		this.leftbottomback.setzAdjacent(this.leftbottomfront);
		//a lot of these lines of code exist to make the cyclical data behave.
	}
}