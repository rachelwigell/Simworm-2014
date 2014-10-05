package processing;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Coordinate;


public class Cube {
	BasicVisual screen;

	Coordinate vertex1;
	Coordinate vertex2;
	Coordinate vertex3;
	Coordinate vertex4;
	Coordinate vertex5;
	Coordinate vertex6;
	Coordinate vertex7;
	Coordinate vertex8;

	Coordinate midpoint12;
	Coordinate midpoint14;
	Coordinate midpoint15;
	Coordinate midpoint23;
	Coordinate midpoint26;
	Coordinate midpoint34;
	Coordinate midpoint37;
	Coordinate midpoint48;
	Coordinate midpoint56;
	Coordinate midpoint58;
	Coordinate midpoint67;
	Coordinate midpoint78;

	boolean mid12;
	boolean mid14;
	boolean mid15;
	boolean mid23;
	boolean mid26;
	boolean mid34;
	boolean mid37;
	boolean mid48;
	boolean mid56;
	boolean mid58;
	boolean mid67;
	boolean mid78;

	HashMap<Coordinate, Boolean> vertices;
	ArrayList<Coordinate> midpoints;

	/**
	 * Constructor for 1 marching cube
	 * @param screen The PApplet where this cube will be displayed
	 * @param x the x coordinate of vertex1 of the cube
	 * @param y the y coordinate of vertex1
	 * @param z the z coordinate of vertex1
	 * @param gridSize the size of the grid being used in screen
	 */
	public Cube(BasicVisual screen, int x, int y, int z, int gridSize){
		this.screen = screen;
		int half = gridSize/2;
		vertex1 = new Coordinate(x, y, z);
		vertex2 = new Coordinate(x+gridSize, y, z);
		vertex3 = new Coordinate(x+gridSize, y+gridSize, z);
		vertex4 = new Coordinate(x, y+gridSize, z);
		vertex5 = new Coordinate(x, y, z+gridSize);
		vertex6 = new Coordinate(x+gridSize, y, z+gridSize);
		vertex7 = new Coordinate(x+gridSize, y+gridSize, z+gridSize);
		vertex8 = new Coordinate(x, y+gridSize, z+gridSize);

		midpoint12 = new Coordinate(x+half, y, z);
		midpoint14 = new Coordinate(x, y+half, z);
		midpoint15 = new Coordinate(x, y, z+half);
		midpoint23 = new Coordinate(x+gridSize, y+half, z);
		midpoint26 = new Coordinate(x+gridSize, y, z+half);
		midpoint34 = new Coordinate(x+half, y+gridSize, z);
		midpoint37 = new Coordinate(x+gridSize, y+gridSize, z+half);
		midpoint48 = new Coordinate(x, y+gridSize, z+half);
		midpoint56 = new Coordinate(x+half, y, z+gridSize);
		midpoint58 = new Coordinate(x, y+half, z+gridSize);
		midpoint67 = new Coordinate(x+gridSize, y+half, z+gridSize);
		midpoint78 = new Coordinate(x+half, y+gridSize, z+gridSize);

		vertices = new HashMap<Coordinate, Boolean>();
		midpoints = new ArrayList<Coordinate>();
	}

	/**
	 * implementation of exclusive or
	 * @param one first boolean value
	 * @param two second boolean value
	 * @return true if one or two is true but not if both are true
	 */
	public boolean exclusiveOr(boolean one, boolean two){
		if(one && two) return false;
		if(!one && !two) return false;
		return true;
	}

	/**
	 * populates the midpoint values that determine what cube situation this is
	 * a midpoint is true if exactly one of the vertices on its line is true
	 */
	public void populateMids(){
		mid12 = exclusiveOr(vertices.get(vertex1), vertices.get(vertex2));
		mid14 = exclusiveOr(vertices.get(vertex1), vertices.get(vertex4)); 
		mid15 = exclusiveOr(vertices.get(vertex1), vertices.get(vertex5));
		mid23 = exclusiveOr(vertices.get(vertex2), vertices.get(vertex3));
		mid26 = exclusiveOr(vertices.get(vertex2), vertices.get(vertex6));
		mid34 = exclusiveOr(vertices.get(vertex3), vertices.get(vertex4));
		mid37 = exclusiveOr(vertices.get(vertex3), vertices.get(vertex7));
		mid48 = exclusiveOr(vertices.get(vertex4), vertices.get(vertex8));
		mid56 = exclusiveOr(vertices.get(vertex5), vertices.get(vertex6));
		mid58 = exclusiveOr(vertices.get(vertex5), vertices.get(vertex8));
		mid67 = exclusiveOr(vertices.get(vertex6), vertices.get(vertex7));
		mid78 = exclusiveOr(vertices.get(vertex7), vertices.get(vertex8));

		if(mid12) midpoints.add(midpoint12);
		if(mid14) midpoints.add(midpoint14);
		if(mid15) midpoints.add(midpoint15);
		if(mid23) midpoints.add(midpoint23);
		if(mid26) midpoints.add(midpoint26);
		if(mid34) midpoints.add(midpoint34);
		if(mid37) midpoints.add(midpoint37);
		if(mid48) midpoints.add(midpoint48);
		if(mid56) midpoints.add(midpoint56);
		if(mid58) midpoints.add(midpoint58);
		if(mid67) midpoints.add(midpoint67);
		if(mid78) midpoints.add(midpoint78);
	}

