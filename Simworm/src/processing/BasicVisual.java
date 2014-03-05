package processing;

import java.awt.TextField;
import java.util.HashMap;

import controlP5.*;
import dataStructures.Shell;
import processing.core.*;
import peasy.*;

public class BasicVisual extends PApplet{	
	Shell displayShell;
	String userText = "Type a cell name";
	PeasyCam camera;
	PMatrix matScene;
	ControlP5 info;
	//Slider mutationRate;
	Textarea userTextArea;
	Button frontB;
	Button backB;
	Button topB;
	Button bottomB;
	Button leftB;
	Button rightB;
	boolean mutantsChosen = false;
	HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
	CheckBox chooseMutants;
	Button createShell;
	
	public void setup(){
		size(1400, 800, P3D);
		info = new ControlP5(this);
		info.setAutoDraw(false);
		
		new Textarea(info, "boxeslabel").setPosition(600, 450).setText("CHOOSE MUTANTS");
		chooseMutants = new CheckBox(info, "boxes");
		chooseMutants.setItemsPerRow(3)
		.setSpacingColumn(30)
		.setSpacingRow(20)
		.setPosition(600, 500)
		.addItem("par-1", 0)
		.addItem("par-2", 1)
		.addItem("par-3", 2)
		.addItem("par-4", 3)
		.addItem("par-5", 4)
		.addItem("par-6", 5)
		.addItem("pkc-3", 6);
		createShell = new Button(info, "createShell").setPosition(600, 600).setLabel("create shell");
		
		camera = new PeasyCam(this, 500, 400, 0, 600);
		
		//mutationRate = new Slider(info, "mutation rate");
		//mutationRate.setRange(0, 100).setPosition(1100, 650).setSize(200, 25);		
	}
	
	public void secondarySetup(){
		info.remove("boxeslabel");
		info.remove("boxes");
		info.remove("createShell");
		mutantsChosen = true;
		displayShell = new Shell(this, mutants);
		userTextArea = new Textarea(info, "infoText");
		userTextArea.setPosition(1200, 20).setSize(200, 600);
		
		frontB = new Button(info, "front").setPosition(1100, 700);
		backB = new Button(info, "back").setPosition(1100, 730);
		topB = new Button(info, "top").setPosition(1200, 700);
		bottomB = new Button(info, "bottom").setPosition(1200, 730);
		leftB = new Button(info, "left").setPosition(1300, 700);
		rightB = new Button(info, "right").setPosition(1300, 730);
		
		//new Button(info, "par-1").setPosition(1100, 570).setColorBackground(color(255, 0, 255)).setColorActive(color(255, 0, 255)).setColorForeground(color(255, 0, 255));
		//new Button(info, "par-2").setPosition(1100, 600).setColorBackground(color(255, 0, 0)).setColorActive(color(255, 0, 0)).setColorForeground(color(255, 0, 0));
		//new Button(info, "par-3").setPosition(1200, 570).setColorBackground(color(0, 255, 255)).setColorActive(color(0, 255, 255)).setColorForeground(color(0, 255, 255));
		//new Button(info, "par-4").setPosition(1200, 600).setColorBackground(color(0, 0, 255)).setColorActive(color(0, 0, 255)).setColorForeground(color(0, 0, 255));
		//new Button(info, "par-5").setPosition(1300, 570).setColorBackground(color(255, 255, 0)).setColorActive(color(255, 255, 0)).setColorForeground(color(255, 255, 0));
		//new Button(info, "par-6").setPosition(1300, 600).setColorBackground(color(0, 255, 0)).setColorActive(color(0, 255, 0)).setColorForeground(color(0, 255, 0));
	}

	public void draw(){
		background(0);
		translate(250, 250, -150);
		lights();
		spotLight(180, 255, 255, 250, 250, 400, 0, 0, -1, PI/4, 2);
		noStroke();
		if(mutantsChosen){
			drawAxes();
			displayShell.drawAllCells();
			userTextArea.setText(userText);
		}
		
		//displayShell.mutationProb = (float) (mutationRate.getValue()/100.0);

		if(mouseX > 1100) camera.setActive(false);
		else camera.setActive(true);

		gui();
	}
	
	void createShell(float theValue){
		//read states of the checkboxes
		mutants.put("par-1", chooseMutants.getState(0));
		mutants.put("par-2", chooseMutants.getState(1));
		mutants.put("par-3", chooseMutants.getState(2));
		mutants.put("par-4", chooseMutants.getState(3));
		mutants.put("par-5", chooseMutants.getState(4));
		mutants.put("par-6", chooseMutants.getState(5));
		mutants.put("pkc-3", chooseMutants.getState(6));
		secondarySetup();
	}
	
	void front(float theValue){
		camera.reset(0);
	}
	
	void back(float theValue){
		//still off
		camera.reset(0);
		camera.rotateY(180);
	}
	
	void top(float theValue){
		camera.reset(0);
		camera.rotateX(90);
	}
	
	void bottom(float theValue){
		camera.reset(0);
		camera.rotateX(-90);
	}
	
	void left(float theValue){
		camera.reset(0);
		camera.rotateY(-90);
	}
	
	void right(float theValue){
		camera.reset(0);
		camera.rotateY(90);
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
		strokeWeight(5);
		//set up to draw x axis
		stroke(255,0,0);
		fill(255,0,0);
		line(-200, 400, 0, 0, 400, 0);
		text("a", -200, 390, 0);
		text("p", 0, 390, 0);
		//set up to draw y axis
		stroke(0,255,0);
		fill(0,255,0);
		text("v", -100, 510, 0);
		text("d", -100, 290, 0);
		line(-100, 500, 0, -100, 300, 0);
		//set up to draw z axis
		stroke(0,0,255);
		fill(0,0,255);
		line(-100, 400, -100, -100, 400, 100);
		text("r", -90, 400, -100);
		text("l", -110, 400, 100);
		//set up to draw cells
		noStroke();
		fill(180, 255, 255);
	}
	
	public void gui(){
		  hint(DISABLE_DEPTH_TEST);
		  camera.beginHUD();
		  info.draw();
		  camera.endHUD();
		  hint(ENABLE_DEPTH_TEST);
		}
	
	public static void main(String args[]){
		   PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 
	
}
