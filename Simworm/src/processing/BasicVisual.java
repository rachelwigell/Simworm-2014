package processing;

import dataStructures.Coordinates;
import dataStructures.Cell;
import picking.BoundingBox3D;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import controlP5.*;
import dataStructures.ColorMode;
import dataStructures.Shell;
import processing.core.*;
import peasy.*;

public class BasicVisual extends PApplet{	
	private static final long serialVersionUID = 1L;
	Shell displayShell;
	Shell farthestShell;
	String userText;
	PeasyCam camera;
	ControlP5 info;
	Textarea userTextArea;
	Textarea cellNamesArea;
	Button frontB;
	Button backB;
	Button topB;
	Button bottomB;
	Button leftB;
	Button rightB;
	boolean mutantsChosen;
	HashMap<String, Boolean> mutants;
	CheckBox chooseMutants;
	Button createShell;
	RadioButton chooseColorMode;
	RadioButton chooseTimeflowMode;
	boolean autoTime;
	boolean lineageState;
	boolean fateState;
	boolean parsState;
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
	int currentTime;
	int farthestSeen;
	HashMap<Integer, Shell> shellsOverTime;
	int timeCount;	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	float width = screenSize.width;
	float height = screenSize.height;
	Slider progressBar;
	Button startOver;
	boolean firstTime = true;
	boolean firstClick = false;
	
