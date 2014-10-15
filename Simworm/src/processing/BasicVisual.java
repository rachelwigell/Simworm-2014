package processing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;

import peasy.PeasyCam;
import processing.core.PApplet;
import controlP5.Button;
import controlP5.CheckBox;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Textarea;
import controlP5.Toggle;
import dataStructures.Cell;
import dataStructures.ColorMode;
import dataStructures.ConsequentsList;
import dataStructures.Coordinate;
import dataStructures.RGB;
import dataStructures.Shell;

public class BasicVisual extends PApplet{	
	private static final long serialVersionUID = 1L;
	Shell displayShell;
	Shell farthestShell;
	public ConsequentsList conslist = new ConsequentsList();
	String userText;
	PeasyCam camera;
	ControlP5 info;
	Textarea userTextArea;
//	Textarea cellNamesArea;
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
	HashMap<Integer, Integer> numCellsAtTime;
	int timeCount;	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int width = screenSize.width;
	int height = screenSize.height;
	Slider progressBar;
	Button startOver;
	RadioButton showAnimations;
	Slider chooseGridSize;
	RadioButton showShell;

	boolean firstTime = true;
	boolean firstClick = false;
	public RGB currentColor;
	boolean moving = false;

	//marching cubes fields
	int gridSize = 12;
	float threshold = 16.0f;
	boolean[][][] field;
	private ArrayList<ArrayList<Coordinate>> vertices;
	private ArrayList<RGB> displayColorField;
	private ArrayList<RGB> uniqueColorField;