	/**
	 * Returns the vertices for this cube shape
	 * in the case that three midpoints were true
	 * @return the list of vertices
	 */
	public ArrayList<Coordinate> threeCase(){
		ArrayList<Coordinate> vertices = new ArrayList<Coordinate>();
		for(Coordinate c: midpoints){
			vertices.add(c);
		}
		return vertices;
	}

	/**
	 * returns a list of shapes (each of which is a list of vertices)
	 * in the case that four midpoints were true
	 * @return the list of shapes
	 */
	public ArrayList<Coordinate> fourCase(){
		ArrayList<Coordinate> shape = new ArrayList<Coordinate>();
		/****exclude an edge****/
		//exclude edge12
		if(mid14 && mid23 && mid26 && mid15){
			shape.add(midpoint14);
			shape.add(midpoint23);
			shape.add(midpoint26);
			shape.add(midpoint15);
			return shape;
		}
		//exclude edge34
		else if(mid14 && mid23 && mid37 && mid48){
			shape.add(midpoint14);
			shape.add(midpoint23);
			shape.add(midpoint37);
			shape.add(midpoint48);
			return shape;
		}
		//exclude edge56
		else if(mid58 && mid15 && mid26 && mid67){
			shape.add(midpoint58);
			shape.add(midpoint15);
			shape.add(midpoint26);
			shape.add(midpoint67);
			return shape;
		}
		//exclude edge78
		else if(mid48 && mid37 && mid67 && mid58){
			shape.add(midpoint48);
			shape.add(midpoint37);
			shape.add(midpoint67);
			shape.add(midpoint58);
			return shape;
		}
		//exclude edge48
		else if(mid14 && mid58 && mid78 && mid34){
			shape.add(midpoint14);
			shape.add(midpoint58);
			shape.add(midpoint78);
			shape.add(midpoint34);
			return shape;
		}
		//exclude edge37
		else if(mid78 && mid34 && mid23 && mid67){
			shape.add(midpoint78);
			shape.add(midpoint34);
			shape.add(midpoint23);
			shape.add(midpoint67);
			return shape;
		}
		//exclude edge15
		else if(mid14 && mid12 && mid56 && mid58){
			shape.add(midpoint14);
			shape.add(midpoint12);
			shape.add(midpoint56);
			shape.add(midpoint58);
			return shape;
		}
		//exclude edge26
		else if(mid23 && mid67 && mid56 && mid12){
			shape.add(midpoint23);
			shape.add(midpoint67);
			shape.add(midpoint56);
			shape.add(midpoint12);
			return shape;
		}
		//exclude edge14
		else if(mid48 && mid34 && mid12 && mid15){
			shape.add(midpoint48);
			shape.add(midpoint34);
			shape.add(midpoint12);
			shape.add(midpoint15);
			return shape;
		}
		//exclude edge58
		else if(mid78 && mid48 && mid15 && mid56){
			shape.add(midpoint78);
			shape.add(midpoint48);
			shape.add(midpoint15);
			shape.add(midpoint56);
			return shape;
		}
		//exclude edge23
		else if(mid34 && mid37 && mid26 && mid12){
			shape.add(midpoint34);
			shape.add(midpoint37);
			shape.add(midpoint26);
			shape.add(midpoint12);
			return shape;
		}
		//exclude edge67
		else if(mid78 && mid37 && mid26 && mid56){
			shape.add(midpoint78);
			shape.add(midpoint37);
			shape.add(midpoint26);
			shape.add(midpoint56);
			return shape;
		}

		/*****planes******/
		//xz
		else if(mid14 && mid23 && mid67 && mid58){
			shape.add(midpoint14);
			shape.add(midpoint23);
			shape.add(midpoint67);
			shape.add(midpoint58);
			return shape;
		}
		//xy
		else if(mid48 && mid37 && mid26 && mid15){
			shape.add(midpoint48);
			shape.add(midpoint37);
			shape.add(midpoint26);
			shape.add(midpoint15);
			return shape;
		}
		//yz
		else if(mid34 && mid78 && mid56 && mid12){
			shape.add(midpoint34);
			shape.add(midpoint78);
			shape.add(midpoint56);
			shape.add(midpoint12);
			return shape;
		}
		System.out.println("missing cases in four case");
		return shape;
	}

