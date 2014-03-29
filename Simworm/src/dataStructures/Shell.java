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
	private Coordinates dimensions;
	private HashMap<String, DivisionData> divisions = new HashMap<String, DivisionData>(); //holds the information about when and how cells divide in theory (according to events queue)
	int simTime = 1;
	public float mutationProb = (float) 0; // number between 0 and 1 to indicate the probability of a mutation happening at any time - NO LONGER USED
	public HashMap<String, Gene> startGenes = new HashMap<String, Gene>(); //genes in p-0
	public HashMap<String, Boolean> mutants = new HashMap<String, Boolean>(); //all of the genes that have the potential to be mutated, and their status (true = mutant)
	public ColorMode colorMode = ColorMode.LINEAGE; //set colorMode to lineage initially
	public boolean recentGrowth = false; //indicates when the shell has recently gained cells
	
	/**
	 * Constructor for a cell - initializes everything
	 * @param window The PApplet in which the shell will be drawn
	 * @param mutants The user's choice for which genes should be mutated in this shell
	 */
	public Shell(BasicVisual window, HashMap<String, Boolean> mutants){
		this.window = window;
		this.mutants = mutants;
		
		//info about the shell itself
		this.cells = new HashMap<String, Cell>();
		this.dimensions = new Coordinates(500, 300, 300);
		
		//info about p-0, which fills the whole shell at the start
		Coordinates startCenter = new Coordinates(250, 150, 150);
		Coordinates startLengths = new Coordinates(500, 300, 300);
		
		//populate the cell's genes from csv file
		startGenes = readGeneInfo("csv/genes.csv");
		
		//populate events queue data from csv file
		divisions = readEventsQueue("csv/eventsQueue.csv");
				
		//calculate mutations
		perShellMutations();
		perCellMutations(startGenes);
		
		//create p-0 with all the info calculated
		Cell start = new Cell(this.window, "p-0", startCenter, startLengths, null, startGenes, new RGB(255, 255, 0), divisions.get("p-0"), 0);
		this.cells.put("p-0", start);		
	}
	
	//getters
	public HashMap<String, Cell> getCells() {
		return cells;
	}

	public Coordinates getDimensions() {
		return dimensions;
	}

	public HashMap<String, DivisionData> getDivisions() {
		return divisions;
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
			if(!(g.getChanges() == null)){ //if this is a gene that changes compartments mid-sim
				if(g.getChanges().getChangeDivision().equals(parent)){ //check if the division it changes during is the one that's occurring
					g.setLocation(g.getChanges().getChangedLocation()); //set new compartment
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
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges()).populateCons()); //add a new instance of the gene to childGenes, and populate its relevantCons list
				}
			}
			break;
		case Y: //equivalent code for y and z
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getDV();
				if((comp != Compartment.VENTRAL && daughter1) || (comp != Compartment.DORSAL && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges()).populateCons());
				}
			}
			break;
		case Z:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getLR();
				if((comp != Compartment.LEFT && daughter1) || (comp != Compartment.RIGHT && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation(), g.getChanges()).populateCons());
				}
			}
			break;
		}
		return childGenes;
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
		Coordinates d1Center;
		Coordinates d1Lengths;
		Coordinates d2Center;
		Coordinates d2Lengths;
		Cell daughter1;
		Cell daughter2;
		String d1name = nameCalc(parent, axis, true);
		String d2name = nameCalc(parent, axis, false);
		HashMap<String, Gene> d1genes = childGenes(parent, axis, true); //call childgenes to determine the genes inherited by daughter1
		d1genes = perCellMutations(d1genes);
		HashMap<String, Gene> d2genes = childGenes(parent, axis, false); //equivalent for daughter2
		d2genes = perCellMutations(d2genes);
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
				d1Center = new Coordinates((float) ((d1Percentage - 1) * (ofInterest.getLengths().getX() / 2.0) + ofInterest.getCenter().getX()),
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ()); //calculations to get the center of daughter1
				d1Lengths = new Coordinates((float) (d1Percentage*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ()); //calculations to get the dimensions of daughter1
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, color1, divisions.get(d1name), ofInterest.getGeneration()+1); //create daughter1 with all the fields we have calculated
				d2Center = new Coordinates((float) (d1Percentage * ofInterest.getLengths().getX() / 2.0 + ofInterest.getCenter().getX()), //repeat for daughter2
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates((float) ((1-d1Percentage)*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, color2, divisions.get(d2name), ofInterest.getGeneration()+1);
				changes.cellsRemoved.add(parent); //store parent cell in changes to be removed later
				changes.cellsAdded.add(daughter1); //store daughters in changes to be added to the actual cells hashmap later
				changes.cellsAdded.add(daughter2);
				break;
			case Y: //equivalent cases for y and z
				d1Center = new Coordinates(ofInterest.getCenter().getX(),
						(float) ((d1Percentage - 1) * (ofInterest.getLengths().getY() / 2.0) + ofInterest.getCenter().getY()),
						ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), (float) (d1Percentage*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, color1, divisions.get(d1name), ofInterest.getGeneration()+1);
				d2Center = new Coordinates(ofInterest.getCenter().getX(),
						(float) (d1Percentage * ofInterest.getLengths().getY() / 2.0 + ofInterest.getCenter().getY()),
						ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), (float) ((1-d1Percentage)*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, color2, divisions.get(d2name), ofInterest.getGeneration()+1);
				changes.cellsRemoved.add(parent);
				changes.cellsAdded.add(daughter1);
				changes.cellsAdded.add(daughter2);
				break;
			case Z:
				d1Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						(float) ((d1Percentage - 1) * (ofInterest.getLengths().getZ() / 2.0) + ofInterest.getCenter().getZ()));
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) (d1Percentage*ofInterest.getLengths().getZ()));
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, color1, divisions.get(d1name), ofInterest.getGeneration()+1);
				d2Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						(float) (d1Percentage * ofInterest.getLengths().getZ() / 2.0 + ofInterest.getCenter().getZ()));
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) ((1-d1Percentage)*ofInterest.getLengths().getZ()));
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, color2, divisions.get(d2name), ofInterest.getGeneration()+1);
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
		else if(cellName.startsWith("d")) return new RGB(200, 100, 200);
		else if(cellName.startsWith("p")) return new RGB(255, 255, 0);
		else return new RGB(255, 255, 255);
	}
	
	/**
	 * color codes based on cell fate, which is determined by the states of various genes
	 * @param genes The geneslist of the cell to be colored
	 * @return The RGB value of the cell's color
	 */
	public RGB cellColorFate(HashMap<String, Gene> genes){
		Gene pie = genes.get("pie-1");
		Gene skn = genes.get("skn-1");
		Gene pal = genes.get("pal-1");
		
		boolean germline = false;
		boolean MSE = false;
		boolean CD = false;
		
		if(pie != null){
			if(!pie.getState().isUnknown()){
				if(pie.getState().isOn()){
					germline = true;
				}
			}
		}
		if(skn != null){
			if(!skn.getState().isUnknown()){
				if(skn.getState().isOn()){
					MSE = true;
				}
			}
		}
		if(pal != null){
			if(!pal.getState().isUnknown()){
				if(pal.getState().isOn()){
					CD = true;
				}
			}
		}
		if(germline && !MSE && !CD) return new RGB(102, 194, 165);
		else if(!germline && MSE && !CD) return new RGB(252, 141, 98);
		else if(!germline && !MSE && CD) return new RGB(141, 160, 203);
		else return new RGB(231, 138, 195); //occurs if no or more than 1 cell fates are satisfied; default situation
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
	 * Draws all cells present in the shell to the screen
	 */
	public void drawAllCells(){
		for(String s: this.cells.keySet()){
			this.cells.get(s).drawCell();
		}
	}
	
	/**
	 * does any divisions that occur at this timestep then runs per cell timestep function on each cell 
	 */
	public void timeStep(){
		recentGrowth = false;
		//System.out.println("Beginning cell divisions, if any");
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
			for(String s: d.cellsRemoved){
				cells.remove(s);
			}
			for(Cell c: d.cellsAdded){
				cells.put(c.getName(), c);
			}
		}
		//System.out.println("Beginning new time step " + simTime);
		//System.out.println("Beginning check of genes in all cells");
		for(String s: this.cells.keySet()){
			cells.put(s, cells.get(s).timeLapse(cells.size(), recentGrowth));		
		}
		//fate color mode the only one in which cells can change colors between divisions
		if(this.colorMode == ColorMode.FATE){
			updateColorMode();
		}
		simTime++;
	}
	
	@Deprecated
	//this is not how mutations actually work in biology...oops, I am just the code monkey
	
	//this method is inefficient because it works on a cell that is already calculated, goes through everything, and makes the changes to mutate it.
	//if simulation becomes slow, you should move all of the pieces into other methods such that they can be calculated on the first pass.
	//The function should remain but be marked as deprecated, because it is still helpful to see all of the pieces together and read the comments
	public Cell calcMutation(Cell c){
		//if we have turned mutations off, return the cell unchanged
		if(mutationProb == 0) return c;
		//else continue on. mutationProb is a number between 0 and 1 inclusive that represents the probability of a mutation.
		//but random number generation works like rolling dice, what we need is the number of sides the dice should have.
		//this is found by dividing 1 by mutationProb. We need an int, so round it. The probability of action is 1 over the resultant
		int possibilities = Math.round(1/mutationProb);
		System.out.println("possibilities is " + possibilities);
		Random r = new Random();	
		
		//first, we deal with the possibility of mutated genes
		HashMap<String, Gene> mutatedGenes = new HashMap<String, Gene>(); //create a new hashmap to store the mutated genes
		System.out.println("start genes");
		for(String s: c.getGenes().keySet()){
			Gene toAdd = c.getGenes().get(s); //look at a gene
			//two parts to the if: first, we can only mutate genes that are known
			//secondly, we only act if our dice "roll" a 0. 0 is not arbitrary, it is the only number guaranteed to be within the range of the "roll."
			int rand = r.nextInt(possibilities);
			System.out.println("\t" + s + " " + toAdd.getState().isOn() + " rolled " + rand);
			if(!toAdd.getState().isUnknown() && rand == 0){
				toAdd.setState(new GeneState(!toAdd.getState().isOn()));
				mutatedGenes.put(s, toAdd); //add the same gene but with opposite state
				System.out.println("\t\tnew state is " + toAdd.getState().isOn());
			}
			else{
				mutatedGenes.put(s, toAdd); //otherwise add the gene unchanged
			}
		}
		
		//calculate color based on the new, mutated genes.
		RGB mutatedColor = cellColorPars(mutatedGenes);
		
		//now deal with the possibility of mutated division data
		DivisionData cData = c.getDivide();
		//check d1/d2 split
		double mutatedPercent = cData.getD1Percentage(); //set equal to the normal percent
		int rand = r.nextInt(possibilities);
		System.out.println("start divisions");
		System.out.println("\t" + c.getName() + " " + c.getDivide().getD1Percentage() + " percent rolled " + rand);
		if(rand == 0){ //get a new random number, division mutation should be entirely independent of gene mutation
			//nextInt(11) will get a random number between 0 and 10 inclusive. Dividing by 10 will get a random number between 0 and 1
			//this is what we want because d1/d2 percentage is expressed on a 0-1 scale in increments of .1
			mutatedPercent = r.nextInt(11)/10.0;
			System.out.println("\t\tnew percent is " + mutatedPercent);
		}
		//if the if does not occur, this value will remain at the normal percent as set earlier
		
		int mutatedTime = cData.getTime(); //much like percentage we start by setting equal to the normal percent and changing it only if the dice demand it
		rand = r.nextInt(possibilities);
		System.out.println("\t" + c.getName() + " " + c.getDivide().getTime() + " time rolled " + rand);
		if(rand == 0){
			mutatedTime = simTime + r.nextInt(96 - simTime) + 1; //this sets the new time to somewhere between the current time and the end of our events queue
			System.out.println("\t\tnew time is " + mutatedTime);
		}
		
		//place all of the parts into a divisiondata structure
		DivisionData mutatedData = new DivisionData(cData.getParent(), mutatedPercent, cData.getAxis(), mutatedTime, c.getGeneration());
		//and finally place all of the parts into a cell to be returned. the non-mutatable attribute are drawn directly from the original cell.
		return new Cell(c.window, c.getName(), c.getCenter(), c.getLengths(), c.getParent(), mutatedGenes, mutatedColor, mutatedData, c.getGeneration());
	}
	
	/**
	 * Calculates mutations for each cell
	 * @param genes The genes that the cell contains
	 * @return The updated list of genes with mutations calculated
	 */
	public HashMap<String, Gene> perCellMutations (HashMap<String, Gene> genes){
		for(String s: mutants.keySet()){ //go through the set of mutants; this contains each of the par's and an associated boolean value indicating whether it is mutant.
			if(mutants.get(s)){ //if a par is mutant...
				genes.remove(s); //remove it from the present genes
				if(s.equals("par-1")){ //check which par it is and apply unique mutations
					genes = par1Mutations(genes);
				}
				else if(s.equals("par-2")){
					genes = par2Mutations(genes);
				}
				else if(s.equals("par-3") || s.equals("par-6") || s.equals("pkc-3")){ //par-3, 6, and pkc-3 all manifest mutations the same way
					genes = par3Mutations(genes);
				}
				else if(s.equals("par-4")){
					genes = par4Mutations(genes);
				}
				else if(s.equals("par-5")){
					genes = par5Mutations(genes);
				}
			}
		}
		return genes;
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
					LinkedList<Integer> times = groupGenerations.get(d.getGeneration()); //get the list as it stands now
					times.add(d.getTime()); //add the new time to it
					groupGenerations.put(d.getGeneration(), times); //replace list in hashmap with larger list
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
				else chosen.add(r.nextInt(times.getLast() - times.getFirst()) + times.getFirst()); //choose a random number between the smallest and largest time in the list
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
	
	/**
	 * Calculates mutations that occur in a cell due to par1 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public HashMap<String, Gene> par1Mutations(HashMap<String, Gene> genes){
		Random r = new Random();
		int var;
		//skn-1 mislocalized
		if(genes.get("skn-1") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("skn-1", genes.get("skn-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("skn-1", genes.get("skn-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("skn-1", genes.get("skn-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//pie-1 degrades
		if(genes.get("pie-1") != null){
			genes.remove("pie-1");
		}
		//glp-1 mislocalized
		if(genes.get("glp-1") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//par-3 mislocalized
		if(genes.get("par-3") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//mex-3 mislocalized
		if(genes.get("mex-3") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("mex-3", genes.get("mex-3").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("mex-3", genes.get("mex-3").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("mex-3", genes.get("mex-3").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//mex-5 mislocalized
		if(genes.get("mex-5") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		return genes;
	}
	
	/**
	 * Calculates mutations that occur in a cell due to par2 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public HashMap<String, Gene> par2Mutations(HashMap<String, Gene> genes){
		Random r = new Random();
		int var;
		//glp-1 mislocalized
		if(genes.get("glp-1") != null){
			var = r.nextInt(100);
			if(var < 63) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			var = r.nextInt(100);
			if(var < 50) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//par-1 mislocalized
		if(genes.get("par-1") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//par-3 mislocalized
		if(genes.get("par-3") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		return genes;
	}
	
	/**
	 * Calculates mutations that occur in a cell due to par3, par6, or pkc-3 being mutant (these mutants all behave the same way)
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public HashMap<String, Gene> par3Mutations(HashMap<String, Gene> genes){
		Random r = new Random();
		int var;
		//par-2 mislocalized
		if(genes.get("par-2") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-2", genes.get("par-2").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-2", genes.get("par-2").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-2", genes.get("par-2").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//par-1 mislocalized
		if(genes.get("par-1") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-1", genes.get("par-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//glp-1 mislocalized
		if(genes.get("glp-1") != null){
			var = r.nextInt(100);
			if(var < 23) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			var = r.nextInt(100);
			if(var < 50) genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		return genes;
	}
	
	/**
	 * Calculates mutations that occur in a cell due to par4 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public HashMap<String, Gene> par4Mutations(HashMap<String, Gene> genes){
		//glp-1 moves to center
		genes.put("glp-1", genes.get("glp-1").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));	
		return genes;
	}
	
	/**
	 * Calculates mutations that occur in a cell due to par5 being mutant
	 * @param genes the cell's genes
	 * @return the updated genes with effects from mutation
	 */
	public HashMap<String, Gene> par5Mutations(HashMap<String, Gene> genes){
		Random r = new Random();
		int var;
		//par-3 mislocalized
		if(genes.get("par-3") != null){
			var = r.nextInt(100);
			if(var < 90) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else if(var < 95) genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("par-3", genes.get("par-3").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		//mex-5 mislocalized
		if(genes.get("mex-5") != null){
			var = r.nextInt(100);
			if(var < 28) genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
			var = r.nextInt(100);
			if(var < 50) genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)));
			else genes.put("mex-5", genes.get("mex-5").setLocation(new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)));
		}
		return genes;
	}
	
	/**
	 * Populates the initial gene list from a CSV
	 * @param file the name of the CSV as a string
	 * @return The genes list as populated
	 */
	public HashMap<String, Gene> readGeneInfo(String file){
		String name = null;
		GeneState state = null;
		Coordinates location = null;
		HashMap<String, Gene> genes = new HashMap<String, Gene>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			while((line = reader.readLine()) != null){ //read one line
				String[] geneInfo = line.split(","); //split line into an array using commas as separators
				name = geneInfo[0]; //name of the gene is in the first cell in the row
				//second cell is gene state. should only be A, I, U, or a number
				if(geneInfo[1].equals("A")) state = new GeneState(true); //if A, gene set to active
				else if(geneInfo[1].equals("I")) state = new GeneState(false); //I is inactive
				else if(geneInfo[1].equals("U")) state = new GeneState(); //U is unknown indefinitely
				else state = new GeneState(Integer.parseInt(geneInfo[1])); //if a number, gene set to unknown but will become known at the given time
				//next three cells contain compartment of the gene
				Compartment x = Compartment.XCENTER;
				Compartment y = Compartment.YCENTER;
				Compartment z = Compartment.ZCENTER;
				if(geneInfo[2].equals("anterior")) x = Compartment.ANTERIOR;
				else if(geneInfo[2].equals("posterior")) x = Compartment.POSTERIOR;
				if(geneInfo[3].equals("dorsal")) y = Compartment.DORSAL;
				else if(geneInfo[3].equals("ventral")) y = Compartment.VENTRAL;
				if(geneInfo[4].equals("left")) z = Compartment.LEFT;
				else if(geneInfo[4].equals("right")) z = Compartment.RIGHT;
				location = new Coordinates(x, y, z);
				if(geneInfo.length > 5){ //row might be over now, but if not, the remaining cells hold data for genes that switch compartments during the sim
					//first three are the compartment it switches into
					Compartment newX = Compartment.XCENTER;
					Compartment newY = Compartment.YCENTER;
					Compartment newZ = Compartment.ZCENTER;
					if(geneInfo[5].equals("anterior")) newX = Compartment.ANTERIOR;
					else if(geneInfo[5].equals("posterior")) newX = Compartment.POSTERIOR;
					if(geneInfo[6].equals("dorsal")) newY = Compartment.DORSAL;
					else if(geneInfo[6].equals("ventral")) newY = Compartment.VENTRAL;
					if(geneInfo[7].equals("left")) newZ = Compartment.LEFT;
					else if(geneInfo[7].equals("right")) newZ = Compartment.RIGHT;
					String changeTime = geneInfo[8]; //ninth is the division in which the change takes place
					LocationData changes = new LocationData(new Coordinates(x, y, z), new Coordinates(newX, newY, newZ), changeTime);
					genes.put(name, new Gene(name, state, location, changes).populateCons()); //make a gene with all the info
				}
				else genes.put(name, new Gene(name, state, location).populateCons()); //make a gene with all the info (different constructor)
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return genes;
	}
	
	
	/**
	 * Reads info about the events queue from CSV
	 * @param file the name of the CSV as a string
	 * @return The events queue as populated
	 */
	public HashMap<String, DivisionData> readEventsQueue(String file){
		HashMap<String, DivisionData> queue = new HashMap<String, DivisionData>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			while((line = reader.readLine()) != null){ //read one row
				String[] queueInfo = line.split(","); //split into an array using commas as separators
				String parent = queueInfo[0]; //name of the splitting cell is in first cell of the row
				double d1Percentage = Integer.parseInt(queueInfo[1])/100.0; //percentage of the volume that goes to daughter1 in second cell of the row (convert to double)
				Axes axis = Axes.X; //axis of split in the third cell
				if(queueInfo[2].equals("Y")) axis = Axes.Y;
				if(queueInfo[2].equals("Z")) axis = Axes.Z;
				int time = Integer.parseInt(queueInfo[3]); //time of split in fourth cell
				int generation = Integer.parseInt(queueInfo[4]); //generation of the splitting cell in fifth row
				queue.put(parent, new DivisionData(parent, d1Percentage, axis, time, generation)); //put data into queue
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return queue;
	}
	
}
