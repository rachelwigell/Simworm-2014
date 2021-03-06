package dataStructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import processing.BasicVisual;

public class Shell{
	BasicVisual window; // window in which to display the shell
	private HashMap<String, Cell> cells;
	private HashMap<String, DivisionData> divisions; //holds the information about when and how cells divide in theory (according to events queue)
	public int simTime;
	@Deprecated public float mutationProb = (float) 0; // number between 0 and 1 to indicate the probability of a mutation happening at any time - NO LONGER USED
	public HashMap<String, Boolean> mutants = new HashMap<String, Boolean>(); //all of the genes that have the potential to be mutated, and their status (true = mutant)
	public ColorMode colorMode; //set colorMode to lineage initially
	public boolean recentGrowth; //indicates when the shell has recently gained cells
	public ArrayList<String> mislocalized; // genes that are mislocalized due to a mutation
	public HashMap<String, Integer> validGenes; //valid  C. elegans gene names as determined by Wormbase text file parser
	public HashMap<CellFate, ArrayList<Gene>> fateRules;

	private final int shellWidth = 500;
	private final int shellHeight = 300;
	private final int shellDepth = 300;

	/**
	 * Constructor for a shell - initializes everything
	 * @param window The PApplet in which the shell will be drawn
	 * @param mutants The user's choice for which genes should be mutated in this shell
	 */
	public Shell(BasicVisual window, HashMap<String, Boolean> mutants){
		this.window = window;
		this.mutants = mutants;
		this.divisions = new HashMap<String, DivisionData>();
		this.simTime = 1;
		this.colorMode = ColorMode.LINEAGE;
		this.recentGrowth = false;
		this.mislocalized = new ArrayList<String>();
		this.fateRules = new HashMap<CellFate, ArrayList<Gene>>();

		//info about the shell itself
		this.cells = new HashMap<String, Cell>();

		//info about p-0, which fills the whole shell at the start
		Coordinate startCenter = new Coordinate(0, 0, 0);
		Coordinate startLengths = new Coordinate(shellWidth, shellHeight, shellDepth);

		//populate events queue data from csv file
		//look in two different places for this file - directory is different depending on run type
		//for java application or junit test
		try{
			divisions = readEventsQueue("src/components/eventsQueue.csv");
		}
		catch(Exception e){
			//for java applet or executable
			try{
				divisions = readEventsQueue("eventsQueue.csv");
			}
			catch(Exception f){
				System.out.println("Couldn't find eventsQueue.csv at either of the expected locations.");
			}
		}

		//populate valid gene names from wormbase file
		//for java application or junit test
		try{
			compileValidGenes("src/components/wormbaseGeneInfo.txt");
		}
		catch(Exception e){
			//for java applet or executable
			try{
				compileValidGenes("wormbaseGeneInfo.txt");
			}
			catch(Exception f){
				System.out.println("Couldn't find wormbaseGeneInfo.txt at either of the expected locations.");
			}
		}
		
		try{
			compileFateRules("src/components/fateRules.csv");
		}
		catch(Exception e){
			try{
				compileFateRules("fateRules.csv");
			}
			catch(Exception f){
				System.out.println("Couldn't find fateRules.csv at either of the expected locations.");
			}
		}

		window.currentColor = new RGB(1, 0, 0);

		//create p-0 with all the info calculated
		Cell start = new Cell(this.window, "p-0", startCenter, startLengths, new HashMap<String, Gene>(), new RGB(255, 255, 0), divisions.get("p-0"));
		//works for java application or junit
		try{
			start.readGeneInfo("src/components/genes.csv", this);
		}
		catch(Exception e){
			//works for java applet or executable
			try{
				start.readGeneInfo("genes.csv", this);
			}
			catch(Exception f){}
		}
		start.allRecentlyChanged();

		//calculate mutations
		perShellMutations();
		perCellMutations(start);

		this.cells.put("p-0", start);
	}

	//getters
	public HashMap<String, Cell> getCells() {
		return cells;
	}

	public HashMap<String, DivisionData> getDivisions() {
		return divisions;
	}

	public int getShellWidth(){
		return shellWidth;
	}

	public int getShellHeight(){
		return shellHeight;
	}

	public int getShellDepth(){
		return shellDepth;
	}	