	/**
	 * returns a list of shapes (each of which is a list of vertices)
	 * in the case that seven midpoints were true
	 * @return the list of shapes
	 */
	public ArrayList<ArrayList<Coordinate>> sevenCase(){
		ArrayList<ArrayList<Coordinate>> shapes = new ArrayList<ArrayList<Coordinate>>();
		//exclude vertex7 and edge12
		if(mid78 && mid37 && mid67 && mid23 && mid14 && mid15 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint37);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint23);
			shape2.add(midpoint14);
			shape2.add(midpoint15);
			shape2.add(midpoint26);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex8 and edge12
		else if(mid78 && mid48 && mid58 && mid23 && mid14 && mid15 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint48);
			shape1.add(midpoint58);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint23);
			shape2.add(midpoint14);
			shape2.add(midpoint15);
			shape2.add(midpoint26);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex4 and edge56
		else if(mid14 && mid34 && mid48 && mid58 && mid15 && mid26 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint34);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shape2.add(midpoint26);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex3 and edge56
		else if(mid23 && mid34 && mid37 && mid58 && mid15 && mid26 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint23);
			shape1.add(midpoint34);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shape2.add(midpoint26);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex8 and edge26
		else if(mid48 && mid58 && mid78 && mid23 && mid67 && mid56 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint58);
			shape1.add(midpoint78);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint23);
			shape2.add(midpoint67);
			shape2.add(midpoint56);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex4 and edge26
		else if(mid14 && mid34 && mid48 && mid23 && mid67 && mid56 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint34);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint23);
			shape2.add(midpoint67);
			shape2.add(midpoint56);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex3 and edge15
		else if(mid37 && mid34 && mid23 && mid14 && mid12 && mid56 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint34);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint12);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex7 and edge15
		else if(mid37 && mid78 && mid67 && mid14 && mid12 && mid56 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint78);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint12);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex2 and edge48
		if(mid26 && mid12 && mid23 && mid14 && mid58 && mid78 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shape2.add(midpoint34);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex6 and edge48
		else if(mid26 && mid67 && mid56 && mid14 && mid58 && mid78 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint67);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shape2.add(midpoint34);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex1 and edge37
		else if(mid12 && mid15 && mid14 && mid78 && mid34 && mid23 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint15);
			shape1.add(midpoint14);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex5 and edge37
		else if(mid56 && mid15 && mid58 && mid78 && mid34 && mid23 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint15);
			shape1.add(midpoint58);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex5 and edge34
		else if(mid56 && mid15 && mid58 && mid14 && mid23 && mid37 && mid48){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint15);
			shape1.add(midpoint58);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint23);
			shape2.add(midpoint37);
			shape2.add(midpoint48);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex6 and edge34
		else if(mid56 && mid67 && mid26 && mid14 && mid23 && mid37 && mid48){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint23);
			shape2.add(midpoint37);
			shape2.add(midpoint48);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex1 and edge78
		else if(mid12 && mid14 && mid15 && mid48 && mid37 && mid67 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shape2.add(midpoint58);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex2 and edge78
		else if(mid12 && mid26 && mid23 && mid48 && mid37 && mid67 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint26);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shape2.add(midpoint58);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex7 and edge14
		else if(mid67 && mid78 && mid37 && mid48 && mid34 && mid12 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint67);
			shape1.add(midpoint78);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shape2.add(midpoint12);
			shape2.add(midpoint15);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex6 and edge14
		else if(mid67 && mid26 && mid56 && mid48 && mid34 && mid12 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shape2.add(midpoint12);
			shape2.add(midpoint15);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex5 and edge23
		else if(mid56 && mid58 && mid15 && mid34 && mid37 && mid26 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint58);
			shape1.add(midpoint15);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex8 and edge23
		else if(mid78 && mid58 && mid48 && mid34 && mid37 && mid26 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint58);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex3 and edge58
		else if(mid23 && mid34 && mid37 && mid78 && mid48 && mid15 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint23);
			shape1.add(midpoint34);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex2 and edge58
		else if(mid23 && mid12 && mid26 && mid78 && mid48 && mid15 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint23);
			shape1.add(midpoint12);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex1 and edge67
		else if(mid15 && mid12 && mid14 && mid78 && mid37 && mid26 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//exclude vertex4 and edge67
		else if(mid34 && mid48 && mid14 && mid78 && mid37 && mid26 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint48);
			shape1.add(midpoint14);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		System.out.println("missing cases in seven case");
		return shapes;
	}

	/**
	 * returns a list of shapes (each of which is a list of vertices)
	 * in the case that eight midpoints were true
	 * @return the list of shapes
	 */
	public ArrayList<ArrayList<Coordinate>> eightCase(){
		ArrayList<ArrayList<Coordinate>> shapes = new ArrayList<ArrayList<Coordinate>>();
		/****exclude 2 edges *****/
		//exclude edge48 and edge 26
		if(mid14 && mid58 && mid78 && mid34 && mid23 && mid67 && mid56 && mid12){
			boolean above1 = screen.pollField(vertex1);
			if(!above1){
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint14);
				shape1.add(midpoint58);
				shape1.add(midpoint78);
				shape1.add(midpoint34);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint23);
				shape2.add(midpoint67);
				shape2.add(midpoint56);
				shape2.add(midpoint12);
				shapes.add(shape2);
				return shapes;
			}
			//SAME AS ABOVE: handle ambiguity
			//exclude edge37 and edge15
			else{
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint78);
				shape1.add(midpoint34);
				shape1.add(midpoint23);
				shape1.add(midpoint67);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint14);
				shape2.add(midpoint12);
				shape2.add(midpoint56);
				shape2.add(midpoint58);
				shapes.add(shape2);
				return shapes;
			}
		}
		//exclude edge12 and edge78
		else if(mid14 && mid23 && mid26 && mid15 && mid48 && mid37 && mid67 && mid58){
			boolean above1 = screen.pollField(vertex1);
			if(above1){
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint14);
				shape1.add(midpoint23);
				shape1.add(midpoint26);
				shape1.add(midpoint15);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint48);
				shape2.add(midpoint37);
				shape2.add(midpoint67);
				shape2.add(midpoint58);
				shapes.add(shape2);
				return shapes;	
			}
			//exclude edge34 and edge56
			else{
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint14);
				shape1.add(midpoint23);
				shape1.add(midpoint37);
				shape1.add(midpoint48);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint58);
				shape2.add(midpoint15);
				shape2.add(midpoint26);
				shape2.add(midpoint67);
				shapes.add(shape2);
				return shapes;
			}
		}

		//exclude edge14 and edge67
		else if(mid48 && mid34 && mid12 && mid15 && mid78 && mid37 && mid26 && mid56){
			boolean above1 = screen.pollField(vertex1);
			if(above1){
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint48);
				shape1.add(midpoint34);
				shape1.add(midpoint12);
				shape1.add(midpoint15);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint78);
				shape2.add(midpoint37);
				shape2.add(midpoint26);
				shape2.add(midpoint56);
				shapes.add(shape2);
				return shapes;
			}
			else{
				//exclude edge58 and edge23
				ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
				shape1.add(midpoint78);
				shape1.add(midpoint48);
				shape1.add(midpoint15);
				shape1.add(midpoint56);
				shapes.add(shape1);
				ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
				shape2.add(midpoint34);
				shape2.add(midpoint37);
				shape2.add(midpoint26);
				shape2.add(midpoint12);
				shapes.add(shape2);
				return shapes;
			}
		}

		/*******trapezoid******/
		//face 1234 facing foward
		//vertex1 lower left, triangle around 4
		if(mid15 && mid58 && mid67 && mid23 && mid12 && mid48 && mid14 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint58);
			shape2.add(midpoint23);
			shape2.add(midpoint12);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint14);
			shape3.add(midpoint34);
			shapes.add(shape3);
			return shapes;
		}
		//vertex2 lower left, triangle around 3
		else if(mid14 && mid48 && mid78 && mid56 && mid12 && mid23 && mid37 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint12);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint23);
			shape3.add(midpoint37);
			shape3.add(midpoint34);
			shapes.add(shape3);
			return shapes;
		}
		//vertex3 lower left, triangle around 2
		else if(mid14 && mid58 && mid67 && mid34 && mid37 && mid12 && mid23 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint12);
			shape3.add(midpoint23);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//vertex4 lower left, triangle around 1
		else if(mid34 && mid78 && mid56 && mid23 && mid26 && mid12 && mid14 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint14);
			shape3.add(midpoint12);
			shapes.add(shape3);
			return shapes;
		}
		//face2367 in front
		//vertex2 lower left, triangle around 8
		else if(mid14 && mid67 && mid23 && mid15 && mid56 && mid48 && mid58 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint14);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint58);
			shape3.add(midpoint78);
			shapes.add(shape3);
			return shapes;
		}
		//vertex6 lower left, triangle around 7
		else if(mid34 && mid12 && mid56 && mid48 && mid58 && mid78 && mid37 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint12);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shape2.add(midpoint48);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint78);
			shape3.add(midpoint67);
			shape3.add(midpoint37);
			shapes.add(shape3);
			return shapes;
		}
		//vertex7 lower left, triangle around 6
		else if(mid14 && mid58 && mid23 && mid78 && mid37 && mid67 && mid56 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint23);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint26);
			shape3.add(midpoint67);
			shape3.add(midpoint56);
			shapes.add(shape3);
			return shapes;
		}
		//vertex3 lower left, triangle around 5
		else if(mid34 && mid78 && mid12 && mid67 && mid26 && mid15 && mid56 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint58);
			shape3.add(midpoint56);
			shapes.add(shape3);
			return shapes;
		}
		//face 5678 in front
		//vertex6 lower left, triangle around 7
		else if(mid14 && mid58 && mid23 && mid56 && mid26 && mid37 && mid78 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint56);
			shape2.add(midpoint26);
			shape2.add(midpoint23);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint67);
			shape3.add(midpoint78);
			shape3.add(midpoint37);
			shapes.add(shape3);
			return shapes;
		}
		//vertex5 lower left, triangle around 6
		else if(mid12 && mid34 && mid78 && mid15 && mid58 && mid67 && mid26 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint12);
			shape2.add(midpoint15);
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint67);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//vertex8 lower left, triangle around 5
		else if(mid14 && mid67 && mid23 && mid48 && mid78 && mid15 && mid56 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint58);
			shape3.add(midpoint56);
			shapes.add(shape3);
			return shapes;
		}
		//vertex7 lower left, triangle around 8
		else if(mid34 && mid12 && mid56 && mid37 && mid67 && mid48 && mid78 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint12);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shape2.add(midpoint56);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint58);
			shape3.add(midpoint78);
			shapes.add(shape3);
			return shapes;
		}
		//face1584 in front
		//vertex5 lower left, triangle around 3
		else if(mid14 && mid58 && mid67 && mid12 && mid26 && mid34 && mid37 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint12);
			shape2.add(midpoint26);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint37);
			shape3.add(midpoint34);
			shape3.add(midpoint23);
			shapes.add(shape3);
			return shapes;
		}
		//vertex1 lower left, triangle around 2
		else if(mid34 && mid78 && mid56 && mid15 && mid14 && mid12 && mid23 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint14);
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint12);
			shape3.add(midpoint23);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//vertex4 lower left, triangle around 1
		else if(mid58 && mid67 && mid23 && mid34 && mid48 && mid14 && mid12 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shape2.add(midpoint23);
			shape2.add(midpoint34);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint12);
			shape3.add(midpoint14);
			shapes.add(shape3);
			return shapes;
		}
		//vertex8 lower left, triangle around 4
		else if(mid78 && mid56 && mid12 && mid37 && mid23 && mid14 && mid34 && mid48){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint23);
			shape2.add(midpoint12);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint14);
			shape3.add(midpoint34);
			shapes.add(shape3);
			return shapes;
		}
		//face3487 in front
		//vertex4 lower left, triangle around 1
		else if(mid48 && mid37 && mid26 && mid58 && mid56 && mid14 && mid15 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint14);
			shape3.add(midpoint12);
			shapes.add(shape3);
			return shapes;
		}
		//vertex3 lower left, triangle around 4
		else if(mid15 && mid26 && mid37 && mid78 && mid58 && mid34 && mid48 && mid14){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint14);
			shape3.add(midpoint48);
			shape3.add(midpoint34);
			shapes.add(shape3);
			return shapes;
		}
		//vertex7 lower left, triangle around 3
		else if(mid48 && mid15 && mid26 && mid78 && mid67 && mid37 && mid34 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint15);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint37);
			shape3.add(midpoint34);
			shape3.add(midpoint23);
			shapes.add(shape3);
			return shapes;
		}
		//vertex8 lower left, triangle around 2
		else if(mid15 && mid48 && mid37 && mid56 && mid67 && mid23 && mid26 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint37);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint12);
			shape3.add(midpoint26);
			shape3.add(midpoint23);
			shapes.add(shape3);
			return shapes;
		}
		//face1265 in front
		//vertex5 lower left, triangle around 8
		else if(mid15 && mid26 && mid37 && mid14 && mid34 && mid48 && mid78 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint15);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint58);
			shape3.add(midpoint78);
			shapes.add(shape3);
			return shapes;
		}
		//vertex6 lower left, triangle around 7
		else if(mid48 && mid15 && mid26 && mid34 && mid23 && mid78 && mid37 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint15);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint26);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint67);
			shape3.add(midpoint37);
			shape3.add(midpoint78);
			shapes.add(shape3);
			return shapes;
		}
		//vertex2 lower left, triangle around 6
		else if(mid15 && mid48 && mid37 && mid12 && mid23 && mid67 && mid26 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint15);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint12);
			shape2.add(midpoint23);
			shape2.add(midpoint37);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint26);
			shape3.add(midpoint67);
			shape3.add(midpoint56);
			shapes.add(shape3);
			return shapes;
		}
		//vertex1 lower left, triangle around 5
		else if(mid48 && mid26 && mid37 && mid14 && mid12 && mid56 && mid58 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint56);
			shape3.add(midpoint58);
			shapes.add(shape3);
			return shapes;
		}
		System.out.println("missing cases in eight case");
		return shapes;
	}	

	public ArrayList<ArrayList<Coordinate>> nineCase(){
		ArrayList<ArrayList<Coordinate>> shapes = new ArrayList<ArrayList<Coordinate>>();
		/*****exclude three*******/
		//exclude 4, 7, and 2
		if(mid14 && mid48 && mid34 && mid37 && mid78 && mid67 && mid12 && mid23 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint12);
			shape3.add(midpoint23);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 8, 3, and 1
		else if(mid14 && mid15 && mid12 && mid37 && mid34 && mid23 && mid78 && mid48 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint78);
			shape3.add(midpoint48);
			shape3.add(midpoint58);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 4, 7, and 5
		else if(mid14 && mid48 && mid34 && mid37 && mid78 && mid67 && mid58 && mid56 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint15);
			shape3.add(midpoint58);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 8, 3, and 6
		else if(mid78 && mid48 && mid58 && mid37 && mid34 && mid23 && mid67 && mid56 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint48);
			shape1.add(midpoint58);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint67);
			shape3.add(midpoint56);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 8, 1, and 6
		else if(mid78 && mid48 && mid58 && mid14 && mid15 && mid12 && mid67 && mid56 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint48);
			shape1.add(midpoint58);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint12);
			shape2.add(midpoint15);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint67);
			shape3.add(midpoint56);
			shape3.add(midpoint26);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 2, 7, and 5
		else if(mid12 && mid23 && mid26 && mid37 && mid78 && mid67 && mid58 && mid56 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint15);
			shape3.add(midpoint58);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 6, 3, and 1
		else if(mid56 && mid67 && mid26 && mid37 && mid34 && mid23 && mid12 && mid14 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint12);
			shape3.add(midpoint14);
			shape3.add(midpoint15);
			shapes.add(shape3);
			return shapes;
		}
		//exclude 4, 2, and 5
		else if(mid14 && mid48 && mid34 && mid12 && mid26 && mid23 && mid58 && mid56 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint12);
			shape2.add(midpoint23);
			shape2.add(midpoint26);
			shapes.add(shape2);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint15);
			shape3.add(midpoint58);
			shapes.add(shape3);
			return shapes;
		}
		System.out.println("missing cases in nine case");
		return shapes;
	}

	public void twelveCase(){
		//revisit
	}

	public ArrayList<ArrayList<Coordinate>> fiveCase(){
		ArrayList<ArrayList<Coordinate>> shapes = new ArrayList<ArrayList<Coordinate>>();
		/******* trapezoids *******/
		//face 1234 facing foward
		//vertex1 lower left
		if(mid15 && mid58 && mid67 && mid23 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint58);
			shape2.add(midpoint23);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//vertex2 lower left
		else if(mid14 && mid48 && mid78 && mid56 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//vertex3 lower left
		else if(mid14 && mid58 && mid67 && mid34 && mid37){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//vertex4 lower left
		else if(mid34 && mid78 && mid56 && mid23 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//face2367 in front
		//vertex2 lower left
		else if(mid14 && mid67 && mid23 && mid15 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint14);
			shapes.add(shape2);
			return shapes;
		}
		//vertex6 lower left
		else if(mid34 && mid12 && mid56 && mid48 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint12);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shape2.add(midpoint48);
			shapes.add(shape2);
			return shapes;
		}
		//vertex7 lower left
		else if(mid14 && mid58 && mid23 && mid78 && mid37){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint23);
			shapes.add(shape2);
			return shapes;
		}
		//vertex3 lower left
		else if(mid34 && mid78 && mid12 && mid67 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//face 5678 in front
		//vertex6 lower left
		else if(mid14 && mid58 && mid23 && mid56 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint56);
			shape2.add(midpoint26);
			shape2.add(midpoint23);
			shapes.add(shape2);
			return shapes;
		}
		//vertex5 lower left
		else if(mid12 && mid34 && mid78 && mid15 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint12);
			shape2.add(midpoint15);
			shape2.add(midpoint58);
			shape2.add(midpoint78);
			shapes.add(shape2);
			return shapes;
		}
		//vertex8 lower left
		else if(mid14 && mid67 && mid23 && mid48 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//vertex7 lower left
		else if(mid34 && mid12 && mid56 && mid37 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint12);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint67);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//face1584 in front
		//vertex5 lower left
		else if(mid14 && mid58 && mid67 && mid12 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint12);
			shape2.add(midpoint26);
			shape2.add(midpoint67);
			shapes.add(shape2);
			return shapes;
		}
		//vertex1 lower left
		else if(mid34 && mid78 && mid56 && mid15 && mid14){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint34);
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint14);
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shapes.add(shape2);
			return shapes;
		}
		//vertex4 lower left
		else if(mid58 && mid67 && mid23 && mid34 && mid48){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint67);
			shape1.add(midpoint23);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shape2.add(midpoint23);
			shape2.add(midpoint34);
			shapes.add(shape2);
			return shapes;
		}
		//vertex8 lower left
		else if(mid78 && mid56 && mid12 && mid37 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint78);
			shape1.add(midpoint56);
			shape1.add(midpoint12);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint37);
			shape2.add(midpoint23);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		//face3487 in front
		//vertex4 lower left
		else if(mid48 && mid37 && mid26 && mid58 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shapes.add(shape2);
			return shapes;
		}
		//vertex3 lower left
		else if(mid15 && mid26 && mid37 && mid78 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shapes.add(shape2);
			return shapes;
		}
		//vertex7 lower left
		else if(mid48 && mid15 && mid26 && mid78 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint15);
			shape1.add(midpoint26);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shapes.add(shape2);
			return shapes;
		}
		//vertex8 lower left
		else if(mid15 && mid48 && mid37 && mid56 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint37);
			shapes.add(shape2);
			return shapes;
		}
		//face1265 in front
		//vertex5 lower left
		else if(mid15 && mid26 && mid37 && mid14 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint15);
			shape1.add(midpoint37);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint15);
			shapes.add(shape2);
			return shapes;
		}
		//vertex6 lower left
		else if(mid48 && mid15 && mid26 && mid34 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint26);
			shape1.add(midpoint15);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shape2.add(midpoint26);
			shapes.add(shape2);
			return shapes;
		}
		//vertex2 lower left
		else if(mid15 && mid48 && mid37 && mid12 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint15);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint12);
			shape2.add(midpoint23);
			shape2.add(midpoint37);
			shapes.add(shape2);
			return shapes;
		}
		//vertex1 lower left
		else if(mid48 && mid26 && mid37 && mid14 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint26);
			shape1.add(midpoint48);
			shapes.add(shape1);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint26);
			shape2.add(midpoint12);
			shapes.add(shape2);
			return shapes;
		}
		System.out.println("missing cases in five case");
		return shapes;
	}

	public ArrayList<ArrayList<Coordinate>> sixCase(){
		ArrayList<ArrayList<Coordinate>> shapes = new ArrayList<ArrayList<Coordinate>>();
		/*******two opposite triangles*******/
		//1 and 7
		if(mid12 && mid14 && mid15 && mid78 && mid67 && mid37){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}
		//5 and 3
		else if(mid56 && mid58 && mid15 && mid34 && mid23 && mid37){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint58);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}
		//6 and 4
		else if(mid48 && mid34 && mid14 && mid26 && mid56 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}
		//2 and 8
		else if(mid48 && mid78 && mid58 && mid12 && mid23 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		/****** diagonal triangles *******/
		//1 and 3
		if(mid12 && mid14 && mid15 && mid34 && mid37 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint34);
			shape2.add(midpoint23);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//1 and 6
		if(mid12 && mid14 && mid15 && mid26 && mid56 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//1 and 8
		if(mid12 && mid14 && mid15 && mid78 && mid48 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//2 and 4
		if(mid12 && mid23 && mid26 && mid14 && mid48 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint14);
			shape2.add(midpoint48);
			shape2.add(midpoint34);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//2 and 7
		if(mid12 && mid23 && mid26 && mid37 && mid67 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//2 and 5
		if(mid12 && mid23 && mid26 && mid56 && mid15 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//3 and 8
		if(mid34 && mid37 && mid23 && mid78 && mid48 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint34);
			shape1.add(midpoint23);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//3 and 6
		if(mid34 && mid37 && mid23 && mid26 && mid56 && mid67){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint34);
			shape1.add(midpoint23);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//4 and 7
		if(mid14 && mid48 && mid34 && mid37 && mid67 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//4 and 5
		if(mid14 && mid48 && mid34 && mid56 && mid15 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//5 and 7
		if(mid56 && mid15 && mid58 && mid37 && mid67 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint58);
			shape1.add(midpoint15);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint78);
			shape2.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		//6 and 8
		if(mid26 && mid56 && mid67 && mid78 && mid48 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint56);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shapes.add(shape1);
			shapes.add(shape2);
			return shapes;
		}

		/****** hexagon *****/
		if(mid14 && mid48 && mid78 && mid67 && mid26 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint48);
			shape1.add(midpoint78);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			shape1.add(midpoint12);
			shapes.add(shape1);
			return shapes;
		}
		else if(mid15 && mid58 && mid78 && mid37 && mid23 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint58);
			shape1.add(midpoint78);
			shape1.add(midpoint37);
			shape1.add(midpoint23);
			shape1.add(midpoint12);
			shapes.add(shape1);
			return shapes;
		}
		else if(mid14 && mid34 && mid37 && mid67 && mid56 && mid15){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint14);
			shape1.add(midpoint34);
			shape1.add(midpoint37);
			shape1.add(midpoint67);
			shape1.add(midpoint56);
			shapes.add(shape1);
			return shapes;
		}
		else if(mid48 && mid34 && mid23 && mid26 && mid56 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			shape1.add(midpoint23);
			shape1.add(midpoint26);
			shape1.add(midpoint56);
			shapes.add(shape1);
			return shapes;
		}
		else if(mid15 && mid58 && mid78 && mid37 && mid23 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint78);
			shape1.add(midpoint37);
			shape1.add(midpoint23);
			shape1.add(midpoint12);
			shape1.add(midpoint15);
			shapes.add(shape1);
			return shapes;
		}
		else if(mid14 && mid48 && mid78 && mid67 && mid26 && mid12){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint78);
			shape1.add(midpoint67);
			shape1.add(midpoint26);
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shapes.add(shape1);
			return shapes;
		}

		/****** quad *****/
		else if(mid15 && mid12 && mid48 && mid78 &&  mid67 && mid23){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint12);
			shape1.add(midpoint48);
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint67);
			shape2.add(midpoint12);
			shape2.add(midpoint23);
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint78);
			shape3.add(midpoint67);
			shape3.add(midpoint12);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid14 && mid23 && mid15 && mid56 &&  mid78 && mid37){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint14);
			shape1.add(midpoint23);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint15);
			shape2.add(midpoint56);
			shape2.add(midpoint78);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint15);
			shape3.add(midpoint78);
			shape3.add(midpoint37);
			shape3.add(midpoint23);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid14 && mid58 && mid56 && mid26 &&  mid37 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint14);
			shape1.add(midpoint23);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint26);
			shape2.add(midpoint56);
			shape2.add(midpoint37);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint14);
			shape3.add(midpoint34);
			shape3.add(midpoint37);
			shape3.add(midpoint56);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid37 && mid12 && mid26 && mid58 &&  mid67 && mid48){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint37);
			shape1.add(midpoint12);
			shape1.add(midpoint26);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint26);
			shape2.add(midpoint58);
			shape2.add(midpoint67);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint34);
			shape3.add(midpoint26);
			shape3.add(midpoint58);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid14 && mid34 && mid58 && mid37 &&  mid26 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint34);
			shape1.add(midpoint58);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint34);
			shape2.add(midpoint37);
			shape2.add(midpoint26);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint34);
			shape3.add(midpoint58);
			shape3.add(midpoint56);
			shape3.add(midpoint26);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid12 && mid48 && mid34 && mid58 &&  mid67 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint34);
			shape1.add(midpoint48);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint48);
			shape2.add(midpoint58);
			shape2.add(midpoint67);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint26);
			shape3.add(midpoint12);
			shape3.add(midpoint48);
			shape3.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}

		/******* triangles ******/
		else if(mid14 && mid58 && mid12 && mid78 && mid37 && mid26){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint58);
			shape1.add(midpoint12);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint37);
			shape2.add(midpoint26);
			shape2.add(midpoint12);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint58);
			shape3.add(midpoint12);
			shape3.add(midpoint37);
			shape3.add(midpoint78);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid15 && mid34 && mid12 && mid37 && mid67 && mid58){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint15);
			shape1.add(midpoint34);
			shape1.add(midpoint12);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint58);
			shape2.add(midpoint15);
			shape2.add(midpoint67);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint34);
			shape3.add(midpoint15);
			shape3.add(midpoint37);
			shape3.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid48 && mid15 && mid56 && mid23 && mid67 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint15);
			shape1.add(midpoint56);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint23);
			shape2.add(midpoint67);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint34);
			shape3.add(midpoint23);
			shape3.add(midpoint56);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid14 && mid48 && mid78 && mid26 && mid23 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint23);
			shape1.add(midpoint26);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint78);
			shape2.add(midpoint56);
			shape2.add(midpoint26);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint48);
			shape3.add(midpoint78);
			shape3.add(midpoint26);
			shape3.add(midpoint14);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid48 && mid34 && mid15 && mid67 && mid23 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint34);
			shape1.add(midpoint15);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint67);
			shape2.add(midpoint23);
			shape2.add(midpoint34);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint67);
			shape3.add(midpoint34);
			shape3.add(midpoint15);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		else if(mid48 && mid15 && mid56 && mid67 && mid23 && mid34){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint56);
			shape1.add(midpoint15);

			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			shape2.add(midpoint56);
			shape2.add(midpoint67);
			shape2.add(midpoint23);

			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			shape3.add(midpoint56);
			shape3.add(midpoint23);
			shape3.add(midpoint34);
			shape3.add(midpoint48);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			return shapes;
		}
		//begin originally missed cases
		else if(mid14 && mid15 && mid26 && mid34 && mid67 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape4 = new ArrayList<Coordinate>();
			shape1.add(midpoint14);
			shape1.add(midpoint34);
			shape1.add(midpoint23);
			shape2.add(midpoint23);
			shape2.add(midpoint67);
			shape2.add(midpoint26);
			shape3.add(midpoint14);
			shape3.add(midpoint15);
			shape3.add(midpoint26);
			shape3.add(midpoint23);
			shape4.add(midpoint34);
			shape4.add(midpoint78);
			shape4.add(midpoint67);
			shape4.add(midpoint23);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			shapes.add(shape4);
			return shapes;
		}
		else if(mid15 && mid23 && mid26 && mid34 && mid58 && mid78){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape4 = new ArrayList<Coordinate>();
			shape1.add(midpoint58);
			shape1.add(midpoint78);
			shape1.add(midpoint34);
			shape2.add(midpoint58);
			shape2.add(midpoint34);
			shape2.add(midpoint15);
			shape3.add(midpoint15);
			shape3.add(midpoint34);
			shape3.add(midpoint23);
			shape4.add(midpoint15);
			shape4.add(midpoint23);
			shape4.add(midpoint26);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			shapes.add(shape4);
			return shapes;
		}
		else if(mid12 && mid23 && mid37 && mid48 && mid58 && mid56){
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape4 = new ArrayList<Coordinate>();
			shape1.add(midpoint48);
			shape1.add(midpoint58);
			shape1.add(midpoint37);
			shape2.add(midpoint58);
			shape2.add(midpoint37);
			shape2.add(midpoint56);
			shape3.add(midpoint56);
			shape3.add(midpoint12);
			shape3.add(midpoint37);
			shape4.add(midpoint23);
			shape4.add(midpoint12);
			shape4.add(midpoint37);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			shapes.add(shape4);
			return shapes;
		}
		else if(mid12 && mid14 && mid37 && mid48 && mid56 && mid67){
//			System.out.println("originally missed case at " + vertex1.x + " " + vertex1.y + " " + vertex1.z);
//			screen.indicators.add(new Coordinates(vertex1.x, vertex1.y, vertex1.z));
			ArrayList<Coordinate> shape1 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape2 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape3 = new ArrayList<Coordinate>();
			ArrayList<Coordinate> shape4 = new ArrayList<Coordinate>();
			shape1.add(midpoint12);
			shape1.add(midpoint14);
			shape1.add(midpoint56);
			shape2.add(midpoint14);
			shape2.add(midpoint56);
			shape2.add(midpoint48);
			shape3.add(midpoint48);
			shape3.add(midpoint37);
			shape3.add(midpoint56);
			shape4.add(midpoint56);
			shape4.add(midpoint37);
			shape4.add(midpoint67);
			shapes.add(shape1);
			shapes.add(shape2);
			shapes.add(shape3);
			shapes.add(shape4);
			return shapes;
		}
		System.out.println("missing cases in six case");
		System.out.println("\tmid12: " + mid12);
		System.out.println("\tmid14: " + mid14);
		System.out.println("\tmid15: " + mid15);
		System.out.println("\tmid23: " + mid23);
		System.out.println("\tmid26: " + mid26);
		System.out.println("\tmid34: " + mid34);
		System.out.println("\tmid37: " + mid37);
		System.out.println("\tmid48: " + mid48);
		System.out.println("\tmid56: " + mid56);
		System.out.println("\tmid58: " + mid58);
		System.out.println("\tmid67: " + mid67);
		System.out.println("\tmid78: " + mid78);
		return shapes;
	}
}
