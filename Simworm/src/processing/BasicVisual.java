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
import dataStructures.ConsList;
import dataStructures.Coordinate;
import dataStructures.RGB;
import dataStructures.Shell;

public class BasicVisual extends PApplet{	
	private static final long serialVersionUID = 1L;
	Shell displayShell;
	Shell farthestShell;
	public ConsList conslist = new ConsList();
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
	int width = screenSize.width;
	int height = screenSize.height;
	Slider progressBar;
	Button startOver;
	RadioButton showAnimations;
	Slider chooseGridSize;
	
	boolean firstTime = true;
	boolean firstClick = false;
	public RGB currentColor;
	boolean moving = false;
	
	//marching cubes fields
	int gridSize = 8;
	float threshold = 16.0f;
//	boolean field[][][]; 
	private ArrayList<ArrayList<Coordinate>> vertices;
	private ArrayList<RGB> displayColorField;
	private ArrayList<RGB> uniqueColorField;
	private boolean drawWithEllisoids = false;

	//called on start. opens the screen where the user chooses mutant genes
	@SuppressWarnings("deprecation")
	public void setup(){
		mutantsChosen = false;
		mutants = new HashMap<String, Boolean>();
		if(firstTime){
			size((int) width, (int) height, P3D);
			camera = new PeasyCam(this, 600); //initialize the peasycam
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
		iterateThroughGrid();
		
		userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
		lineageState = true;
		fateState = false;
		parsState = false;

		userTextArea = new Textarea(info, "infoText"); //textarea where the user can input commands and receive information
		userTextArea.setPosition((float) (width/1.25), height/40)
		.setSize((int) (width/5), (int) (height/5)).
		setFont(createFont("arial", (width/114)));
		cellNamesArea = new Textarea(info, "namesText"); //textarea where the names of the currently present cells are displayed
		cellNamesArea.setPosition((float) (width/1.25), (float) (5*height/20))
		.setSize((int) (width/5), (int) (height/8)).
		setFont(createFont("arial", (width/114)));

		new Button(info, "cell color key") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 13*height/30))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		new Button(info, "show animations") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 18*height/30))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
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
		
		new Button(info, "image granularity") //just a label, not interactive
		.setPosition((float) (width/1.26), (float) (height - 8*height/15))
		.setColorBackground(color(0, 0, 0))
		.setColorActive(color(0, 0, 0))
		.setColorForeground(color(0, 0, 0))
		.captionLabel().setControlFont(new ControlFont(createFont("arial", (float) (width/90))));
		
		chooseGridSize = new Slider(info, "")
		.setPosition((float) (width/1.25), (float) (height - 1*height/2))
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
		lights();
		//spotLight(255, 255, 255, 250, 0, 400, 0, 0, -1, PI/4, 2);
		noStroke();
		//some of these settings should only be called if we're past the mutants screen
		//else these elements won't exist yet and you'll get null point exceptions
		//boolean mutantsChosen will get set when we exit that screen
		if(mutantsChosen){
			drawAxes(); //draw the coordinate axes
			if(drawWithEllisoids){
				displayShell.drawAllCells(); //draw the shell using ellipsoids
			}
			else{
				printVertices(false); //draw the shell using metaballs
			}
			userTextArea.setText(userText); //show the text output that userText is currently set to
			cellNamesArea.setText(displayShell.getCellNames());
			//boolean values for lineageState, fateState, parsState exist so that we don't have to continuously set the color
			//they hold the current state of the color mode (that is, false for those that aren't currently set and true for the one that is)
			//we only set colors when the colormode doesn't match these, which indicates that the colormode has been recently changed
			if(chooseColorMode.getState(0) != lineageState || chooseColorMode.getState(1) != fateState || chooseColorMode.getState(2) != parsState) updateColorMode();
		
			if(moving){
				boolean equilibrium = false;
				equilibrium = moveAllMetaballs();
				iterateThroughGrid();
				if(equilibrium) moving = false;
			}
			
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
	public float netChargeHere(Coordinate point){
		float total = 0;
//		total += ellipsoidContribution(point) * 80;
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			total += m.chargeFrom(point.getX(), point.getY(), point.getZ());
		}
		return total;
	}
	
	public float netChargeMinusThis(Coordinate point, Metaball metaball){
		float total = 0;
		for(String s: displayShell.getCells().keySet()){
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			if(!(m.getCenter().samePoint(metaball.getCenter()))){
				total += m.chargeFrom(point.getX(), point.getY(), point.getZ());
			}
		}
		return total;
	}
	
	/**
	 * A metball looks at all its surroundings and moves in a direction that reduces the forces acting upon it
	 * @param m the metaball
	 * @param unit granularity of movement; i.e. put 1 to move 1 unit each time, higher numbers for coarser granularity
	 * @return an int representing which direction, if any, it moved. Used to test for equilibrium (if it returns 0)
	 */
	public int considerAllSides(Metaball m, int unit){
		float lowest = Math.abs(netChargeMinusThis(m.getCenter(), m));
		Coordinate location = m.getCenter();
		
		int directionMoved = 0;

		Coordinate below = new Coordinate(m.getCenter().getX(), m.getCenter().getY()+unit, m.getCenter().getZ());
		if(inShellEllipsoid(below)){
			float belowVal = Math.abs(netChargeMinusThis(below, m));
			if(belowVal > lowest){
				lowest = belowVal;
				location = below;
				directionMoved = 1;
			}
		}
		Coordinate above = new Coordinate(m.getCenter().getX(), m.getCenter().getY()-unit, m.getCenter().getZ());
		if(inShellEllipsoid(above)){
			float aboveVal = Math.abs(netChargeMinusThis(above, m));
			if(aboveVal > lowest){
				lowest = aboveVal;
				location = above;
				directionMoved = 2;
			}
		}
		Coordinate right = new Coordinate(m.getCenter().getX()+unit, m.getCenter().getY(), m.getCenter().getZ());
		if(inShellEllipsoid(right)){
			float rightVal = Math.abs(netChargeMinusThis(right, m));
			if(rightVal > lowest){
				lowest = rightVal;
				location = right;
				directionMoved = 3;
			}
		}
		Coordinate left = new Coordinate(m.getCenter().getX()-unit, m.getCenter().getY(), m.getCenter().getZ());
		if(inShellEllipsoid(left)){
			float leftVal = Math.abs(netChargeMinusThis(left, m));
			if(leftVal > lowest){
				lowest = leftVal;
				location = left;
				directionMoved = 4;
			}
		}
		Coordinate towards = new Coordinate(m.getCenter().getX(), m.getCenter().getY(), m.getCenter().getZ()+unit);
		if(inShellEllipsoid(towards)){
			float towardsVal = Math.abs(netChargeMinusThis(towards, m));
			if(towardsVal > lowest){
				lowest = towardsVal;
				location = towards;
				directionMoved = 5;
			}
		}
		Coordinate away = new Coordinate(m.getCenter().getX(), m.getCenter().getY(), m.getCenter().getZ()-unit);
		if(inShellEllipsoid(away)){
			float awayVal = Math.abs(netChargeMinusThis(away, m));
			if(awayVal > lowest){
				lowest = awayVal;
				location = away;
				directionMoved = 6;
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
	
	public void moveToEquilibrium(){
		boolean equilibrium = false;
		while(!equilibrium){
			equilibrium = moveAllMetaballs();
		}
		iterateThroughGrid();
	}
	
	/**
	 * returns the selected cell if there is one
	 * @return null if no cell is selected
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
			int colorCoefficient = 800;
			if(selected != null){
				colorCoefficient = 400;
			}
			if(displayShell.getCells().get(s).isSelected()){
				colorCoefficient *= 4;
			}
			Metaball m = displayShell.getCells().get(s).getRepresentation();
			float charge = Math.abs(colorCoefficient/m.squaredRadius(point.getX(), point.getY(), point.getZ()));
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
			float infFrom = Math.abs(m.chargeFrom(point.getX(), point.getY(), point.getZ()));
			if(infFrom > influence){
				influence = infFrom;
				nearest = c;
			}
		}
		return nearest.getUniqueColor();
	}
	
	/**
	 * Populates the array of shapes; call when image needs to change
	 */
	public void iterateThroughGrid(){
//		System.out.println("called");
		vertices = new ArrayList<ArrayList<Coordinate>>();
		displayColorField = new ArrayList<RGB>();
		uniqueColorField = new ArrayList<RGB>();
		int W = displayShell.getShellWidth();
		int H = displayShell.getShellWidth();
		int D = displayShell.getShellWidth();
//		field = new boolean[W][H][D];
		for(int i = -W/2; i < W/2; i += gridSize){
			for(int j = -H/2; j < H/2; j += gridSize){	
				for(int k = -D/2; k < D/2; k += gridSize){
					handleOneCube(i, j, k);
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
			pushMatrix();
//			scale(1, (float) displayShell.getShellHeight()/displayShell.getShellWidth(), (float) displayShell.getShellDepth()/displayShell.getShellWidth());
			beginShape();
			for(Coordinate c: shape){
				vertex(c.getX(), c.getY(), c.getZ());
			}
			endShape(CLOSE);
			popMatrix();
			colorCounter++;
		}
	}
	
	/**
	 * Applies the marching cubes algorithm to the cube at this point
	 * @param x x coordinate of point
	 * @param y y coordinate of point
	 * @param z z coordinate of point
	 */
	public void handleOneCube(int x, int y, int z){
//		if(!inShellEllipsoid(new Coordinate(x, y, z))) return;
		
		RGB color = netColorHere(new Coordinate(x, y, z));
		RGB uniqueColor = uniqueColorHere(new Coordinate(x, y, z));
		Cube cube = new Cube(this, x, y, z, gridSize);
		cube.vertices.put(cube.vertex1, aboveThreshold(cube.vertex1));
		cube.vertices.put(cube.vertex2, aboveThreshold(cube.vertex2));
		cube.vertices.put(cube.vertex3, aboveThreshold(cube.vertex3));
		cube.vertices.put(cube.vertex4, aboveThreshold(cube.vertex4));
		cube.vertices.put(cube.vertex5, aboveThreshold(cube.vertex5));
		cube.vertices.put(cube.vertex6, aboveThreshold(cube.vertex6));
		cube.vertices.put(cube.vertex7, aboveThreshold(cube.vertex7));
		cube.vertices.put(cube.vertex8, aboveThreshold(cube.vertex8));
		cube.populateMids();
		
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
	public boolean aboveThreshold(Coordinate point){
		float charge = netChargeHere(point);
		return Math.abs(charge) > threshold;
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
		iterateThroughGrid();
	}

	public void progressForward(){
		if(currentTime <= 95){
			userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
			boolean mustUpdateDisplay = false;
			if(someCellSelected() != null){
				mustUpdateDisplay = true;
			}
			if(currentTime == farthestSeen){
				farthestShell.timeStep();
				displayShell = farthestShell;
				currentTime++;
				farthestSeen++;
				shellsOverTime.put(currentTime, new Shell(farthestShell));
//				updateColorMode();
				progressBar.setValue(currentTime);
			}
			else{
				int cellDepicted = displayShell.getCells().keySet().size();
				currentTime++;
				displayShell = shellsOverTime.get(currentTime);
				//if we need to show another cell, need to update visual
				if(displayShell.getCells().keySet().size() > cellDepicted){
					mustUpdateDisplay = true;
				}
				updateColorMode();
				progressBar.setValue(currentTime);
			}
			if(mustUpdateDisplay){
				iterateThroughGrid();
			}
		}
	}

	//when the user is typing, different things should happen
	public void keyReleased(){
		if(keyCode == ESC) exit();

		if(mutantsChosen){ //only run if mutantsChosen is set, because userText doesn't exist yet if not
			if(!moving){
				if(keyCode == RIGHT){
					progressForward();
				}
				else if(keyCode == LEFT){
					if(currentTime > 1){
						int numCellsPresent = displayShell.getCells().keySet().size();
						userText = "Type a cell name to see its contents,\nor press right arrow to progress 1 timestep\nor left arrow to move backwards 1 timestep.";
						currentTime--;
						boolean mustUpdateDisplay = false;
						if(someCellSelected() != null){
							System.out.println("recognized selected cell");
							someCellSelected().setSelected(false);
							mustUpdateDisplay = true;
						}
						displayShell = shellsOverTime.get(currentTime);
						//need to update visual only if some of the cells are supposed to "disappear"
						if(displayShell.getCells().keySet().size() < numCellsPresent){
							mustUpdateDisplay = true;
						}
						if(mustUpdateDisplay){
							iterateThroughGrid();
						}
//						updateColorMode();
						progressBar.setValue(currentTime);
					}

				}
				else if(keyCode == UP){
					moveAllMetaballs();
					iterateThroughGrid();
				}
				else if(keyCode == DOWN){
					if(showAnimations.getState(0)){
						moving = true;
					}
					else{
						moveToEquilibrium();
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
	}

	public void mouseReleased(){
		if(chooseGridSize.getValue() != gridSize){
			gridSize = (int) chooseGridSize.getValue();
			iterateThroughGrid();
		}
		camera.stop();
	}
	
	public void mouseClicked(){
		if(mutantsChosen){
			if(!firstClick){
				firstClick = true;
			}
			else{
				if(!moving){
					if(mouseX < 4*(width/5)){ //only do object picking in the 3d side of the screen
						int colorFound = getHiddenColor();
						if(colorFound != -16777216){ //only do object picking if the selected pixel isn't black
							Cell chosen = null;
							for(String s: displayShell.getCells().keySet()){
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
								iterateThroughGrid();
							}
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
		printVertices(true);
	}

	/**
	 * gets the hidden color of the pixel that was just selected
	 * @return the color as an int
	 */
	public int getHiddenColor(){
		renderAsHiddenColors();
		loadPixels();
		return pixels[width*mouseY+mouseX];
	}
	
	public boolean inShellEllipsoid(Coordinate point){
		return ellipsoidContribution(point) < 1;
	}
	
	public float ellipsoidContribution(Coordinate point){
		return point.getX() * point.getX() / (displayShell.getShellWidth() * displayShell.getShellWidth()) +
				point.getY() * point.getY() / (displayShell.getShellHeight() * displayShell.getShellHeight()) +
				point.getZ() * point.getZ() / (displayShell.getShellDepth() * displayShell.getShellDepth());
	}

}