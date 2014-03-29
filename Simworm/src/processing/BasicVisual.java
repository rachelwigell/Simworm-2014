package processing;

import java.util.HashMap;

import controlP5.*;
import dataStructures.ColorMode;
import dataStructures.Shell;
import processing.core.*;
import peasy.*;

public class BasicVisual extends PApplet{	
	Shell displayShell;
	String userText = "Type a cell name";
	PeasyCam camera;
	PMatrix matScene;
	ControlP5 info;
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
	RadioButton chooseColorMode;
	boolean lineageState = true;
	boolean fateState = false;
	boolean parsState = false;
	Button fateKey0;
	Button fateKey1;
	Button fateKey2;
	Button fateKey3;
	Button parsKey0;
	Button parsKey1;
	Button parsKey2;
	Button parsKey3;
	Button parsKey4;
	Button parsKey5;
	Button lineageKey0;
	Button lineageKey1;
	Button lineageKey2;
	Button lineageKey3;
	Button lineageKey4;
	Button lineageKey5;
	Button lineageKey6;
	
	//called on start. opens the screen where the user chooses mutant genes
	public void setup(){
		size(1400, 800, P3D);
		info = new ControlP5(this);
		info.setAutoDraw(false);
		
		Textarea choose = new Textarea(info, "boxeslabel").setPosition(600, 300).setText("CHOOSE MUTANTS"); //just a label
		choose.setFont(new ControlFont(createFont("arial", 16)));
		chooseMutants = new CheckBox(info, "boxes");
		chooseMutants.setItemsPerRow(3)
		.setSpacingColumn(50)
		.setSpacingRow(20)
		.setPosition(600, 335)
		.addItem("par-1", 0)
		.addItem("par-2", 1)
		.addItem("par-3", 2)
		.addItem("par-4", 3)
		.addItem("par-5", 4)
		.addItem("par-6", 5)
		.addItem("pkc-3", 6)
		.setSize(20, 20)
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseMutants.getItems()){ //setting the font size on the checkbox options has to be done kind of indirectly
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		}
		createShell = new Button(info, "createShell").setPosition(600, 450).setLabel("create shell").setSize(130, 20); //button to confirm checkbox settings
		createShell.captionLabel().setControlFont(new ControlFont(createFont("arial", 16)));
		