	//called on start. opens the screen where the user chooses mutant genes
	@SuppressWarnings("deprecation")
	public void setup(){
		mutantsChosen = false;
		mutants = new HashMap<String, Boolean>();
		if(firstTime){
			size((int) width, (int) height, P3D);
			camera = new PeasyCam(this, 500, height/2, 0, width/2.65); //initialize the peasycam
		}
		firstTime = false;
		info = new ControlP5(this);
		info.setAutoDraw(false);
		
		Button choose = new Button(info, "Choose mutants").setPosition(width/3, height/3).setSize((int) (width/3), (int) (height/20))
		.setColorBackground(color(2, 52, 76))
		.setColorForeground(color(2, 52, 76))
		.setColorActive(color(2, 52, 76)); //just a label
		choose.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/80))));
		chooseMutants = new CheckBox(info, "boxes");
		chooseMutants.setItemsPerRow(3)
		.setSpacingColumn((int) (width/9))
		.setSpacingRow((int) (height/30))
		.setPosition(width/3, (float) (height/2.45))
		.addItem("par-1", 0)
		.addItem("par-2", 1)
		.addItem("par-3", 2)
		.addItem("par-4", 3)
		.addItem("par-5", 4)
		.addItem("par-6", 5)
		.addItem("pkc-3", 6)
		.setSize((int) (width/60), (int) (height/30))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseMutants.getItems()){ //setting the font size on the checkbox options has to be done kind of indirectly
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}
		createShell = new Button(info, "createShell").setPosition(width/3, (float) (height/1.65)).setLabel("create shell").setSize((int) (width/3), (int) (height/20)); //button to confirm checkbox settings
		createShell.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/80))));
		
		currentTime = 1;
		farthestSeen = 1;
	}
	
	//called after mutants are confirmed, to set up the main simulation screen
	@SuppressWarnings("deprecation")
	public void secondarySetup(){
		info.remove("Choose mutants"); //remove the elements that were used in the mutants choosing screen
		info.remove("boxes");
		info.remove("createShell");
		farthestShell = new Shell(this, mutants); //create the shell!
		displayShell = farthestShell; //at the start, should display farthestShell.
		shellsOverTime = new HashMap<Integer, Shell>();
		shellsOverTime.put(1, new Shell(farthestShell)); //populate the first member of the hashmap
		timeCount = 0;
		userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
		autoTime = false;
		lineageState = true;
		fateState = false;
		parsState = false;
		
		userTextArea = new Textarea(info, "infoText"); //textarea where the user can input commands and receive information
		userTextArea.setPosition((float) (width/1.25), height/40)
		.setSize((int) (width/5), (int) (height/4)).
		setFont(createFont("arial", (width/114)));
		cellNamesArea = new Textarea(info, "namesText"); //textarea where the names of the currently present cells are displayed
		cellNamesArea.setPosition((float) (width/1.25), (float) (3*height/10))
		.setSize((int) (width/5), (int) (height/5)).
		setFont(createFont("arial", (width/114)));
		
		new Button(info, "cell color key") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 13*height/30))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		//initialize buttons that control camera to choose orthogonal views
		frontB = new Button(info, "front").setPosition((float) (width/1.25), (float) (height - 5*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		frontB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		backB = new Button(info, "back").setPosition((float) (width/1.25), (float) (height - 3*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		backB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		topB = new Button(info, "top").setPosition((float) (width - 2*width/15), (float) (height - 5*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		topB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		bottomB = new Button(info, "bottom").setPosition((float) (width - 2*width/15), (float) (height - 3*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		bottomB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		leftB = new Button(info, "left").setPosition((float) (width - width/15), (float) (height - 5*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		leftB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		rightB = new Button(info, "right").setPosition((float) (width - width/15), (float) (height - 3*height/60)).setColorBackground(color(49, 130, 189))
				.setColorForeground(color(107, 174, 214))
				.setColorActive(color(189, 215, 231)).setSize((int) (width/20), (int) (height/40));
		rightB.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		new Button(info, "Camera views") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 7*height/60))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		
		//initialize the color keys; these are not interactive
		fateKey0 = new Button(info, "germline").setPosition((float) (width/1.25), (float) (height - 12*height/30)).setColorBackground(color(102, 194, 165)).setColorActive(color(102, 194, 165)).setColorForeground(color(102, 194, 165)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		fateKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		fateKey1 = new Button(info, "MS/E").setPosition((float) (width - 2*width/15), (float) (height - 12*height/30)).setColorBackground(color(252, 141, 98)).setColorActive(color(252, 141, 98)).setColorForeground(color(252, 141, 98)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		fateKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		fateKey2 = new Button(info, "C/D").setPosition((float) (width - width/15), (float) (height - 12*height/30)).setColorBackground(color(141, 160, 203)).setColorActive(color(141, 160, 203)).setColorForeground(color(141, 160, 203)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		fateKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		fateKey3 = new Button(info, "Default").setPosition((float) (width/1.25), (float) (height - 11*height/30)).setColorBackground(color(231, 138, 195)).setColorActive(color(231, 138, 195)).setColorForeground(color(231, 138, 195)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		fateKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		
		parsKey0 = new Button(info, "par-1").setPosition((float) (width/1.25), (float) (height - 12*height/30)).setColorBackground(color(255, 0, 255)).setColorActive(color(255, 0, 255)).setColorForeground(color(255, 0, 255)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		parsKey1 = new Button(info, "par-2").setPosition((float) (width/1.25), (float) (height - 11*height/30)).setColorBackground(color(255, 0, 0)).setColorActive(color(255, 0, 0)).setColorForeground(color(255, 0, 0)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		parsKey2 = new Button(info, "par-3").setPosition((float) (width - 2*width/15), (float) (height - 12*height/30)).setColorBackground(color(0, 255, 255)).setColorActive(color(0, 255, 255)).setColorForeground(color(0, 255, 255)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		parsKey3 = new Button(info, "par-4").setPosition((float) (width - 2*width/15), (float) (height - 11*height/30)).setColorBackground(color(0, 0, 255)).setColorActive(color(0, 0, 255)).setColorForeground(color(0, 0, 255)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		parsKey4 = new Button(info, "par-5").setPosition((float) (width - width/15), (float) (height - 12*height/30)).setColorBackground(color(255, 255, 0)).setColorActive(color(255, 255, 0)).setColorForeground(color(255, 255, 0)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey4.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		parsKey5 = new Button(info, "par-6").setPosition((float) (width - width/15), (float) (height - 11*height/30)).setColorBackground(color(0, 255, 0)).setColorActive(color(0, 255, 0)).setColorForeground(color(0, 255, 0)).setVisible(false).setSize((int) (width/20), (int) (height/40));
		parsKey5.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		
		lineageKey0 = new Button(info, "ab-a").setPosition((float) (width/1.25), (float) (height - 12*height/30)).setColorBackground(color(255, 0, 0)).setColorActive(color(255, 0, 0)).setColorForeground(color(255, 0, 0)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey0.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey1 = new Button(info, "ab-p").setPosition((float) (width/1.25), (float) (height - 11*height/30)).setColorBackground(color(0, 0, 255)).setColorActive(color(0, 0, 255)).setColorForeground(color(0, 0, 255)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey1.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey2 = new Button(info, "e, ab").setPosition((float) (width - 2*width/15), (float) (height - 12*height/30)).setColorBackground(color(0, 255, 0)).setColorActive(color(0, 255, 0)).setColorForeground(color(0, 255, 0)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey2.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey3 = new Button(info, "ms").setPosition((float) (width - 2*width/15), (float) (height - 11*height/30)).setColorBackground(color(0, 255, 255)).setColorActive(color(0, 255, 255)).setColorForeground(color(0, 255, 255)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey3.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey4 = new Button(info, "c").setPosition((float) (width - width/15), (float) (height - 12*height/30)).setColorBackground(color(255, 0, 255)).setColorActive(color(255, 0, 255)).setColorForeground(color(255, 0, 255)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey4.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey5 = new Button(info, "d").setPosition((float) (width - width/15), (float) (height - 11*height/30)).setColorBackground(color(200, 70, 150)).setColorActive(color(200, 70, 150)).setColorForeground(color(200, 70, 150)).setVisible(true).setSize((int) (width/20), (int) (height/40));
		lineageKey5.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		lineageKey6 = new Button(info, "p").setPosition((float) (width/1.25), (float) (height - 10*height/30)).setColorBackground(color(255, 255, 0)).setColorActive(color(255, 255, 0)).setColorForeground(color(255, 255, 0)).setVisible(true).setSize((int) (width/20), (int) (height/40));		
		lineageKey6.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/133))));
		
		new Button(info, "set time flow mode") //just a label for the radio buttons, not interactive
		.setPosition((float) (width/1.26), (float) (height - 6*height/30))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		chooseTimeflowMode = new RadioButton(info, "timeflow"); //radio buttons for choosing the color mode
		chooseTimeflowMode.setItemsPerRow(3) //lots of aesthetic settings
		.setSpacingColumn((int) (width/18))
		.setPosition((float) (width/1.25), (float) (height - 5*height/30))
		.addItem("Manual", 0)
		.addItem("Automatic", 1)
		.activate(0)
		.setNoneSelectedAllowed(false)
		.setSize((int) (width/75), (int) (height/40))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseTimeflowMode.getItems()){
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}
		
		
		new Button(info, "choose color mode") //just a label for the radio buttons, not interactive
		.setPosition((float) (width/1.26), (float) (height - 17*height/60))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		chooseColorMode = new RadioButton(info, "radios"); //radio buttons for choosing the color mode
		chooseColorMode.setItemsPerRow(3) //lots of aesthetic settings
		.setSpacingColumn((int) (width/18))
		.setPosition((float) (width/1.25), (float) (height - 15*height/60))
		.addItem("Lineage", 0)
		.addItem("Fate", 1)
		.addItem("Pars", 2)
		.activate(0)
		.setNoneSelectedAllowed(false)
		.setSize((int) (width/75), (int) (height/40))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseColorMode.getItems()){
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}
		
		startOver = new Button(info, "startOver")
		.setLabel("  generate new embryo")
		.setPosition((float) (width/60), (float) (height/40))
		.setSize((int) (width/6), (int) (height/20));
		startOver.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		drawKey(displayShell.colorMode); //draw the appropriate key (set to lineage by default)

		//set up the slider bar
		progressBar = new Slider(info, "Elapsed time")
		.setPosition(width/40, height - height/20)
		.setSize((int) (width-width/3), (int) (height/40))
		.setValue(currentTime)
		.setLock(true)
		.setMax(96);
		progressBar.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		mutantsChosen = true; //value used to choose what items are drawn in draw()
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
			cellNamesArea.setText(displayShell.getCellNames());
			//boolean values for lineageState, fateState, parsState exist so that we don't have to continuously set the color
			//they hold the current state of the color mode (that is, false for those that aren't currently set and true for the one that is)
			//we only set colors when the colormode doesn't match these, which indicates that the colormode has been recently changed
			if(chooseColorMode.getState(0) != lineageState || chooseColorMode.getState(1) != fateState || chooseColorMode.getState(2) != parsState) updateColorMode();
			if(chooseTimeflowMode.getState(1) != autoTime) autoTime = !autoTime;
			if(autoTime){
				if(timeCount >= 10){
					progressForward();
					timeCount = 0;
				}
				else{
					timeCount++;
				}
			}
		}

		//this prevents clicks on the infoView (right hand side) from affecting the camera
		if(mouseX > 4 * (width/5)) camera.setActive(false);
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
		camera.rotateY(160);
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
	
	void startOver(float theValue){
		camera.reset(0);
		setup();
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
	
	public void progressForward(){
		if(currentTime <= 95){
			userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
			for(String s: displayShell.getCells().keySet()){
				displayShell.getCells().get(s).setSelected(true);
			}
			if(currentTime == farthestSeen){
				farthestShell.timeStep();
				displayShell = farthestShell;
				currentTime++;
				farthestSeen++;
				shellsOverTime.put(currentTime, new Shell(farthestShell));
				updateColorMode();
				progressBar.setValue(currentTime);
			}
			else{
				currentTime++;
				displayShell = shellsOverTime.get(currentTime);
				updateColorMode();
				progressBar.setValue(currentTime);
			}
		}
	}
	
	//when the user is typing, different things should happen
	public void keyReleased(){
		if(keyCode == ESC) exit();
		
		if(mutantsChosen){ //only run if mutantsChosen is set, because userText doesn't exist yet if not
			//if(key == ' '){ //spacebar triggers a timestep
				//displayShell.timeStep();
				//userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
			//}
			if(keyCode == RIGHT){
				progressForward();
			}
			else if(keyCode == LEFT){
				if(currentTime > 1){
					for(String s: displayShell.getCells().keySet()){
						displayShell.getCells().get(s).setSelected(true);
					}
					userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
					currentTime--;
					displayShell = shellsOverTime.get(currentTime);
					updateColorMode();
					progressBar.setValue(currentTime);
				}
				
			}
			else if(keyCode == BACKSPACE){ //manually implement backspace
				if (userText.length() > 0) {
					userText = userText.substring(0, userText.length()-1);
				}
			}
			else if(keyCode == ENTER || keyCode == RETURN){ //enter finalizes a command
				if(!displayShell.getCells().containsKey(userText)){ //if the userText isn't currently an existing cell name, print that the cell doesn't exist
					userText = "Cell not present";
				}
				else{ //if it is a cell name, print the genes list for the cell
					for(String s: displayShell.getCells().keySet()){
						if(!s.equals(userText)) displayShell.getCells().get(s).setSelected(false);
						else displayShell.getCells().get(s).setSelected(true);
					}
					userText = displayShell.getCells().get(userText).getInfo();
				}
			}
			else if(((int) key) >= 33 && ((int) key) <= 126){ //if it's any other ("normal" ascii) key, just add that letter to the userText
				if (userText.length() > 10) userText = ""; //clear it if it gets to more than 10 characters so the user never has to delete a bunch of text; no useful commands are over 10 char anyway
				userText = userText + key;
			}
		}
	}
	
	public void mouseClicked(){
		if(mutantsChosen){
			if(!firstClick){
				firstClick = true;
			}
			else{
				if(mouseX < 4*(width/5)){
					loadPixels();
					if(pixels[(int) (mouseY*width+mouseX)] != -16777216){
						System.out.println(pixels[(int) (mouseY*width+mouseX)]);
						LinkedList<Cell> qualifyingCells = new LinkedList<Cell>();
						for(String s: displayShell.getCells().keySet()){
							Cell c = displayShell.getCells().get(s);
							if(pointInBounds(mouseX, mouseY, c)){
								qualifyingCells.add(c);
							}
							c.setSelected(false);
						}
						if(qualifyingCells.size() > 0){
							Cell chosen = qualifyingCells.getFirst();
							if(qualifyingCells.size() > 1){
								float distance = screenZ(chosen.getCenter().getX(), chosen.getCenter().getY(), chosen.getCenter().getZ());
								for(Cell c: qualifyingCells){
									float dist = screenZ(c.getCenter().getX(), c.getCenter().getY(), c.getCenter().getZ());
									if(dist < distance){
										chosen = c;
										distance = dist;
									}
								}
							}
							chosen.setSelected(true);
							userText = chosen.getInfo();
						}
					}
				}
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

	public float areaTriangle(Coordinates point1, Coordinates point2, Coordinates point3){
		return abs((float) ((point1.getX()*(point2.getY()-point3.getY()) + point2.getX()*(point3.getY()-point1.getY()) + point3.getX()*(point1.getY()-point2.getY()))/2.0));
	}
	
	public boolean pointInBounds(int x, int y, Cell s){
		s.boundSphere();
		LinkedList<Coordinates> screenCoor = new LinkedList<Coordinates>();
		for(Coordinates t: s.getBoundingBox()){
			screenCoor.add(new Coordinates(screenX(t.getX(), t.getY(), t.getZ()), screenY(t.getX(), t.getY(), t.getZ()), 0));
		}
		
		float areaPoly = 0;
		float areaPoint = 0;
		
		for(int i = 1; i < (screenCoor.size()-1); i++){
			areaPoly += areaTriangle(screenCoor.getFirst(), screenCoor.get(i), screenCoor.get(i+1));
		}
		
		
		Coordinates mouse = new Coordinates(x, y, 0);
		for(int i = 0; i < (screenCoor.size()-1); i++){
			areaPoint += areaTriangle(mouse, screenCoor.get(i), screenCoor.get(i+1));
		}
		areaPoint += areaTriangle(mouse, screenCoor.getLast(), screenCoor.getFirst());
		
		if(areaPoint > areaPoly+.2) return false;
		else return true;
	}
	
	public Coordinates convertToScreen(Coordinates model){
		return new Coordinates(screenX(model.getX(), model.getY(), model.getZ()), screenY(model.getX(), model.getY(), model.getZ()), 0);
	}
	
	public LinkedList<Coordinates> selectMaxPoints(BoundingBox3D boundingBox){
		LinkedList<Coordinates> maxes = new LinkedList<Coordinates>();
		
		Coordinates lbb = convertToScreen(boundingBox.leftbottomback);
		Coordinates lbf = convertToScreen(boundingBox.leftbottomfront);
		Coordinates ltb = convertToScreen(boundingBox.lefttopback);
		Coordinates ltf = convertToScreen(boundingBox.lefttopfront);
		Coordinates rbb = convertToScreen(boundingBox.rightbottomback);
		Coordinates rbf = convertToScreen(boundingBox.rightbottomfront);
		Coordinates rtb = convertToScreen(boundingBox.righttopback);
		Coordinates rtf = convertToScreen(boundingBox.righttopfront);
		
		if(!lbb.sum360(rbb, ltb, lbf)) maxes.add(boundingBox.leftbottomback);
		if(!lbf.sum360(rbf, ltf, lbb)) maxes.add(boundingBox.leftbottomfront);
		if(!ltb.sum360(rtb, lbb, ltf)) maxes.add(boundingBox.lefttopback);
		if(!ltf.sum360(rtf, lbf, ltb)) maxes.add(boundingBox.lefttopfront);
		if(!rbb.sum360(lbb, rtb, rbf)) maxes.add(boundingBox.rightbottomback);
		if(!rbf.sum360(lbf, rtf, rbb)) maxes.add(boundingBox.rightbottomfront);
		if(!rtb.sum360(ltb, rbb, rtf)) maxes.add(boundingBox.righttopback);
		if(!rtf.sum360(ltf, rbf, rtb)) maxes.add(boundingBox.righttopfront);
		
		return maxes;
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