	public HashMap<CellFate, ArrayList<Gene>> compileFateRules(String file) throws FileReadErrorException, InvalidFormatException{
		HashMap<CellFate, ArrayList<Gene>> fateRules = new HashMap<CellFate, ArrayList<Gene>>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			line = reader.readLine();
			HashMap<Integer, CellFate> locations = new HashMap<Integer, CellFate>();
			String[] fateInfo = line.split(","); //split into an array using commas as separators
			for(int i = 0; i < fateInfo.length; i++){
				String s = fateInfo[i];
				if(s.equals("Germline")){
					locations.put(i, CellFate.GERMLINE);
					fateRules.put(CellFate.GERMLINE, new ArrayList<Gene>());
				}
				else if(s.equals("MS")){
					locations.put(i, CellFate.MS);
					fateRules.put(CellFate.MS, new ArrayList<Gene>());
				}
				else if(s.equals("E")){
					locations.put(i, CellFate.E);
					fateRules.put(CellFate.E, new ArrayList<Gene>());
				}
				else if(s.equals("C")){
					locations.put(i, CellFate.C);
					fateRules.put(CellFate.C, new ArrayList<Gene>());
				}
				else if(s.equals("D")){
					locations.put(i, CellFate.D);
					fateRules.put(CellFate.D, new ArrayList<Gene>());
				}
			}
			int row = 2;
			String[] gene = new String[fateInfo.length];
			while((line = reader.readLine()) != null){ //read one row
				fateInfo = line.split(",");
				for(int i = 0; i < fateInfo.length; i++){
					if(validGenes.keySet().contains(fateInfo[i])){ 
						gene[i] = fateInfo[i];
					}
					else if(fateInfo[i].equals("A")){
						ArrayList<Gene> genes = fateRules.get(locations.get(i));
						genes.add(new Gene(gene[i], new GeneState(GeneStates.ACTIVE)));
						fateRules.put(locations.get(i), genes);
					}
					else if(fateInfo[i].equals("I")){
						ArrayList<Gene> genes = fateRules.get(locations.get(i));
						genes.add(new Gene(gene[i], new GeneState(GeneStates.INACTIVE)));
						fateRules.put(locations.get(i), genes);
					}
					else{
						System.out.println("This is happening");
						reader.close();
						throw new InvalidFormatException(FormatProblem.INVALIDSTATESTRICT, row, i+1);
					}
				}
				row++;
			}
			reader.close();
			this.fateRules = fateRules;
			return fateRules;
		}
		catch (FileNotFoundException e){
			throw new FileReadErrorException(file);
		}
		catch (IOException e){
			throw new FileReadErrorException(file);
		}
	}
	
	/**
	 * Determines the name of a daughter cell based on what the parent cell is and what axis it's dividing along
	 * daughter1 is always the more anterior, dorsal, or right child
	 * @param parent The name of the cell that is dividing
	 * @param axis The axis along which the cell is dividing
	 * @param d1 Indicates whether we are calculating the name of daughter1 or daughter2
	 * @return The name that the corresponding daughter cell should have
	 */
	public String nameCalc(String parent, Axes axis, boolean d1){
		//first handle non-programmatically determined names
		if(parent.equals("p-0")){
			if(d1) return "ab";
			else return "p-1";
		}
		else if(parent.equals("p-1")){
			if(d1) return "ems";
			else return "p-2";
		}
		else if(parent.equals("ems")){
			if(d1) return "ms";
			else return "e";
		}
		else if(parent.equals("p-2")){
			if(d1) return "c";
			else return "p-3";
		}	
		else if(parent.equals("p-3")){
			if(d1) return "d";
			else return "p-4";
		}
		//now handle names that involve appending a direction indicator
		switch(axis){
		case X:
			if(!parent.contains("-")) parent += "-";
			if(d1) return parent + "a";
			else return parent + "p";
		case Y:
			if(!parent.contains("-")) parent += "-";
			if(d1) return parent + "d";
			else return parent + "v";
		case Z:
			if(!parent.contains("-")) parent += "-";
			if(d1) return parent + "r";
			else return parent + "l";
		}
		return null; //code never reaches this point
	}	

	/**
	 * Calculates what generation this cell belongs to, using its name
	 * @param name the name of the cell of interest
	 * @return the generation it belongs to
	 */
	public int calculateGeneration(String name){
		//first handle the prefix-names
		if(name.equals("p-0")) return 0;
		else if(name.equals("p-1")) return 1;
		else if(name.equals("ab")) return 1;
		else if(name.equals("ems")) return 2;
		else if(name.equals("p-2")) return 2;
		else if(name.equals("p-3")) return 3;
		else if(name.equals("c")) return 3;
		else if(name.equals("e")) return 3;
		else if(name.equals("ms")) return 3;
		else if(name.equals("p-4")) return 4;
		else if(name.equals("d")) return 4;
		//if it isn't one of these names, it's one of those names with the addition of a hyphenated suffix (e.g. -alp)
		//remove letters from the end; each one represents another generation
		//once you get back to the hyphen, add the generation of the prefix to get the total generation
		else{
			char c = name.charAt(name.length()-1);
			int gen = 0;
			while(c != '-'){
				name = name.substring(0, name.length()-1);
				gen++;
				c = name.charAt(name.length()-1);
			}
			return gen + calculateGeneration(name.substring(0, name.length()-1));
		}
	}

	/** calculates the genes that a child will contain; to be called during cell division
	 * @param parent the parent that is dividing
	 * @param axis the axis along which the division is occurring
	 * @param daughter1 true if we are calculating genes for daughter1, false if we're calculating for daughter2
	 * @return the genes that the child will contain
	 */
	public HashMap<String, Gene> childGenes(String parent, Axes axis, boolean daughter1){
		HashMap<String, Gene> parentGenes = this.cells.get(parent).getGenes(); //get the parent's genes out of the hashmap
		HashMap<String, Gene> childGenes = new HashMap<String, Gene>(); //create a new hashmap to hold the child's genes; this will be returned
		//must move genes that are changing compartments before we calculate child genes
		for(String s: parentGenes.keySet()){ //look at all genes in the cell
			Gene g = parentGenes.get(s);
			if(!mislocalized.contains(g.getName())){ 
				if(g.getChanges().keySet().size() != 0){ //if this is a gene that changes compartments mid-sim
					if(g.getChanges().keySet().contains(parent)){ //check if the division it changes during is the one that's occurring
						g.setLocation(g.getChanges().get(parent)); //set new compartment
					}
				}
			}
		}
		switch(axis){ //now our actions will depend on which axis we're dividing along.
		case X:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getAP(); //since we're dealing with the X axis, only have to consider the x compartments
				//since daughter1 is always on the anterior side, gene should not go in if we're calculating for daughter1 and it's located in the posterior compartment
				//equivalent with daughter2/anterior
				if((comp != Compartment.POSTERIOR && daughter1) || (comp != Compartment.ANTERIOR && !daughter1)){ //so if neither situation is occurring...
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons()); //add a new instance of the gene to childGenes, and populate its relevantCons list
				}
				//cells not present should be "inherited" regardless of compartment, so that they have the opportunity to be created in any cell
				//when their antecedents are fulfilled
				if(g.getState().isNotPresent()){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons()); //add a new instance of the gene to childGenes, and populate its relevantCons list
				}
			}
			break;
		case Y: //equivalent code for y and z
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getDV();
				if((comp != Compartment.VENTRAL && daughter1) || (comp != Compartment.DORSAL && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons());
				}
				if(g.getState().isNotPresent()){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons());
				}
			}
			break;
		case Z:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getLR();
				if((comp != Compartment.LEFT && daughter1) || (comp != Compartment.RIGHT && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons());
				}
				if(g.getState().isNotPresent()){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges(), window).populateCons());
				}
			}
			break;
		}
		return childGenes;
	}

	/**
	 * Duplication constructor - creates a new shell with all the properties of the given shell. Needed for the hashmap that allows for backward timesteps.
	 * @param toDup The shell to be duplicated.
	 */
	public Shell(Shell toDup){
		this.mutants = toDup.mutants;
		this.window = toDup.window;
		this.simTime = toDup.simTime;
		this.colorMode = toDup.colorMode;
		this.fateRules = toDup.fateRules;
		HashMap<String, Cell> cellsmap = new HashMap<String, Cell>();

		for(String s: toDup.cells.keySet()){
			cellsmap.put(s, new Cell(toDup.cells.get(s)));
		}
		this.cells = cellsmap;
	}

	/**
	 * simulates division of cell by calculating new names, centers, dimensions, and gene states of daughter cells
	 * daughter1 is always the more anterior, dorsal, or right child
	 * @param data The divisionData for the cell that is dividing; contains name, axis, percentages, etc.
	 * @return Data on which cells are now gone or new cells that were created
	 */
	public CellChangesData cellDivision(DivisionData data){
		String parent = data.getParent();
		double d1Percentage = data.getD1Percentage();
		Axes axis = data.getAxis();
		Cell ofInterest = this.cells.get(parent); //retrieve the dividing cell from the hashmap
		Coordinate d1Center;
		Coordinate d1Lengths;
		Coordinate d2Center;
		Coordinate d2Lengths;
		Cell daughter1;
		Cell daughter2;
		String d1name = nameCalc(parent, axis, true);
		String d2name = nameCalc(parent, axis, false);
		HashMap<String, Gene> d1genes = childGenes(parent, axis, true); //call childgenes to determine the genes inherited by daughter1
		HashMap<String, Gene> d2genes = childGenes(parent, axis, false); //equivalent for daughter2
		RGB color1 = new RGB(255, 255, 255);
		RGB color2 = new RGB (255, 255, 255);
		switch(colorMode){ //choose cell color based on current colormode
		case FATE:
			color1 = cellColorFate(d1genes);
			color2 = cellColorFate(d2genes);
			break;
		case PARS:
			color1 = cellColorPars(d1genes);
			color2 = cellColorPars(d2genes);
			break;
		case LINEAGE:
			color1 = cellColorLineage(d1name);
			color2 = cellColorLineage(d2name);
			break;
		}

		CellChangesData changes = new CellChangesData(new ArrayList<String>(), new ArrayList<Cell>()); //will hold all the added/removed cells; this is returned and the changes will propagate within other functions
		switch(axis){ //how we proceed depends on which axis we're dividing along
		case X:
			d1Center = new Coordinate((float) ((d1Percentage - 1) * (ofInterest.getLengths().getX() / 2.0) + ofInterest.getRepresentation().getCenter().getX()),
					ofInterest.getRepresentation().getCenter().getY(), ofInterest.getRepresentation().getCenter().getZ()); //calculations to get the center of daughter1
			d1Lengths = new Coordinate((float) (d1Percentage*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ()); //calculations to get the dimensions of daughter1
			daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, d1genes, color1, divisions.get(d1name)); //create daughter1 with all the fields we have calculated
			d2Center = new Coordinate((float) (d1Percentage * ofInterest.getLengths().getX() / 2.0 + ofInterest.getRepresentation().getCenter().getX()), //repeat for daughter2
					ofInterest.getRepresentation().getCenter().getY(), ofInterest.getRepresentation().getCenter().getZ());
			d2Lengths = new Coordinate((float) ((1-d1Percentage)*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
			daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, d2genes, color2, divisions.get(d2name));
			perCellMutations(daughter1);
			perCellMutations(daughter2);
			changes.cellsRemoved.add(parent); //store parent cell in changes to be removed later
			changes.cellsAdded.add(daughter1); //store daughters in changes to be added to the actual cells hashmap later
			changes.cellsAdded.add(daughter2);
			break;
		case Y: //equivalent cases for y and z
			d1Center = new Coordinate(ofInterest.getRepresentation().getCenter().getX(),
					(float) ((d1Percentage - 1) * (ofInterest.getLengths().getY() / 2.0) + ofInterest.getRepresentation().getCenter().getY()),
					ofInterest.getRepresentation().getCenter().getZ());
			d1Lengths = new Coordinate(ofInterest.getLengths().getX(), (float) (d1Percentage*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
			daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, d1genes, color1, divisions.get(d1name));
			d2Center = new Coordinate(ofInterest.getRepresentation().getCenter().getX(),
					(float) (d1Percentage * ofInterest.getLengths().getY() / 2.0 + ofInterest.getRepresentation().getCenter().getY()),
					ofInterest.getRepresentation().getCenter().getZ());
			d2Lengths = new Coordinate(ofInterest.getLengths().getX(), (float) ((1-d1Percentage)*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
			daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, d2genes, color2, divisions.get(d2name));
			perCellMutations(daughter1);
			perCellMutations(daughter2);
			changes.cellsRemoved.add(parent);
			changes.cellsAdded.add(daughter1);
			changes.cellsAdded.add(daughter2);
			break;
		case Z:
			d1Center = new Coordinate(ofInterest.getRepresentation().getCenter().getX(), ofInterest.getRepresentation().getCenter().getY(),
					(float) ((d1Percentage - 1) * (ofInterest.getLengths().getZ() / 2.0) + ofInterest.getRepresentation().getCenter().getZ()));
			d1Lengths = new Coordinate(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) (d1Percentage*ofInterest.getLengths().getZ()));
			daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, d1genes, color1, divisions.get(d1name));
			d2Center = new Coordinate(ofInterest.getRepresentation().getCenter().getX(), ofInterest.getRepresentation().getCenter().getY(),
					(float) (d1Percentage * ofInterest.getLengths().getZ() / 2.0 + ofInterest.getRepresentation().getCenter().getZ()));
			d2Lengths = new Coordinate(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) ((1-d1Percentage)*ofInterest.getLengths().getZ()));
			daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, d2genes, color2, divisions.get(d2name));
			perCellMutations(daughter1);
			perCellMutations(daughter2);
			changes.cellsRemoved.add(parent);
			changes.cellsAdded.add(daughter1);
			changes.cellsAdded.add(daughter2);
			break;
		}
		return changes;
	}

	/**
	 * Color codes cells based on what par proteins they contain
	 * @param genes The geneslist of the cell to be colored
	 * @return The RGB value of the cell's color
	 */
	public RGB cellColorPars(HashMap<String, Gene> genes){
		int red = 10;
		int green = 10;
		int blue = 10;

		for(String s: genes.keySet()){
			if(s.equals("par-1")){
				red += 80;
				blue += 80;
			}
			if(s.equals("par-2")){
				red += 80;
			}
			if(s.equals("par-3")){
				green += 80;
				blue += 80;
			}
			if(s.equals("par-4")){
				blue += 80;
			}
			if(s.equals("par-5")){
				red += 80;
				green += 80;
			}
			if(s.equals("par-6")){
				green += 80;
			}
		}
		return new RGB(red, green, blue);
	}

	/**
	 * color codes based on lineage, which can be determined from the cell name
	 * @param genes The name of the cell to be colored
	 * @return The RGB value of the cell's color
	 */
	public RGB cellColorLineage(String cellName){
		if(cellName.startsWith("ab-a")) return new RGB(255, 0, 0);
		else if(cellName.startsWith("ab-p")) return new RGB(0, 0, 255);
		else if(cellName.startsWith("e") || cellName.equals("ab")) return new RGB(0, 255, 0);
		else if(cellName.startsWith("ms")) return new RGB(0, 255, 255);
		else if(cellName.startsWith("c")) return new RGB(255, 0, 255);
		else if(cellName.startsWith("d")) return new RGB(200, 70, 150);
		else if(cellName.startsWith("p")) return new RGB(255, 255, 0);
		else return new RGB(255, 255, 255);
	}

	/**
	 * color codes based on cell fate, which is determined by the states of various genes
	 * @param genes The geneslist of the cell to be colored
	 * @return The RGB value of the cell's color
	 */
	public RGB cellColorFate(HashMap<String, Gene> genes){

		boolean germline = true;
		boolean MS = true;
		boolean E = true;
		boolean C = true;
		boolean D = true;
		
		for(Gene g: fateRules.get(CellFate.GERMLINE)){
			if(genes.get(g.getName()) == null){
				germline = false;
			}
			else if(germline){
				germline = genes.get(g.getName()).getState().getState() == g.getState().getState();				
			}
		}
		
		for(Gene g: fateRules.get(CellFate.MS)){
			if(genes.get(g.getName()) == null) MS = false;
			else if(MS){
				MS = genes.get(g.getName()).getState().getState() == g.getState().getState();					
			}
		}
		
		for(Gene g: fateRules.get(CellFate.E)){
			if(genes.get(g.getName()) == null) E = false;
			else if(E){
				E = genes.get(g.getName()).getState().getState() == g.getState().getState();					
			}
		}
		
		for(Gene g: fateRules.get(CellFate.C)){
			if(genes.get(g.getName()) == null) C = false;
			else if(C){
				C = genes.get(g.getName()).getState().getState() == g.getState().getState();					
			}
		}

		for(Gene g: fateRules.get(CellFate.D)){
			if(genes.get(g.getName()) == null) D = false;
			else if(D){
				D = genes.get(g.getName()).getState().getState() == g.getState().getState();					
			}
		}
		
		RGB color = null;
		if(germline) color = mixColors(color, new RGB(166, 216, 84));
		if(MS) color = mixColors(color, new RGB(252, 141, 98));
		if(E) color = mixColors(color, new RGB(255, 217, 47));
		if(C) color = mixColors(color, new RGB(141, 160, 203));
		if(D) color = mixColors(color, new RGB(102, 194, 165));
		if(color == null) color = new RGB(231, 138, 195);
		return color;
	}
	
	public RGB mixColors(RGB color1, RGB color2){
		if(color1 == null) return color2;
		else if(color2 == null) return color1;
		else{
			return new RGB((color1.getRed() + color2.getRed())/2, (color1.getGreen() + color2.getGreen())/2, (color1.getBlue() + color2.getBlue()/2));
		}
	}

	/**
	 * Recolors all cells to match a new color mode
	 */
	public void updateColorMode(){
		for(String s: this.cells.keySet()){
			Cell c = cells.get(s);
			switch(this.colorMode){
			case FATE:
				c.setColor(cellColorFate(c.getGenes()));
				break;
			case LINEAGE:
				c.setColor(cellColorLineage(c.getName()));
				break;
			case PARS:
				c.setColor(cellColorPars(c.getGenes()));
				break;
			}
		}
	}

	/**
	 * Draws all cells present in the shell to the screen as ellipsoids
	 * @deprecated no longer used now that cells are drawn as metaballs
	 */
	@Deprecated
	public void drawAllCells(){
		for(String s: this.cells.keySet()){
			this.cells.get(s).drawCellEllipsoid();
		}
	}

	/**
	 * does any divisions that occur at this timestep then runs per cell timestep function on each cell 
	 */
	public void timeStep(){
		recentGrowth = false;
		ArrayList<CellChangesData> cellChanges = new ArrayList<CellChangesData>();
		for(String s: cells.keySet()){
			Cell c = cells.get(s);
			if(c.getDivide() != null){ //we might not have the division data on the the children, if they divide after gastrulation.
				if(c.getDivide().getTime() == simTime){ //need to get division data from the cell, not what's in Shell's division list, in case of mutations
					recentGrowth = true;
					cellChanges.add(cellDivision(c.getDivide()));
				}
			}
		}
		for(CellChangesData d: cellChanges){
			//remove cells that divided from the shell
			for(String s: d.cellsRemoved){
				cells.remove(s);
			}
			//add their children
			for(Cell c: d.cellsAdded){
				cells.put(c.getName(), c);
			}
		}
		for(String s: this.cells.keySet()){
			cells.put(s, cells.get(s).timeLapse(cells.size(), recentGrowth));		
		}
		//fate color mode is the only one in which cells can change colors between divisions
		//so if we're in fate mode, we must update the colors of the cells in case any have chanegd.
		if(this.colorMode == ColorMode.FATE){
			updateColorMode();
		}
		//if the cell divided, update metaballs in display
		if(recentGrowth){
			window.iterateThroughGrid();
		}
		simTime++;
	}

	/**
	 * Calculates mutations for each cell
	 * @param genes The genes that the cell contains
	 * @return The updated list of genes with mutations calculated
	 */
	public Cell perCellMutations (Cell c){
		//this order is strategic: rules get overwritten in the correct way
		String[] genes = {"par-3", "par-6", "pkc-3", "par-2", "par-5", "par-1", "par-4"};
		for(String s: genes){
			if(mutants.get(s)){
				try{
					readMutantRules("src/components/mutantRules.csv", c, s);
				}
				catch(Exception e){
					//for java applet or executable
					try{
						readMutantRules("mutantRules.csv", c, s);
					}
					catch(Exception f){
						System.out.println("Couldn't find mutantRules.csv at either of the expected locations.");
					}
				}
			}
		}
		return c;
	}
	/**
	 * Calculates mutations for the overall shell
	 */
	public void perShellMutations(){
		Random r = new Random();
		int mutateTimes = 0; //times mutate if there are any mutant pars
		int mutatePercentages = 0; //percentages mutate if anything is mutant except par-4
		boolean par4 = false;
		for(String s: mutants.keySet()){ //go through the set of mutants; this contains each of the par's and an associated boolean value indicating whether it is mutant.
			if(mutants.get(s)){ //if a par is mutant...
				mutateTimes++; //count it
				if(!s.equals("par-4")) mutatePercentages++; //count towards percentages if not par-4
				else par4 = true; //if par-4, have to do special rule
			}
		}
		if(mutatePercentages > 0){ //if there was at least one mutant par (not counting par-4)...
			for(String s: divisions.keySet()){ //look at each division in the events queue...
				DivisionData d = divisions.get(s); //get the relevant data
				int chosen = r.nextInt(20) + 40; //random number between 40 and 60
				divisions.put(s, d.setD1Percentage(chosen/100.0)); //set the d1percentage to something between .4 and .6
			}
		}
		else{
			if(par4){ //if par4 is the only one mutated
				for(String s: divisions.keySet()){
					DivisionData  d = divisions.get(s);
					if(!d.getParent().equals("p-0")){ //apply to all but first cell division
						int chosen = r.nextInt(20) + 40; //random number between 40 and 60
						divisions.put(s, d.setD1Percentage(chosen/100.0)); //set the d1percentage to something between .4 and .6
					}
				}
			}
		}
		if(mutateTimes > 0){ //if there was at least one mutant par...
			HashMap<Integer, LinkedList<Integer>> groupGenerations = new HashMap<Integer, LinkedList<Integer>>(); //will hold all the cell division times of each generation
			for(String s: divisions.keySet()){ //look at each division in the events queue...
				DivisionData d = divisions.get(s); //get the relevant data
				//hashmap keys are the generation that the cell belongs to.
				//we must go through the events queue and find all the times that cells in this generation divide. these get stored in the associated linkedList.
				//then we'll sort the linkedList to find the extremes, and pick a random number within this range
				//then each division in this generation will be set to occur at this same random time.
				if(groupGenerations.keySet().contains(d.getGeneration())){ //if there is already an entry in the hashmap for this generation...
					groupGenerations.get(d.getGeneration()).add(d.getTime());
					//LinkedList<Integer> times = groupGenerations.get(d.getGeneration()); //get the list as it stands now
					//times.add(d.getTime()); //add the new time to it
					//groupGenerations.put(d.getGeneration(), times); //replace list in hashmap with larger list
				}
				else{ //if the entry doesn't exist yet...
					LinkedList<Integer> times = new LinkedList<Integer>(); //initialize with empty list
					times.add(d.getTime());
					groupGenerations.put(d.getGeneration(), times); //initialize it with an empty list.

				}
			}
			for(Integer i: groupGenerations.keySet()){ //now go through  the generations hashmap
				LinkedList<Integer> times = groupGenerations.get(i); //get the associated list
				Collections.sort(times); //sort it
				LinkedList<Integer> chosen = new LinkedList<Integer>();	//new "list" will hold just one number, the randomly chosen one
				if(times.getLast() - times.getFirst() == 0) chosen.add(times.getFirst()); //if there is no variation, just add the only choice. else...
				else{
					int largerTime = times.getFirst(); //we must make sure we don't attempt to set a time for a generation that is sooner than the generation before it
					if(i > 0){
						if(groupGenerations.get(i-1).getFirst()+1 > largerTime){ //if the generation before is dividing late...
							largerTime = groupGenerations.get(i-1).getFirst()+1; //set the start of range of possibilities to the generation before's gen time
						}
					}
					int newTime = r.nextInt(times.getLast() - largerTime) + largerTime; //choose a random number between the smallest and largest time in the list
					chosen.add(newTime);
				}
				groupGenerations.put(i, chosen); //replace entry in hashmap with the chosen number
			}
			for(String s: divisions.keySet()){ //now go through the divisions set
				DivisionData d = divisions.get(s); //get the relevant data
				//each key in groupGenerations corresponds to a generation,
				//and the corresponding list contains only the time that cells in that generation should be set to divide
				divisions.put(s, d.setTime(groupGenerations.get(d.getGeneration()).getFirst()));
			}
		}
	}

	public Cell readMutantRules(String file, Cell c, String mutant) throws FileReadErrorException, InvalidFormatException{
		int column = 0;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			line = reader.readLine();
			String[] mutantInfo = line.split(","); //split into an array using commas as separators
			for(int i = 0; i < mutantInfo.length; i++){ //looking for the column containing the info on this mutant
				if(mutantInfo[i].equals(mutant)) column = i+1;
			}
			if(column == 0){ //if the mutant isn't in the spreadsheet, don't modify the cell
				reader.close();
				return c;
			}

			int row = 2;
			String gene = null;
			Compartment comp = null;
			Coordinate probabilities = new Coordinate(0, 0, 0);
			Random r = new Random();
			int var;
			while((line = reader.readLine()) != null){ //read one row
				String nextInfo = line.split(",")[column-1]; //look at the cell in the pertinent column
				if(c.getGenes().get(nextInfo) != null){ //if it is a gene name in this cell
					gene = nextInfo;
				}
				else if(validGenes.keySet().contains(nextInfo)){ //if it's a valid gene name but just isn't in this cell, we do nothing
					gene = nextInfo;
				}
				else if(nextInfo.equals("absent")){ //if it is an indication of absence...
					if(c.getGenes().get(gene) != null){ //and the gene isn't absent...
						c.getGenes().remove(gene); //remove it.
						c.getRecentlyChanged().remove(gene);
					}
				}
				else if(nextInfo.equals("mislocalized")){ //if it's mislocalized...
					if(c.getGenes().get(gene) != null){
						this.mislocalized.add(gene);
					}
					comp = Compartment.XCENTER; //indicate that the following spreadsheet cell should contain the probability of XCENTER mislocalization
				}
				else{ //the only other valid input is a number
					try{
						float probability = Float.parseFloat(nextInfo); //that number represents the probability of mislocalization in some compartment
						if(c.getGenes().get(gene) != null){
							switch(comp){ //which compartment are we looking at?
							case XCENTER:
								probabilities.setX(probability);
								comp = Compartment.ANTERIOR; //move to next compartment
								break;
							case ANTERIOR:
								probabilities.setY(probability);
								comp = Compartment.POSTERIOR;
								break;
							case POSTERIOR:
								probabilities.setZ(probability);
								var = r.nextInt(200);
								if(var < probabilities.getX() * 2){
									c.getGenes().get(gene).setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
								}
								else if(var < probabilities.getX()*2 + probabilities.getY()*2){
									c.getGenes().get(gene).setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
								}
								else{
									c.getGenes().get(gene).setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
								}
								comp = Compartment.YCENTER;
								break;
							case YCENTER:
								probabilities.setX(probability);
								comp = Compartment.DORSAL;
								break;
							case DORSAL:
								probabilities.setY(probability);
								comp = Compartment.VENTRAL;
								break;
							case VENTRAL:
								probabilities.setZ(probability);
								var = r.nextInt(200);
								Compartment currentX = c.getGenes().get(gene).getLocation().getAP();
								if(var < probabilities.getX() * 2){
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, Compartment.YCENTER, Compartment.ZCENTER));
								}
								else if(var < probabilities.getX()*2 + probabilities.getY()*2){
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, Compartment.DORSAL, Compartment.ZCENTER));
								}
								else{
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, Compartment.VENTRAL, Compartment.ZCENTER));
								}
								comp = Compartment.ZCENTER;
								break;
							case ZCENTER:
								probabilities.setX(probability);
								comp = Compartment.LEFT;
								break;
							case LEFT:
								probabilities.setY(probability);
								comp = Compartment.RIGHT;
								break;
							case RIGHT:
								probabilities.setZ(probability);
								var = r.nextInt(200);
								currentX = c.getGenes().get(gene).getLocation().getAP();
								Compartment currentY = c.getGenes().get(gene).getLocation().getDV();
								if(var < probabilities.getX() * 2){
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, currentY, Compartment.ZCENTER));
								}
								else if(var < probabilities.getX()*2 + probabilities.getY()*2){
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, currentY, Compartment.LEFT));
								}
								else{
									c.getGenes().get(gene).setLocation(new Coordinate(currentX, currentY, Compartment.RIGHT));
								}
								comp = null;
								break;
							}
						}
					}
					catch(NumberFormatException e){
						reader.close();
						throw new InvalidFormatException(FormatProblem.MUTANTCSV, row, column);
					}
				}
				row++;
			}

			reader.close();
			return c;
		}
		catch (FileNotFoundException e){
			throw new FileReadErrorException(file);
		}
		catch (IOException e){
			throw new FileReadErrorException(file);
		}
	}

	/**
	 * @deprecated use readMutantRules instead - this behavior is no longer hard coded
	 * Calculates mutations that occur in a cell due to par1 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public Cell par1Mutations(Cell c){
		c.getGenes().remove("par-1");
		c.getRecentlyChanged().remove("par-1");
		Random r = new Random();
		int var;
		//skn-1 mislocalized
		if(c.getGenes().get("skn-1") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("skn-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("skn-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("skn-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("skn-1");
		}
		//pie-1 degrades
		if(c.getGenes().get("pie-1") != null){
			c.getGenes().remove("pie-1");
			c.getRecentlyChanged().remove("pie-1");
		}
		//glp-1 mislocalized
		if(c.getGenes().get("glp-1") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("glp-1");
		}
		//par-3 mislocalized
		if(c.getGenes().get("par-3") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-3");
		}
		//mex-3 mislocalized
		if(c.getGenes().get("mex-3") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("mex-3").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("mex-3").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("mex-3").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("mex-3");
		}
		//mex-5 mislocalized
		if(c.getGenes().get("mex-5") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("mex-5").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("mex-5").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("mex-5").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("mex-5");
		}
		return c;
	}

	/**
	 * @deprecated use readMutantRules instead - this behavior is no longer hard coded
	 * Calculates mutations that occur in a cell due to par2 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public Cell par2Mutations(Cell c){
		c.getGenes().remove("par-2");
		c.getRecentlyChanged().remove("par-2");
		Random r = new Random();
		int var;
		//glp-1 mislocalized
		if(c.getGenes().get("glp-1") != null){
			var = r.nextInt(200);
			if(var < 126) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 163) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("glp-1");
		}
		//par-1 mislocalized
		if(c.getGenes().get("par-1") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-1");
		}
		//par-3 mislocalized
		if(c.getGenes().get("par-3") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-3");
		}
		return c;
	}

	/**
	 * @deprecated use readMutantRules instead - this behavior is no longer hard coded
	 * Calculates mutations that occur in a cell due to par3, par6, or pkc-3 being mutant (these mutants all behave the same way)
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public Cell par3Mutations(Cell c){
		Random r = new Random();
		int var;
		//par-2 mislocalized
		if(c.getGenes().get("par-2") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-2").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-2").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-2").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-2");
		}
		//par-1 mislocalized
		if(c.getGenes().get("par-1") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-1");
		}
		//glp-1 mislocalized
		if(c.getGenes().get("glp-1") != null){
			var = r.nextInt(200);
			if(var < 46) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 123) c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("glp-1");
		}
		return c;
	}

	/**
	 * @deprecated use readMutantRules instead - this behavior is no longer hard coded
	 * Calculates mutations that occur in a cell due to par4 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public Cell par4Mutations(Cell c){
		c.getGenes().remove("par-4");
		c.getRecentlyChanged().remove("par-4");
		//glp-1 moves to center
		if(c.getGenes().containsKey("glp-1")){
			c.getGenes().get("glp-1").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));	
		}	
		return c;
	}

	/**
	 * @deprecated use readMutantRules instead - this behavior is no longer hard coded
	 * Calculates mutations that occur in a cell due to par5 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public Cell par5Mutations(Cell c){
		c.getGenes().remove("par-5");
		c.getRecentlyChanged().remove("par-5");
		Random r = new Random();
		int var;
		//par-3 mislocalized
		if(c.getGenes().get("par-3") != null){
			var = r.nextInt(200);
			if(var < 190) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else if(var < 195) c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("par-3").setLocation(new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("par-3");
		}
		//mex-5 mislocalized
		if(c.getGenes().get("mex-5") != null){
			var = r.nextInt(100);
			if(var < 72) c.getGenes().get("mex-5").setLocation(new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
			else c.getGenes().get("mex-5").setLocation(new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
			this.mislocalized.add("mex-5");
		}
		return c;
	}

	/**
	 * Produces list of all cell names present in the shell at the current time, to be displayed on the interface
	 * @return The list
	 */
	public String getCellNames(){
		String toReturn = "Cells present:\n";
		for(String s: this.cells.keySet()){
			toReturn += s + "\n";
		}
		return toReturn;
	}

	/**
	 * Checks that the given string is a possible cell name
	 * @param cell name
	 * @return true if the cell name is possible
	 */
	public boolean validCellName(String name){
		LinkedList<String> validPrefixes = new LinkedList<String>();
		validPrefixes.add("p-0");
		validPrefixes.add("ab");
		validPrefixes.add("ems");
		validPrefixes.add("p-2");
		validPrefixes.add("p-1");
		validPrefixes.add("ms");
		validPrefixes.add("e");
		validPrefixes.add("c");
		validPrefixes.add("p-3");
		validPrefixes.add("d");
		validPrefixes.add("p-4");
		if(validPrefixes.contains(name)) return true; //if it's any of the above, it's valid
		for(String s: validPrefixes){
			if(name.startsWith(s)){ //if one of the above is a prefix...
				if(!(name.charAt(s.length()+1) != '-')) return false; // next character must be a hyphen
				String suffix = name.substring(s.length()+1, name.length()); //get the rest of the word
				for(int i = 0; i < suffix.length(); i++){
					char c = suffix.charAt(i);
					if(c != 'a' && c != 'p' && c != 'l' && c != 'r' && c != 'v' && c != 'd') return false; //if the rest is only a/p/l/r/v/d suffixes we'll consider it valid
				}
				return true;
			}
		}
		return false; //anything else is invalid
	}

	/**
	 * Reads info about the events queue from CSV
	 * @param file the name of the CSV as a string
	 * @return The events queue as populated
	 */
	public HashMap<String, DivisionData> readEventsQueue(String file) throws FileNotFoundException, InvalidFormatException{
		HashMap<String, DivisionData> queue = new HashMap<String, DivisionData>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			int row = 1;
			while((line = reader.readLine()) != null){ //read one row
				String[] queueInfo = line.split(","); //split into an array using commas as separators
				String parent = queueInfo[0]; //name of the splitting cell is in first cell of the row
				if(!validCellName(parent)){ //check that the given cell name is a valid C. elegans cell name
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDCELLNAME, row, 0);
				}	
				double d1Percentage;
				try{
					d1Percentage = Integer.parseInt(queueInfo[1])/100.0; //percentage of the volume that goes to daughter1 in second cell of the row (convert to double)
				}
				catch(NumberFormatException e){
					reader.close();
					throw new InvalidFormatException(FormatProblem.EXPECTEDNUMBER, row, 1);
				}
				Axes axis = Axes.X; //axis of split in the third cell
				if(queueInfo[2].equals("Y")) axis = Axes.Y;
				else if(queueInfo[2].equals("Z")) axis = Axes.Z;
				else if(!queueInfo[2].equals("X")){
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDAXIS, row, 2);
				}
				int time;
				try{
					time = Integer.parseInt(queueInfo[3]); //time of split in fourth cell
				}
				catch(NumberFormatException e){
					reader.close();
					throw new InvalidFormatException(FormatProblem.EXPECTEDNUMBER, row, 3);
				}
				queue.put(parent, new DivisionData(parent, d1Percentage, axis, time, calculateGeneration(parent))); //put data into queue
				row++;
			}
			reader.close();
		}
		catch (FileNotFoundException e){
			throw new FileNotFoundException();
		}
		catch (IOException e){
			throw new FileNotFoundException();
		}
		return queue;
	}

	/**
	 * Compiles a list of valid gene C. elegans gene names based on input data from wormbase
	 * @param file the name of the txt file from wormbase
	 */
	public void compileValidGenes(String file) throws FileReadErrorException{
		validGenes = new HashMap<String, Integer>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			while((line = reader.readLine()) != null){ //read one line
				String[] geneInfo = line.split(","); //split line into an array using spaces as separators
				if(geneInfo.length > 2){
					validGenes.put(geneInfo[2], 1);
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e){
			throw new FileReadErrorException(file);
		}
		catch (IOException e){
			throw new FileReadErrorException(file);
		}
	}

}