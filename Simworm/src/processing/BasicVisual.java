package processing;

import dataStructures.Cell;
import dataStructures.Shell;
import processing.core.*;

public class BasicVisual extends PApplet{
	int value1 = width/2;
	int value2 = height/2;
	Shell displayShell;
	public PGraphics displayView;
	public PGraphics infoView;
	int w = 700;
	int h = 800;
	String userText = "Type a cell name";
	
	public float recentX = 8000;
	public float recentY = 8000;

	public void setup() {
		size(1400, 800, P3D);
		displayView = createGraphics(w, h, P3D);
		infoView = createGraphics(w, h, P2D);
		displayShell = new Shell(this);
	}

	public void draw() {
		displayView.beginDraw();
		displayView.background(0);
		displayView.camera(value1, value2, (h/2) / tan(PI/6), w/2, h/2, 0, 0, 1, 0);
		displayView.translate(w/2-250, h/2-150, -150);
		displayView.noStroke();		
		displayView.fill(180, 255, 255);
		displayView.lights();
		displayView.spotLight(180, 255, 255, w/2, h/2, 400, 0, 0, -1, PI/4, 2);
		displayShell.drawAllCells();
		displayView.endDraw();
		
		infoView.beginDraw();
		infoView.background(0);
		infoView.fill(255);
		infoView.text(userText, w/2, 100);
		infoView.endDraw();
		
		image(displayView, 0, 0);
		image(infoView, w, 0);
	}
	
	public void mouseDragged(){
		value1 = pmouseX;
		value2 = pmouseY;
	}
	
	public void mouseClicked(){
		this.recentX = mouseX;
		this.recentY = mouseY;
		//System.out.println("x is " + this.recentX + " and y is " + this.recentY);
	}
	
	public void keyReleased(){
		if(key == ' '){
			displayShell.timeStep();
		}
		else if(keyCode == BACKSPACE){
			if (userText.length() > 0) {
			      userText = userText.substring(0, userText.length()-1);
			}
		}
		else if(keyCode == ENTER){
			if(!displayShell.getCells().containsKey(userText)){
				userText = "Cell not present";
			}
			else{
				userText = displayShell.getCells().get(userText).getInfo();
			}
		}
		else{
			if (userText.length() > 15) userText = "";
			userText = userText + key;
		}
	}
	
	public static void main(String args[]) {
		   PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 
}
