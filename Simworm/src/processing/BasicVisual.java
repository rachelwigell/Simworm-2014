package processing;

import dataStructures.Shell;
import processing.core.*;

public class BasicVisual extends PApplet{
	int value1 = width/2;
	int value2 = height/2;
	Shell displayShell;

	public void setup() {
		size(1000, 600, P3D);
		displayShell = new Shell(this);
	}

	public void draw() {
		background(0);
		camera(value1, value2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
		translate(width/2-250, height/2-150, -250);
		noStroke();		
		fill(180, 255, 255);
		lights();
		spotLight(180, 255, 255, width/2, height/2, 400, 0, 0, -1, PI/4, 2);
		displayShell.drawAllCells();
	}
	
	public void mouseDragged(){
		value1 = pmouseX;
		value2 = pmouseY;
	}
	
	public void keyReleased(){
		displayShell.timeStep();
	}
	
	public static void main(String args[]) {
		   PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 
}
