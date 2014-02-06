package processing;

import processing.core.*;

public class BasicVisual extends PApplet{

	public void setup() {
		size(800, 600, P3D);
	}

	public void draw() {
		background(0);
		camera(mouseX, mouseY, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
		translate(width/2, height/2, -100);
		stroke(255);
		noFill();
		box(200);
	}
	
	public static void main(String args[]) {
		   PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 
}