		camera = new PeasyCam(this, 500, 400, 0, 600); //initialize the peasycam		
	}
	
	//called after mutants are confirmed, to set up the main simulation screen
	public void secondarySetup(){
		info.remove("boxeslabel"); //remove the elements that were used in the mutants choosing screen
		info.remove("boxes");
		info.remove("createShell");
		mutantsChosen = true; //value used to choose what items are drawn in draw()
		displayShell = new Shell(this, mutants); //create the shell!
		
		userTextArea = new Textarea(info, "infoText"); //textarea where the user can input commands and receive information
		userTextArea.setPosition(1100, 20)
		.setSize(280, 500).
		setFont(createFont("arial", 14));
		
		//initialize buttons that control camera to choose orthogonal views
		frontB = new Button(info, "front").setPosition(1100, 700).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));;
		frontB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		backB = new Button(info, "back").setPosition(1100, 730).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));
		backB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		topB = new Button(info, "top").setPosition(1200, 700).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));
		topB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		bottomB = new Button(info, "bottom").setPosition(1200, 730).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));
		bottomB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		leftB = new Button(info, "left").setPosition(1300, 700).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));
		leftB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		rightB = new Button(info, "right").setPosition(1300, 730).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231));
		rightB.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		
		//initialize the color keys; these are not interactive
		fateKey0 = new Button(info, "germline").setPosition(1100, 570).setColorBackground(color(102, 194, 165)).setColorActive(color(102, 194, 165)).setColorForeground(color(102, 194, 165)).setVisible(false);
		fateKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		fateKey1 = new Button(info, "MS/E").setPosition(1100, 600).setColorBackground(color(252, 141, 98)).setColorActive(color(252, 141, 98)).setColorForeground(color(252, 141, 98)).setVisible(false);
		fateKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		fateKey2 = new Button(info, "C/D").setPosition(1200, 570).setColorBackground(color(141, 160, 203)).setColorActive(color(141, 160, 203)).setColorForeground(color(141, 160, 203)).setVisible(false);
		fateKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		fateKey3 = new Button(info, "Default").setPosition(1200, 600).setColorBackground(color(231, 138, 195)).setColorActive(color(231, 138, 195)).setColorForeground(color(231, 138, 195)).setVisible(false);
		fateKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		
		parsKey0 = new Button(info, "par-1").setPosition(1100, 570).setColorBackground(color(255, 0, 255)).setColorActive(color(255, 0, 255)).setColorForeground(color(255, 0, 255)).setVisible(false);
		parsKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		parsKey1 = new Button(info, "par-2").setPosition(1100, 600).setColorBackground(color(255, 0, 0)).setColorActive(color(255, 0, 0)).setColorForeground(color(255, 0, 0)).setVisible(false);
		parsKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		parsKey2 = new Button(info, "par-3").setPosition(1200, 570).setColorBackground(color(0, 255, 255)).setColorActive(color(0, 255, 255)).setColorForeground(color(0, 255, 255)).setVisible(false);
		parsKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		parsKey3 = new Button(info, "par-4").setPosition(1200, 600).setColorBackground(color(0, 0, 255)).setColorActive(color(0, 0, 255)).setColorForeground(color(0, 0, 255)).setVisible(false);
		parsKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		parsKey4 = new Button(info, "par-5").setPosition(1300, 570).setColorBackground(color(255, 255, 0)).setColorActive(color(255, 255, 0)).setColorForeground(color(255, 255, 0)).setVisible(false);
		parsKey4.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		parsKey5 = new Button(info, "par-6").setPosition(1300, 600).setColorBackground(color(0, 255, 0)).setColorActive(color(0, 255, 0)).setColorForeground(color(0, 255, 0)).setVisible(false);
		parsKey5.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		
		lineageKey0 = new Button(info, "ab-a").setPosition(1100, 570).setColorBackground(color(255, 0, 0)).setColorActive(color(255, 0, 0)).setColorForeground(color(255, 0, 0)).setVisible(true);
		lineageKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey1 = new Button(info, "ab-p").setPosition(1100, 600).setColorBackground(color(0, 0, 255)).setColorActive(color(0, 0, 255)).setColorForeground(color(0, 0, 255)).setVisible(true);
		lineageKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey2 = new Button(info, "e, ab").setPosition(1200, 570).setColorBackground(color(0, 255, 0)).setColorActive(color(0, 255, 0)).setColorForeground(color(0, 255, 0)).setVisible(true);
		lineageKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey3 = new Button(info, "ms").setPosition(1200, 600).setColorBackground(color(0, 255, 255)).setColorActive(color(0, 255, 255)).setColorForeground(color(0, 255, 255)).setVisible(true);
		lineageKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey4 = new Button(info, "c").setPosition(1300, 570).setColorBackground(color(255, 0, 255)).setColorActive(color(255, 0, 255)).setColorForeground(color(255, 0, 255)).setVisible(true);
		lineageKey4.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey5 = new Button(info, "d").setPosition(1300, 600).setColorBackground(color(200, 100, 200)).setColorActive(color(200, 100, 200)).setColorForeground(color(200, 100, 200)).setVisible(true);
		lineageKey5.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		lineageKey6 = new Button(info, "p").setPosition(1100, 630).setColorBackground(color(255, 255, 0)).setColorActive(color(255, 255, 0)).setColorForeground(color(255, 255, 0)).setVisible(true);		
		lineageKey6.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		
		new Button(info, "color mode") //just a label for the radio buttons, not interactive
		.setPosition(1095, 665)
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		chooseColorMode = new RadioButton(info, "radios"); //radio buttons for choosing the color mode
		chooseColorMode.setItemsPerRow(3) //lots of aesthetic settings
		.setSpacingColumn(58)
		.setSpacingRow(20)
		.setPosition(1188, 665)
		.addItem("Lineage", 0)
		.addItem("Fate", 1)
		.addItem("Pars", 2)
		.activate(0)
		.setNoneSelectedAllowed(false)
		.setSize(16, 16)
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseColorMode.getItems()){
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", 12)));
		}
		
		drawKey(displayShell.colorMode); //draw the appropriate key (set to lineage by default)
	}

	//implicitly called in a loop
	public void draw(){
		background(0);
		translate(250, 250, -150);
		lights();
		spotLight(255, 255, 255, 250, 0, 400, 0, 0, -1, PI/4, 2);
		noStroke();
		//some of these settings should only be called if we're past the mutants screen
		//else these elements won't exist yet and you'll get null point exceptions
		//boolean mutantsChosen will get set when we exit that screen
		if(mutantsChosen){
			drawAxes(); //draw the coordinate axes
			displayShell.drawAllCells(); //draw the shell
			userTextArea.setText(userText); //show the text output that userText is currently set to
			//boolean values for lineageState, fateState, parsState exist so that we don't have to continuously set the color
			//they hold the current state of the color mode (that is, false for those that aren't currently set and true for the one that is)
			//we only set colors when the colormode doesn't match these, which indicates that the colormode has been recently changed
			if(chooseColorMode.getState(0) != lineageState || chooseColorMode.getState(1) != fateState || chooseColorMode.getState(2) != parsState) updateColorMode();
		}

		//this prevents clicks on the infoView (right hand side) from affecting the camera
		if(mouseX > 1100) camera.setActive(false);
		else camera.setActive(true);
		
		gui();
	}
	
	//action listener for the create shell button on the choose mutants screen. finalizes the mutants choice and calls secondarySetup
	//secondary setup will draw the main simulation screen
	void createShell(float theValue){
		//read states of the checkboxes and set these values in the shell
		mutants.put("par-1", chooseMutants.getState(0));
		mutants.put("par-2", chooseMutants.getState(1));
		mutants.put("par-3", chooseMutants.getState(2));
		mutants.put("par-4", chooseMutants.getState(3));
		mutants.put("par-5", chooseMutants.getState(4));
		mutants.put("par-6", chooseMutants.getState(5));
		mutants.put("pkc-3", chooseMutants.getState(6));
		secondarySetup();
	}
	
	//action listeners for the orthogonal button views; set the camera position
	void front(float theValue){
		camera.reset(0);
	}
	
	void back(float theValue){
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
	
	//detects changes in the selected value of the colormode radio buttons
	//this only gets called if the new selected value doesn't match the boolean switches for lineageState, fateState, parsState
	public void updateColorMode(){
		if(chooseColorMode.getState(0)){ //if lineage button is selected
			displayShell.colorMode = ColorMode.LINEAGE; //set colormode to lineage
			drawKey(ColorMode.LINEAGE); //draw the lineage color key
			lineageState = true; //reset the boolean switches
			fateState = false;
			parsState = false;
		}
		//equivalent cases for fate, pars
		else if(chooseColorMode.getState(1)){
			displayShell.colorMode = ColorMode.FATE;
			drawKey(ColorMode.FATE);
			lineageState = false;
			fateState = true;
			parsState = false;
		}
		else if (chooseColorMode.getState(2)){
			displayShell.colorMode = ColorMode.PARS;
			drawKey(ColorMode.PARS);
			lineageState = false;
			fateState = false;
			parsState = true;
		}
		displayShell.updateColorMode(); //calls the method that will change the colors of the existing cells
	}
	
	//when the user is typing, different things should happen
	public void keyReleased(){
		if(mutantsChosen){ //only run if mutantsChosen is set, because userText doesn't exist yet if not
			if(key == ' '){ //spacebar triggers a timestep
				displayShell.timeStep();
			}
			else if(keyCode == BACKSPACE){ //manually implement backspace
				if (userText.length() > 0) {
					userText = userText.substring(0, userText.length()-1);
				}
			}
			else if(keyCode == ENTER){ //enter finalizes a command
				if(!displayShell.getCells().containsKey(userText)){ //if the userText isn't currently an existing cell name, print that the cell doesn't exist
					userText = "Cell not present";
				}
				else{ //if it is a cell name, print the genes list for the cell
					userText = displayShell.getCells().get(userText).getInfo();
				}
			}
			else if(((int) keyCode) >= 33 && ((int) keyCode) <= 126){ //if it's any other ("normal" ascii) key, just add that letter to the userText
				if (userText.length() > 10) userText = ""; //clear it if it gets to more than 10 characters so the user never has to delete a bunch of text; no useful commands are over 10 char anyway
				userText = userText + key;
			}
		}
	}
	
	//display the correct color key for the currently selected colormode
	public void drawKey(ColorMode colorMode){
		switch(colorMode){		
		case FATE:
			fateKey0.setVisible(true);
			fateKey1.setVisible(true);
			fateKey2.setVisible(true);
			fateKey3.setVisible(true);
			parsKey0.setVisible(false);
			parsKey1.setVisible(false);
			parsKey2.setVisible(false);
			parsKey3.setVisible(false);
			parsKey4.setVisible(false);
			parsKey5.setVisible(false);
			lineageKey0.setVisible(false);
			lineageKey1.setVisible(false);
			lineageKey2.setVisible(false);
			lineageKey3.setVisible(false);
			lineageKey4.setVisible(false);
			lineageKey5.setVisible(false);
			lineageKey6.setVisible(false);
			break;
		case PARS:
			fateKey0.setVisible(false);
			fateKey1.setVisible(false);
			fateKey2.setVisible(false);
			fateKey3.setVisible(false);
			parsKey0.setVisible(true);
			parsKey1.setVisible(true);
			parsKey2.setVisible(true);
			parsKey3.setVisible(true);
			parsKey4.setVisible(true);
			parsKey5.setVisible(true);
			lineageKey0.setVisible(false);
			lineageKey1.setVisible(false);
			lineageKey2.setVisible(false);
			lineageKey3.setVisible(false);
			lineageKey4.setVisible(false);
			lineageKey5.setVisible(false);
			lineageKey6.setVisible(false);
			break;
		case LINEAGE:
			fateKey0.setVisible(false);
			fateKey1.setVisible(false);
			fateKey2.setVisible(false);
			fateKey3.setVisible(false);
			parsKey0.setVisible(false);
			parsKey1.setVisible(false);
			parsKey2.setVisible(false);
			parsKey3.setVisible(false);
			parsKey4.setVisible(false);
			parsKey5.setVisible(false);
			lineageKey0.setVisible(true);
			lineageKey1.setVisible(true);
			lineageKey2.setVisible(true);
			lineageKey3.setVisible(true);
			lineageKey4.setVisible(true);
			lineageKey5.setVisible(true);
			lineageKey6.setVisible(true);
			break;
		}
		
	}
	
	//draw the coordinate axes
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
