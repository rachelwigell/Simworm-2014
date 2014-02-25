package processing;

import dataStructures.Shell;
import processing.core.*;
import peasy.*;

public class BasicVisual extends PApplet{	
	Shell displayShell;
	public PGraphics displayView;
	public PGraphics infoView;
	public static final int w = 1000; //width of display view
	public static final int h = 800; //height of display view
	int value1 = w/2;
	int value2 = h/2;	
	String userText = "Type a cell name";
	PeasyCam camera;
	PMatrix matScene;

	public void setup(){
		size(1400, 800, P3D);
		displayView = createGraphics(w, h, P3D);
		infoView = createGraphics(width-w, h, P2D);
		displayShell = new Shell(this);
		matScene = getMatrix();
		//camera = new PeasyCam(this, w/2, h/2, 0, 600);
	}

	public void draw(){
		displayView.beginDraw();
			displayView.background(0);
			displayView.setMatrix(getMatrix());
			displayView.camera(value1, value2, (h/2) / tan(PI/6), w/2, h/2, 0, 0, 1, 0);
			displayView.translate(w/2-250, h/2-150, -150);
			displayView.lights();
			displayView.spotLight(180, 255, 255, w/2-250, h/2-150, 400, 0, 0, -1, PI/4, 2);
			drawAxes();
			displayView.noStroke();
			displayShell.drawAllCells();
		displayView.endDraw();
		
		infoView.beginDraw();
			infoView.background(50);
			infoView.fill(255);
			infoView.text(userText, (width-w)/3, 50);
			drawButtons();
			drawKey();
		infoView.endDraw();
		
		setMatrix(matScene);
		
		image(displayView, 0, 0);
		image(infoView, w, 0);
	}
	
	public void mouseDragged(){
		if(mouseX < w){
			value1 = mouseX;
			value2 = mouseY;
		}
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
			if (userText.length() > 10) userText = "";
			userText = userText + key;
		}
	}
	
	public void drawAxes(){
		displayView.strokeWeight(5);
		//set up to draw x axis
		displayView.stroke(255,0,0);
		displayView.fill(255,0,0);
		displayView.line(-200, 400, 0, 0, 400, 0);
		displayView.text("a", -200, 390, 0);
		displayView.text("p", 0, 390, 0);
		//set up to draw y axis
		displayView.stroke(0,255,0);
		displayView.fill(0,255,0);
		displayView.text("v", -100, 510, 0);
		displayView.text("d", -100, 290, 0);
		displayView.line(-100, 500, 0, -100, 300, 0);
		//set up to draw z axis
		displayView.stroke(0,0,255);
		displayView.fill(0,0,255);
		displayView.line(-100, 400, -100, -100, 400, 100);
		displayView.text("r", -90, 400, -100);
		displayView.text("l", -110, 400, 100);
		//set up to draw cells
		displayView.noStroke();
		displayView.fill(180, 255, 255);
	}
	
	public void drawButtons(){
		infoView.fill(180, 180, 180);
		infoView.rect(20, 650, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("front", 30, 675);
		
		infoView.fill(180, 180, 180);
		infoView.rect(20, 710, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("back", 30, 735);
		
		infoView.fill(180, 180, 180);
		infoView.rect(140, 650, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("top", 150, 675);
		
		infoView.fill(180, 180, 180);
		infoView.rect(140, 710, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("bottom", 150, 735);
		
		infoView.fill(180, 180, 180);
		infoView.rect(260, 650, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("left", 270, 675);

		infoView.fill(180, 180, 180);
		infoView.rect(260, 710, 100, 50);
		infoView.fill(0,0,0);
		infoView.text("right", 270, 735);		
	}
	
	public void mouseClicked(){		
		if(mouseX >= w+20 && mouseX <= w+120 && mouseY >= 650 && mouseY <= 700){
			//over anterior button
			value1 = w/2;
			value2 = h/2;
//			camera.reset(0);
		}
		else if(mouseX >= w+20 && mouseX <= w+120 && mouseY >= 710 && mouseY <= 760){
			//over posterior
//			camera.reset(0);
//			camera.rotateX(180);
		}
		else if(mouseX >= w+140 && mouseX <= w+240 && mouseY >= 650 && mouseY <= 700){
			//over dorsal
//			camera.reset(0);
//			camera.rotateY(90);
		}
		else if(mouseX >= w+140 && mouseX <= w+240 && mouseY >= 710 && mouseY <= 760){
			//over ventral
//			camera.reset(0);
//			camera.rotateY(270);
		}
		else if(mouseX >= w+260 && mouseX <= w+360 && mouseY >= 650 && mouseY <= 700){
			//over left
//			camera.reset(0);
//			camera.rotateZ(90)
		}
		else if(mouseX >= w+260 && mouseX <= w+360 && mouseY >= 710 && mouseY <= 760){
			//over right
//			camera.reset(0);
//			camera.rotateZ(270);
		}
	}
	
	public void drawKey(){
		infoView.fill(255, 0, 255);
		infoView.ellipse(40, 590, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-1", 60, 590);
		
		infoView.fill(255, 0, 0);
		infoView.ellipse(160, 590, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-2", 180, 590);
		
		infoView.fill(0, 255, 255);
		infoView.ellipse(280, 590, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-3", 300, 590);
		
		infoView.fill(0, 0, 255);
		infoView.ellipse(40, 620, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-4", 60, 620);
		
		infoView.fill(255, 255, 0);
		infoView.ellipse(160, 620, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-5", 180, 620);
		
		infoView.fill(0, 255, 0);
		infoView.ellipse(280, 620, 20, 20);
		infoView.fill(255, 255, 255);
		infoView.text("par-6", 300, 620);
	}
	
	public static void main(String args[]){
		   PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 
	
}