	/**
	 * called on start. opens the screen where the user chooses mutant genes
	 */
	@SuppressWarnings("deprecation")
	public void setup(){
		mutantsChosen = false;
		firstClick = false;
		mutants = new HashMap<String, Boolean>();
		if(firstTime){
			size(width, height, P3D);
			camera = new PeasyCam(this, 600); //initialize the peasycam
		}
		firstTime = false;
		info = new ControlP5(this);
		info.setAutoDraw(false);

		//sets up the mutant selection menu
		Button choose = new Button(info, "Choose mutants").setPosition(width/3, height/3).setSize((width/3), (height/20))
				.setColorBackground(color(2, 52, 76))
				.setColorForeground(color(2, 52, 76))
				.setColorActive(color(2, 52, 76)); //just a label
		choose.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/80))));
		chooseMutants = new CheckBox(info, "boxes");
		chooseMutants.setItemsPerRow(3)
		.setSpacingColumn((width/9))
		.setSpacingRow((height/30))
		.setPosition(width/3, (float) (height/2.45))
		.addItem("par-1", 0)
		.addItem("par-2", 1)
		.addItem("par-3", 2)
		.addItem("par-4", 3)
		.addItem("par-5", 4)
		.addItem("par-6", 5)
		.addItem("pkc-3", 6)
		.setSize((width/60), (height/30))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: chooseMutants.getItems()){ //setting the font size on the checkbox options has to be done kind of indirectly
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}
		createShell = new Button(info, "createShell").setPosition(width/3, (float) (height/1.65)).setLabel("create shell").setSize((width/3), (height/20)); //button to confirm checkbox settings
		createShell.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/80))));

		currentTime = 1;
		farthestSeen = 1;
	}

	/**
	 * called after mutants are confirmed, to set up the main simulation screen
	 */
	@SuppressWarnings("deprecation")
	public void secondarySetup(){
		info.remove("Choose mutants"); //remove the elements that were used in the mutants choosing screen
		info.remove("boxes");
		info.remove("createShell");
		farthestShell = new Shell(this, mutants); //create the shell!
		displayShell = farthestShell; //at the start, should display farthestShell.
		shellsOverTime = new HashMap<Integer, Shell>();
		numCellsAtTime = new HashMap<Integer, Integer>();
		shellsOverTime.put(1, new Shell(farthestShell)); //populate the first member of the hashmap
		numCellsAtTime.put(1, 1);
		timeCount = 0;
		iterateThroughGrid();

		userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
		lineageState = true;
		fateState = false;
		parsState = false;

		//textarea where the user can input commands and receive information
		userTextArea = new Textarea(info, "infoText");
		userTextArea.setPosition((float) (width/1.25), height/40)
		.setSize((width/5), (int) (height/3.5))
		.setFont(createFont("arial", (width/114)));
		
//		cellNamesArea = new Textarea(info, "namesText"); //textarea where the names of the currently present cells are displayed
//		cellNamesArea.setPosition((float) (width/1.25), (float) (5*height/20))
//		.setSize((int) (width/5), (int) (height/8)).
//		setFont(createFont("arial", (width/114)));

		//label for the color key
		new Button(info, "cell color key") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 13*height/30))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		//label for the option buttons to turn shell depiction on/off
		new Button(info, "draw shell") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 41*height/60))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		//buttons for choosing to display shell
		showShell = new RadioButton(info, "showShell");
		showShell.setItemsPerRow(3)
		.setSpacingColumn((int) (width/18))
		.setPosition((float) (width/1.25), (float) (height - 39*height/60))
		.addItem("On ", 0)
		.addItem("Off ", 1)
		.activate(1)
		.setNoneSelectedAllowed(false)
		.setSize((int) (width/75), (int) (height/40))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: showShell.getItems()){
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}

		//label for the option buttons to turn animations on/off
		new Button(info, "show animations") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 9*height/15))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));

		//buttons for choosing to show animations
		showAnimations = new RadioButton(info, "animations");
		showAnimations.setItemsPerRow(3)
		.setSpacingColumn((int) (width/18))
		.setPosition((float) (width/1.25), (float) (height - 17*height/30))
		.addItem("On", 0)
		.addItem("Off", 1)
		.activate(0)
		.setNoneSelectedAllowed(false)
		.setSize((int) (width/75), (int) (height/40))
		.setColorBackground(color(49, 130, 189))
		.setColorForeground(color(107, 174, 214))
		.setColorActive(color(189, 215, 231));
		for(Toggle t: showAnimations.getItems()){
			t.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		}

		//label for the slider bar for image granularity
		new Button(info, "image granularity") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 31*height/60))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));

		//slider bar for choosing image granularity
		chooseGridSize = new Slider(info, "")
		.setPosition((float) (width/1.25), (float) (height - 29*height/60))
		.setSize((int) (23*width/125), (int) (height/40))
		.setLock(false)
		.setNumberOfTickMarks(19)
		.snapToTickMarks(true)
		.showTickMarks(false)
		.setMin(4)
		.setMax(22)
		.setValue(gridSize);
		chooseGridSize.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));

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

		//label for the buttons that choose time flow mode
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

		//label for the buttons to choose color mode
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

		//the button that lets you start over with a new shell
		startOver = new Button(info, "startOver")
		.setLabel("  generate new embryo")
		.setPosition((float) (width/60), (float) (height/40))
		.setSize((int) (width/6), (int) (height/20));
		startOver.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));

		//draw the appropriate key (set to lineage by default)
		drawKey(displayShell.colorMode);

		//set up the slider bar that depicts how far into the animation we are
		progressBar = new Slider(info, "Elapsed time")
		.setPosition(width/40, height - height/20)
		.setSize((int) (width-width/3), (int) (height/40))
		.setValue(currentTime)
		.setLock(true)
		.setMax(96);
		progressBar.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/100))));
		mutantsChosen = true; //value used to choose what items are drawn in draw()
		//set this back to true to return to start menu
	}

	/**
	 * draws the semi-transparent shell if that option is turned on
	 */
	public void drawShell(){
		if(showShell.getState(0)){
			pushMatrix();
			scale(1.0f, 3.0f/5.0f, 3.0f/5.0f);
			noStroke();
			fill(255, 100);
			sphere(260);
			popMatrix();
		}
	}

	/**
	 * implicitly called in a loop, handles the main actions of the program
	 */
	public void draw(){
		background(0);
		lights();
		noStroke();
		
		//some of these settings should only be called if we're past the mutants screen
		//else these elements won't exist yet and you'll get null pointer exceptions
		//boolean mutantsChosen will get set to false when we exit that screen
		if(mutantsChosen){
			drawAxes(); //draw the coordinate axes
			printVertices(false); //draw the shell using metaballs
			drawShell();
			userTextArea.setText(userText); //show the text output that userText is currently set to
			//boolean values for lineageState, fateState, parsState exist so that we don't have to continuously set the color
			//they hold the current state of the color mode (that is, false for those that aren't currently set and true for the one that is)
			//we only set colors when the colormode doesn't match these, which indicates that the colormode has been recently changed
			if(chooseColorMode.getState(0) != lineageState || chooseColorMode.getState(1) != fateState || chooseColorMode.getState(2) != parsState){
				updateColorMode();
			}
			//if an animation is occurring, we need to continuously update the cell locations until it has ended
			if(moving){
				boolean equilibrium = false;
				equilibrium = moveAllMetaballs();
				iterateThroughGrid();
				if(equilibrium) moving = false;
			}
			//if we're in automatic time flow mode, call progressForward periodically
			if(chooseTimeflowMode.getState(1)){
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

	/**
	 * Gets the total metaball influence at a given point
	 * @param point the point we're considering
	 * @return metaball field value
	 */
	public float netChargeHere(Coordinate point, Metaball metaball){
		float total = 0;
		total -= ellipsoidContribution(point) * 800;
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			if(m.getCenter().samePoint(metaball.getCenter())){
				total += m.fourthPowerChargeFrom(point);
			}
			else{
				total -= m.fourthPowerChargeFrom(point);
			}
		}
		return total;
	}

	/**
	 * gets the total metaball influence, ignoring one metaball
	 * necessary when calculating inter-cellular forces, as a cell doesn't exert force on itself.
	 * @param point the point at which we want to calculate the force field
	 * @param metaball the metaball we're ignoring
	 * @return the metaball field strength
	 */
	public float netChargeMinusThis(Coordinate point, Metaball metaball){
		float total = 0;
		total += ellipsoidContribution(point) * 800;
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			if(!(m.getCenter().samePoint(metaball.getCenter()))){
				total += m.fourthPowerChargeFrom(point);
			}
		}
		return total;
	}

	/**
	 * A metaball looks at all its surroundings and moves in a direction that reduces the forces acting upon it
	 * @param m the metaball
	 * @param unit granularity of movement; i.e. put 1 to move 1 unit each time, higher numbers for coarser granularity
	 * @return an int representing which direction, if any, it moved. Used to test for equilibrium
	 * (if it returns 0, it chose not to move and is in equilibrium)
	 */
	public int considerAllSides(Metaball m, int unit){
		float lowest = netChargeMinusThis(m.getCenter(), m);
		Coordinate location = m.getCenter();

		int directionMoved = 0;

		Coordinate above = new Coordinate(m.getCenter().getX(), m.getCenter().getY()-unit, m.getCenter().getZ());
		if(inShellEllipsoid(above)){
			float aboveVal = netChargeMinusThis(above, m);
			if(aboveVal < lowest){
				lowest = aboveVal;
				location = above;
				directionMoved = 2;
			}
		}
		Coordinate below = new Coordinate(m.getCenter().getX(), m.getCenter().getY()+unit, m.getCenter().getZ());
		if(inShellEllipsoid(below)){
			float belowVal = netChargeMinusThis(below, m);
			if(belowVal < lowest){
				lowest = belowVal;
				location = below;
				directionMoved = 1;
			}
		}
		Coordinate left = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY(), m.getCenter().getZ());
		if(inShellEllipsoid(left)){
			float leftVal = netChargeMinusThis(left, m);
			if(leftVal < lowest){
				lowest = leftVal;
				location = left;
				directionMoved = 4;
			}
		}
		Coordinate right = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY(), m.getCenter().getZ());
		if(inShellEllipsoid(right)){
			float rightVal = netChargeMinusThis(right, m);
			if(rightVal < lowest){
				lowest = rightVal;
				location = right;
				directionMoved = 3;
			}
		}
		Coordinate away = new Coordinate(m.getCenter().getX(), m.getCenter().getY(), m.getCenter().getZ()-unit);
		if(inShellEllipsoid(away)){
			float awayVal = netChargeMinusThis(away, m);
			if(awayVal < lowest){
				lowest = awayVal;
				location = away;
				directionMoved = 6;
			}
		}
		Coordinate towards = new Coordinate(m.getCenter().getX(), m.getCenter().getY(), m.getCenter().getZ()+unit);
		if(inShellEllipsoid(towards)){
			float towardsVal = netChargeMinusThis(towards, m);
			if(towardsVal < lowest){
				lowest = towardsVal;
				location = towards;
				directionMoved = 5;
			}
		}
		Coordinate topLeft = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()-unit, m.getCenter().getZ());
		if(inShellEllipsoid(topLeft)){
			float topLeftVal = netChargeMinusThis(topLeft, m);
			if(topLeftVal < lowest){
				lowest = topLeftVal;
				location = topLeft;
				directionMoved = 11;
			}
		}
		Coordinate topRight = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()-unit, m.getCenter().getZ());
		if(inShellEllipsoid(topRight)){
			float topRightVal = netChargeMinusThis(topRight, m);
			if(topRightVal < lowest){
				lowest = topRightVal;
				location = topRight;
				directionMoved = 12;
			}
		}
		Coordinate topBack = new Coordinate(m.getCenter().getX(), m.getCenter().getY()-unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(topBack)){
			float topBackVal = netChargeMinusThis(topBack, m);
			if(topBackVal < lowest){
				lowest = topBackVal;
				location = topBack;
				directionMoved = 14;
			}
		}
		Coordinate topFront = new Coordinate(m.getCenter().getX(), m.getCenter().getY()-unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(topFront)){
			float topFrontVal = netChargeMinusThis(topFront, m);
			if(topFrontVal < lowest){
				lowest = topFrontVal;
				location = topFront;
				directionMoved = 13;
			}
		}
		Coordinate downLeft = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()+unit, m.getCenter().getZ());
		if(inShellEllipsoid(downLeft)){
			float downLeftVal = netChargeMinusThis(downLeft, m);
			if(downLeftVal < lowest){
				lowest = downLeftVal;
				location = downLeft;
				directionMoved = 7;
			}
		}
		Coordinate downRight = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()+unit, m.getCenter().getZ());
		if(inShellEllipsoid(downRight)){
			float downRightVal = netChargeMinusThis(downRight, m);
			if(downRightVal < lowest){
				lowest = downRightVal;
				location = downRight;
				directionMoved = 8;
			}
		}
		Coordinate downBack = new Coordinate(m.getCenter().getX(), m.getCenter().getY()+unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(downBack)){
			float downBackVal = netChargeMinusThis(downBack, m);
			if(downBackVal < lowest){
				lowest = downBackVal;
				location = downBack;
				directionMoved = 10;
			}
		}
		Coordinate downFront = new Coordinate(m.getCenter().getX(), m.getCenter().getY()+unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(downFront)){
			float downFrontVal = netChargeMinusThis(downFront, m);
			if(downFrontVal < lowest){
				lowest = downFrontVal;
				location = downFront;
				directionMoved = 9;
			}
		}
		Coordinate leftBack = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY(), m.getCenter().getZ()-unit);
		if(inShellEllipsoid(leftBack)){
			float leftBackVal = netChargeMinusThis(leftBack, m);
			if(leftBackVal < lowest){
				lowest = leftBackVal;
				location = leftBack;
				directionMoved = 17;
			}
		}
		Coordinate rightBack = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY(), m.getCenter().getZ()-unit);
		if(inShellEllipsoid(rightBack)){
			float rightBackVal = netChargeMinusThis(rightBack, m);
			if(rightBackVal < lowest){
				lowest = rightBackVal;
				location = rightBack;
				directionMoved = 18;
			}
		}
		Coordinate leftFront = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY(), m.getCenter().getZ()+unit);
		if(inShellEllipsoid(leftFront)){
			float leftFrontVal = netChargeMinusThis(leftFront, m);
			if(leftFrontVal < lowest){
				lowest = leftFrontVal;
				location = leftFront;
				directionMoved = 15;
			}
		}
		Coordinate rightFront = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY(), m.getCenter().getZ()+unit);
		if(inShellEllipsoid(rightFront)){
			float rightFrontVal = netChargeMinusThis(rightFront, m);
			if(rightFrontVal < lowest){
				lowest = rightFrontVal;
				location = rightFront;
				directionMoved = 16;
			}
		}
		Coordinate topLeftBack = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()-unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(topLeftBack)){
			float topLeftBackVal = netChargeMinusThis(topLeftBack, m);
			if(topLeftBackVal < lowest){
				lowest = topLeftBackVal;
				location = topLeftBack;
				directionMoved = 24;
			}
		}
		Coordinate topLeftFront = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()-unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(topLeftFront)){
			float topLeftFrontVal = netChargeMinusThis(topLeftFront, m);
			if(topLeftFrontVal < lowest){
				lowest = topLeftFrontVal;
				location = topLeftFront;
				directionMoved = 23;
			}
		}
		Coordinate topRightBack = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()-unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(topRightBack)){
			float topRightBackVal = netChargeMinusThis(topRightBack, m);
			if(topRightBackVal < lowest){
				lowest = topRightBackVal;
				location = topRightBack;
				directionMoved = 26;
			}
		}
		Coordinate topRightFront = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()-unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(topRightFront)){
			float topRightFrontVal = netChargeMinusThis(topRightFront, m);
			if(topRightFrontVal < lowest){
				lowest = topRightFrontVal;
				location = topRightFront;
				directionMoved = 25;
			}
		}
		Coordinate downLeftBack = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()+unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(downLeftBack)){
			float downLeftBackVal = netChargeMinusThis(downLeftBack, m);
			if(downLeftBackVal < lowest){
				lowest = downLeftBackVal;
				location = downLeftBack;
				directionMoved = 20;
			}
		}
		Coordinate downLeftFront = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY()+unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(downLeftFront)){
			float downLeftFrontVal = netChargeMinusThis(downLeftFront, m);
			if(downLeftFrontVal < lowest){
				lowest = downLeftFrontVal;
				location = downLeftFront;
				directionMoved = 19;
			}
		}
		Coordinate downRightBack = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()+unit, m.getCenter().getZ()-unit);
		if(inShellEllipsoid(downRightBack)){
			float downRightBackVal = netChargeMinusThis(downRightBack, m);
			if(downRightBackVal < lowest){
				lowest = downRightBackVal;
				location = downRightBack;
				directionMoved = 22;
			}
		}
		Coordinate downRightFront = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY()+unit, m.getCenter().getZ()+unit);
		if(inShellEllipsoid(downRightFront)){
			float downRightFrontVal = netChargeMinusThis(downRightFront, m);
			if(downRightFrontVal < lowest){
				lowest = downRightFrontVal;
				location = downRightFront;
				directionMoved = 21;
			}
		}
		m.move(location);
		return directionMoved;
	}

	/**
	 * Iterates through the list of metaballs and allows all of them to move to places with less force
	 * @return a boolean indicating whether the system is in equilibrium (if none of the metaballs moved)
	 */
	public boolean moveAllMetaballs(){
		int equilibrium = 0;
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			equilibrium += considerAllSides(m, 5);
		}
		return equilibrium == 0;
	}

	/**
	 * moves each metaball until they reach equilibrium
	 */
	public void moveToEquilibrium(){
		boolean equilibrium = false;
		while(!equilibrium){
			equilibrium = moveAllMetaballs();
		}
		iterateThroughGrid();
	}

	/**
	 * returns the selected cell if there is one
	 * @return null if no cell is selected, or the selected cell
	 */
	public Cell someCellSelected(){
		for(String s: displayShell.getCells().keySet()){
			if(displayShell.getCells().get(s).isSelected()){
				return displayShell.getCells().get(s);
			}
		}
		return null;
	}

	/**
	 * Color is determined according to the metaball field; each metaball's color
	 * contributes to the color at a point proportionate to its distance from the point
	 * @param point the point we want to get the color at
	 * @return the color at that point
	 */
	public RGB netColorHere(Coordinate point){
		int red = 0;
		int green = 0;
		int blue = 0;
		Cell selected = someCellSelected();
		for(String s: displayShell.getCells().keySet()){
			float colorCoefficient = .01f;
			if(selected != null){
				colorCoefficient = .005f;
			}
			if(displayShell.getCells().get(s).isSelected()){
				colorCoefficient = .02f;
			}
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			float charge = Math.abs(colorCoefficient * m.fourthPowerChargeFrom(point));
			if(charge > 1) charge = 1;
			red += charge*m.getColor().getRed();
			green += charge*m.getColor().getGreen();
			blue += charge*m.getColor().getBlue();
		}
		return new RGB(red, green, blue);
	}

	/**
	 * Attempts to define the boundaries of the metaballs such that picking
	 * method using color buffer can continue to be used
	 * @param point the point at which we're trying to define a unique color
	 * @return the unique color for this point
	 */
	public RGB uniqueColorHere(Coordinate point){
		float influence = 0;
		Cell nearest = null;
		for(String s: displayShell.getCells().keySet()){
			Cell c = displayShell.getCells().get(s);
			Metaball m = c.getRepresentation();
			float infFrom = Math.abs(m.fourthPowerChargeFrom(point));
			if(infFrom > influence){
				influence = infFrom;
				nearest = c;
			}
		}
		//the metaball with the strongest influence at this point is the one that's considered to be located here
		return nearest.getUniqueColor();
	}

	/**
	 * Populates the array of shapes; call when image needs to change
	 * Highest cost function!! call only when absolutely necessary!
	 */
	public void iterateThroughGrid(){		
		threshold = (float) (15.0 + displayShell.getCells().keySet().size());
		vertices = new ArrayList<ArrayList<Coordinate>>();
		displayColorField = new ArrayList<RGB>();
		uniqueColorField = new ArrayList<RGB>();
		int W = displayShell.getShellWidth();
		int H = displayShell.getShellWidth();
		int D = displayShell.getShellWidth();
		field = new boolean[W+gridSize][H+gridSize][D+gridSize];
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			setFieldNearBall(m);
		}
		for(int i = -W/2; i < W/2; i += gridSize){
			for(int j = -H/2; j < H/2; j += gridSize){	
				for(int k = -D/2; k < D/2; k += gridSize){
					handleOneCube(i, j, k);
				}
			}
		}
	}
	
	/**
	 * For testing purposes, this displays the radius of influence of each cell as a semitransparent sphere
	 */
	public void showAllRad(){
		for(String s: displayShell.getCells().keySet()){
			showRadiusOfInfluence(displayShell.getCells().get(s).getRepresentation());
		}
	}
	
	/**
	 * For testing purposes, this displays the radius of influence of the given metaball as a semitransparent sphere
	 * @param m the metaball to display
	 */
	public void showRadiusOfInfluence(Metaball m){
		pushMatrix();
		translate(m.getCenter().getX(), m.getCenter().getY(), m.getCenter().getZ());
		fill(255, 50);
		sphere(m.getRadiusOfInfluence());
		popMatrix();
	}
	
	/**
	 * Takes in any Coordinate and returns the nearest coordinate location that is on the grid
	 * @param near coordinate we want to be near
	 * @param lower determines whether we round down or up
	 * @return a coordinate on the grid
	 */
	public Coordinate snapToGrid(Coordinate near, boolean lower){
		int W = displayShell.getShellWidth()/2;		
		
		float x = near.getX() - (near.getX() + W) % gridSize;
		float y = near.getY() - (near.getY() + W) % gridSize;
		float z = near.getZ() - (near.getZ() + W) % gridSize;
		
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		
		if(x < -W) x = -W;
		else if(x > W) x = W;
		if(y < -W) y = -W;
		else if(y > W) y = W;
		if(z < -W) z = -W;
		else if(z > W) z = W;
		
		if(lower) return new Coordinate(x, y, z);
		else return new Coordinate(x+gridSize, y+gridSize, z+gridSize);
	}

	/**
	 * stores the value of the field at every location within the radius of influence of the given metaball
	 * @param metaball the metaball we're looking near
	 */
	public void setFieldNearBall(Metaball metaball){
		Coordinate startPoint = snapToGrid(metaball.getCenter().plus(-metaball.getRadiusOfInfluence()), true);
		Coordinate endPoint = snapToGrid(metaball.getCenter().plus(metaball.getRadiusOfInfluence()), false);
		int W = displayShell.getShellWidth()/2;
		for(int i = (int) startPoint.getX(); i <= endPoint.getX()+1; i += gridSize){
			for(int j = (int) startPoint.getY(); j <= endPoint.getY()+1; j += gridSize){
				for(int k = (int) startPoint.getZ(); k <= endPoint.getZ()+1; k += gridSize){
					if(aboveThreshold(new Coordinate(i, j, k), metaball)){
						field[i+W][j+W][k+W] = true;
					}
				}
			}
		}
	}

	/**
	 * Prints the stored vertices to the screen; call every time step
	 * @param unique whether or not we're printing with unique colors
	 */
	public void printVertices(boolean unique){
		int colorCounter = 0;
		for(ArrayList<Coordinate> shape: vertices){
			if(unique){
				RGB color = uniqueColorField.get(colorCounter);
				fill(color.getRed(), color.getGreen(), color.getBlue());
			}
			else{
				RGB color = displayColorField.get(colorCounter);
				fill(color.getRed(), color.getGreen(), color.getBlue());
			}
			beginShape();
			for(Coordinate c: shape){
				vertex(c.getX(), c.getY(), c.getZ());
			}
			endShape(CLOSE);
			colorCounter++;
		}
	}

	/**
	 * Find out what the stored field value is at the given point
	 * @param at the point we want to poll
	 * @return the field value (above or below threshold)
	 */
	public boolean pollField(Coordinate at){
		int W = displayShell.getShellWidth();
		int H = displayShell.getShellWidth();
		int D = displayShell.getShellWidth();
		return field[(int) at.getX() + W/2][(int) at.getY() + H/2][(int) at.getZ() + D/2];
	}

	/**
	 * Applies the marching cubes algorithm to the cube at this point
	 * @param x x coordinate of point
	 * @param y y coordinate of point
	 * @param z z coordinate of point
	 */
	public void handleOneCube(int x, int y, int z){
		if(!inShellEllipsoid(new Coordinate(x, y, z))) return;
		RGB color = netColorHere(new Coordinate(x, y, z));
		RGB uniqueColor = uniqueColorHere(new Coordinate(x, y, z));
		Cube cube = new Cube(this, x, y, z, gridSize);
		cube.vertices.put(cube.vertex1, pollField(cube.vertex1));
		cube.vertices.put(cube.vertex2, pollField(cube.vertex2));
		cube.vertices.put(cube.vertex3, pollField(cube.vertex3));
		cube.vertices.put(cube.vertex4, pollField(cube.vertex4));
		cube.vertices.put(cube.vertex5, pollField(cube.vertex5));
		cube.vertices.put(cube.vertex6, pollField(cube.vertex6));
		cube.vertices.put(cube.vertex7, pollField(cube.vertex7));
		cube.vertices.put(cube.vertex8, pollField(cube.vertex8));
		cube.populateMids();

		//a different cube case occurs depending on how many midpoints are set to true
		switch(cube.midpoints.size()){
		case 3:
			vertices.add(cube.threeCase());
			displayColorField.add(color);
			uniqueColorField.add(uniqueColor);
			return;
		case 4:
			vertices.add(cube.fourCase());
			displayColorField.add(color);
			uniqueColorField.add(uniqueColor);
			return;
		case 5:
			ArrayList<ArrayList<Coordinate>> shapes5 = cube.fiveCase();
			vertices.addAll(shapes5);
			for(int i = 0; i < shapes5.size(); i++){
				displayColorField.add(color);
				uniqueColorField.add(uniqueColor);
			}
			return;
		case 6:
			ArrayList<ArrayList<Coordinate>> shapes6 = cube.sixCase();
			vertices.addAll(shapes6);
			for(int i = 0; i < shapes6.size(); i++){
				displayColorField.add(color);
				uniqueColorField.add(uniqueColor);
			}
			return;
		case 7:
			ArrayList<ArrayList<Coordinate>> shapes7 = cube.sevenCase();
			vertices.addAll(shapes7);
			for(int i = 0; i < shapes7.size(); i++){
				displayColorField.add(color);
				uniqueColorField.add(uniqueColor);
			}
			return;
		case 8:
			ArrayList<ArrayList<Coordinate>> shapes8 = cube.eightCase();
			vertices.addAll(shapes8);
			for(int i = 0; i < shapes8.size(); i++){
				displayColorField.add(color);
				uniqueColorField.add(uniqueColor);
			}
			return;
		case 9:
			ArrayList<ArrayList<Coordinate>> shapes9 = cube.nineCase();
			vertices.addAll(shapes9);
			for(int i = 0; i < shapes9.size(); i++){
				displayColorField.add(color);
				uniqueColorField.add(uniqueColor);
			}
			return;
		case 0:
			return;
		default:
			System.out.println("unhandled case of " + cube.midpoints.size());
			return;
		}			
	}

	/**
	 * Determines if the metaball field is above the threshold value at this point
	 * @param point the point we're considering
	 * @return true if above, false if below
	 */
	public boolean aboveThreshold(Coordinate point, Metaball m){
		float charge = netChargeHere(point, m);
		return charge > threshold;
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
		iterateThroughGrid(); //image has changed
	}
	
	/**
	 * moves the cells during animations
	 * shows each step if showAnimations is on, otherwise just moves them to equilibrium immediately
	 */
	public void moveAllCells(){
		if(showAnimations.getState(0)){
			moving = true;
		}
		else{
			moveToEquilibrium();
		}
	}

	/**
	 * progresses time forward; called when right arrow is pressed, or automatically in auto time flow mode
	 */
	public void progressForward(){
		//limited to time 95; this is the end of the simulation
		if(currentTime <= 95){
			//reset the text
			userText = "Click a cell to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
			//certain actions occurring will cause us to need to change the picture. in these situations, we'll want to call
			//iterateOverGrid(). But this function is very expensive, so we want to do this only if strictly necessary.
			boolean mustUpdateDisplay = fateState; //if we're set to fateState we need to update every time step, because the colors change unpredictably
			if(someCellSelected() != null){ //if there was a selected cell
				someCellSelected().setSelected(false); //we want to deselect it
				mustUpdateDisplay = true; //and we'll have to redraw the screen to reflect this
			}
			int cellsOnScreenBefore = displayShell.getCells().keySet().size(); //how many cells were on the screen before this time step?
			if(currentTime == farthestSeen){ //if this is currently as far as we've been
				farthestShell.timeStep(); //call the shell's timestep method to calculate next step
				displayShell = farthestShell; //and display the resultant shell
				int cellsOnScreenAfter = displayShell.getCells().keySet().size(); //now how many cells are on the screen?
				currentTime++; //the current time has increased
				farthestSeen++; //the farthest-visited time has increased
				numCellsAtTime.put(currentTime, cellsOnScreenAfter); //store the number of cells that were present at this time step
				if(cellsOnScreenBefore != cellsOnScreenAfter){ //if we gained new cells this time step...
					shellsOverTime.put(cellsOnScreenAfter, new Shell(farthestShell)); //store a new duplicate shell in the hashmap, for if we come backwards in time
					moveAllCells(); //allow the cells to move around. display will be updated within this function
				}
				progressBar.setValue(currentTime); //increment the progress bar
			}
			else{ //if we're looking at a past timestep that we have already been to before
				currentTime++; //current time has incremented
				if(numCellsAtTime.get(currentTime) != cellsOnScreenBefore){ //if the stored value for the number of cells on the screen at this time doesn't match what it was before
					displayShell = new Shell(shellsOverTime.get(numCellsAtTime.get(currentTime))); //get the stored shell containing this # of cells from the hashmap
					updateColorMode(); //update the color mode in case the shell that was stored in the hashmap at this time was with different colors
					moveAllCells(); //allow the cells to move around. display will be updated within this function
				}
				progressBar.setValue(currentTime); //increment the progress bar
			}
			if(mustUpdateDisplay){
				iterateThroughGrid(); //if any of our update conditions occurred, update the screen
			}
		}
	}
	
	/**
	 * progresses backward in time, called when left arrow is pressed
	 */
	public void progressBackward(){
		if(currentTime > 1){ //can't go before time 1
			int numCellsBefore = displayShell.getCells().keySet().size(); //how many cells were on the screen before we started making calculations?
			//reset the text
			userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
			currentTime--; //current time has decremented
			//certain actions occurring will cause us to need to change the picture. in these situations, we'll want to call
			//iterateOverGrid(). But this function is very expensive, so we want to do this only if strictly necessary.
			boolean mustUpdateDisplay = false;
			if(someCellSelected() != null){ // if there is some cell selected
				someCellSelected().setSelected(false); //deselect it
				mustUpdateDisplay = true; // need to update display to reflect this
			}
			int numCellsAfter = numCellsAtTime.get(currentTime); //what is the stored value for the number of cells that should be on the screen at this timestep?
			if(numCellsAfter != numCellsBefore){ //if we're going back by a cell division
				displayShell = new Shell(shellsOverTime.get(numCellsAfter)); //need to get the stored shell containing the new number of cells
				mustUpdateDisplay = true; //need to update visual if some of the cells are supposed to "disappear"
				moveAllCells(); //allow the cells to move around
			}
			if(mustUpdateDisplay){ //if one of the conditions occurred to make the sceen update necessary
				updateColorMode(); //make sure the color mode doesn't change when we grabbed the new shell from the hashmap
				iterateThroughGrid(); //update the display
			}
			progressBar.setValue(currentTime); //set the progress bar to reflect our timestep
		}
	}

	//when the user is typing, different things should happen depending on what keys they're hitting
	public void keyReleased(){
		if(keyCode == ESC) exit(); //exit program when user hits Esc key

		if(mutantsChosen){ //only run if mutantsChosen is set, because userText doesn't exist yet if not - null pointer
			if(!moving){ //don't interrupt an animation in progress, it causes problems
				if(keyCode == RIGHT){ //press right to progress forward in time
					progressForward();
				}
				else if(keyCode == LEFT){ //press left arrow to progress backward in time
					progressBackward();
				}
				else if(keyCode == BACKSPACE){ //manually implement backspace
					if (userText.length() > 0) { //if there is text
						userText = userText.substring(0, userText.length()-1); //replace it with the same text minus the last letter
					}
				}
				else if(keyCode == ENTER || keyCode == RETURN){ //enter finalizes a command
					if(!displayShell.getCells().containsKey(userText)){ //if the userText isn't currently an existing cell name, print that the cell doesn't exist
						userText = "Cell not present";
					}
					else{ //if it is a cell name, print the genes list for the cell
						for(String s: displayShell.getCells().keySet()){
							if(!s.equals(userText)) displayShell.getCells().get(s).setSelected(false);
							else displayShell.getCells().get(s).setSelected(true); //and select the one that was written
						}
						userText = displayShell.getCells().get(userText).getInfo();
						iterateThroughGrid(); //update display to show selected cell
					}
				}
				else if(((int) key) >= 33 && ((int) key) <= 126){ //if it's any other ("normal" ascii) key, just add that letter to the userText
					if (userText.length() > 10) userText = ""; //clear it if it gets to more than 10 characters so the user never has to delete a bunch of text; no useful commands are over 10 char anyway
					userText = userText + key;
				}
			}
		}
	}

	/**
	 * detects user setting a new gridSize on the slider
	 */
	public void mouseReleased(){
		if(mutantsChosen){ //the slider only exists in the main screen, after the mutant choice menu
			if(chooseGridSize.getValue() != gridSize){ //if we notice a difference between the set grid size and the value on the slider
				gridSize = (int) chooseGridSize.getValue(); //set the gridsize to the slider value
				iterateThroughGrid(); //update display to reflect the change
			}
			camera.stop(); //also, we didn't like the camera's "momentum" on rotation, so we stop it as soon as the mouse is released
		}
	}

	/**
	 * handles picking!
	 */
	public void mouseClicked(){
		if(mutantsChosen){
			if(!firstClick){ //this ignores the very first click that occurs in the main screen
				firstClick = true; //because we were accidentally picking the first cell after clicking the create shell button
			}
			else{
				if(!moving){ //don't allow picking during animations, it gets too confusing
					if(mouseX < 4*(width/5)){ //only do object picking in the 3d side of the screen
						int colorFound = getHiddenColor(); //render cells as their hidden unique colors. what color was the pixel we clicked on?
						if(colorFound != -16777216){ //only do object picking if the selected pixel isn't black, black will never be a cell
							Cell chosen = null;
							for(String s: displayShell.getCells().keySet()){ //find the cell with the matching hidden color
								Cell c = displayShell.getCells().get(s);
								int keyColor = color(c.getUniqueColor().getRed(), c.getUniqueColor().getGreen(), c.getUniqueColor().getBlue());
								if(colorFound == keyColor){
									chosen = c;
								}
							}
							for(String s: displayShell.getCells().keySet()){
								displayShell.getCells().get(s).setSelected(false); //set all non qualifying cells to be unselected
							}
							if(chosen != null){
								chosen.setSelected(true); //select the chosen cell
								userText = chosen.getInfo(); //print its info
								iterateThroughGrid(); //update display
							}
						}
					}
				}
			}
		}
	}

	/**
	 * display the correct color key for the currently selected colormode
	 * @param colorMode the current colormode
	 */
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

	/**
	 * draw the coordinate axes to the screen
	 */
	public void drawAxes(){
		strokeWeight(5);
		//set up to draw x axis
		stroke(255,0,0);
		fill(255,0,0);
		line(-550, 150, 0, -350, 150, 0);
		text("a", -550, 140, 0);
		text("p", -350, 140, 0);
		//set up to draw y axis
		stroke(0,255,0);
		fill(0,255,0);
		text("v", -450, 270, 0);
		text("d", -450, 40, 0);
		line(-450, 250, 0, -450, 50, 0);
		//set up to draw z axis
		stroke(0,0,255);
		fill(0,0,255);
		line(-450, 150, -100, -450, 150, 100);
		text("r", -440, 150, -100);
		text("l", -460, 150, 100);
		//set up to draw cells
		noStroke();
	}

	/**
	 * code grabbed from online
	 * prevents the information panel (controlp5) from rotating
	 * when the camera rotates
	 */
	public void gui(){
		hint(DISABLE_DEPTH_TEST);
		camera.beginHUD();
		info.draw();
		camera.endHUD();
		hint(ENABLE_DEPTH_TEST);
	}

	/**
	 * this allows the program to be run as a java application
	 * @param args
	 */
	public static void main(String args[]){
		PApplet.main(new String[] { "--present", "processing.BasicVisual" });
	} 

	/**
	 * calls incrementColor on the visual's instance of currentColor
	 */
	public void incrementCurrentColor(){
		currentColor.incrementColor();
	}

	/**
	 * renders the scene with each cell as its unique color
	 * no lighting so that each cell is a flat color
	 * for object picking
	 */
	public void renderAsHiddenColors(){
		noLights();
		if(showShell.getState(0)){ //if we were showing the shell, that will influence the color picked
			background(0); //so cover it up
			printVertices(true); //and print just the cells, no shell, for 1 timestep
		}
		else{
			printVertices(true); //else just print the cells (with unique colors)
		}
	}

	/**
	 * gets the hidden color of the pixel that was just selected
	 * @return the color as an int
	 */
	public int getHiddenColor(){
		renderAsHiddenColors(); //render the cells with their unique colors
		loadPixels(); //load the image into the pixel array
		return pixels[width*mouseY+mouseX]; //get the color of the pixel just clicked
	}

	/**
	 * Determines whether a given point is within the ellipsoid of the shell
	 * @param point the point we're considering
	 * @return true if it's inside the ellipsoid, false otherwise
	 */
	public boolean inShellEllipsoid(Coordinate point){
		return ellipsoidContribution(point) < 1;
	}

	/**
	 * A mathematical function that is largest at the surface of an ellipsoid
	 * and drops off as you approach its center point
	 * used to determine how hard the shell is "pushing" on cells (hardest near the surface)
	 * @param point the point at which we want to calculate the force
	 * @return the strength of the shell's force at that point
	 */
	public float ellipsoidContribution(Coordinate point){
		return (float) Math.pow((Math.pow(point.getX(), 2) / Math.pow(displayShell.getShellWidth(), 2) +
				Math.pow(point.getY(), 2) / Math.pow(displayShell.getShellHeight(), 2) +
				Math.pow(point.getZ(), 2) / Math.pow(displayShell.getShellDepth(), 2)), 2);
	}

}