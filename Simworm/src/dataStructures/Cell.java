package dataStructures;

import picking.BoundingBox3D;
import picking.VersionException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import dataStructures.Coordinates;
import processing.BasicVisual;

public class Cell {
	BasicVisual window;
	private String name;
	private Coordinates center;
	private Coordinates lengths; //a little misleading, this isn't a set of coordinates, it's the length of the cell in each direction
	private String parent;
	private HashMap<String, Gene> genes;
	private HashMap<String, Gene> recentlyChanged = new HashMap<String, Gene>();
	private RGB color;
	private DivisionData divide;
	private int generation;
	private boolean selected;
	LinkedList<Coordinates> boundingBox;
	
	Coordinates sphereLocation;
	
	/**
	 * Constructor for a cell object
	 * @param window The PApplet where the cell will be displayed
	 * @param name The name of the cell
	 * @param center The coordinates of the center point of the cell
	 * @param lengths The length of the cell on each axis
	 * @param parent The name of this cell's parent (the cell that divides to create this cell)
	 * @param genes The list of genes present in this cell
	 * @param color The color the cell should be rendered in
	 * @param divide The data that will be required to calculate this cell's division
	 * @param generation Generation that the cell belongs to (p-0 is 0th generation, ab and p-1 are first generation, etc)
	 */
	public Cell(BasicVisual window, String name, Coordinates center, Coordinates lengths, String parent, HashMap<String, Gene> genes, RGB color, DivisionData divide, int generation){
		this.window = window;
		this.name = name;
		this.center = center;
		this.lengths = lengths;
		this.parent = parent;
		this.genes = genes;
		this.color = color;
		this.divide = divide;
		this.generation = generation;
		this.selected = true;
		
		allRecentlyChanged();
	}
	
	public Cell(Cell toDup){
		this.window = toDup.window;
		this.name = toDup.name;
		this.center = new Coordinates(toDup.center);
		this.lengths = new Coordinates(toDup.lengths);
		this.parent = toDup.parent;
		HashMap<String, Gene> genesmap = new HashMap<String, Gene>();
		for(String s: toDup.genes.keySet()){
			genesmap.put(s, new Gene(toDup.genes.get(s)));
		}
		this.genes = genesmap;
		this.color = new RGB(toDup.color);
		if(toDup.divide != null){
			this.divide = new DivisionData(toDup.divide);
		}
		this.generation = toDup.generation;
		this.selected = true;
		HashMap<String, Gene> changesmap = new HashMap<String, Gene>();
		for(String s: toDup.recentlyChanged.keySet()){
			changesmap.put(s, new Gene(toDup.recentlyChanged.get(s)));
		}
		this.recentlyChanged = changesmap;
	}
	
	public void allRecentlyChanged(){
		for(String s: genes.keySet()){
			recentlyChanged.put(s, genes.get(s));
		}
	}
	
	//getters	
	public String getName() {
		return name;
	}

	public Coordinates getCenter() {
		return center;
	}

	public Coordinates getLengths() {
		return lengths;
	}

	public String getParent() {
		return parent;
	}

	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public HashMap<String, Gene> getRecentlyChanged() {
		return recentlyChanged;
	}

	public Coordinates getSphereLocation() {
		return sphereLocation;
	}

	public DivisionData getDivide() {
		return divide;
	}

	public int getGeneration() {
		return generation;
	}
	
	public LinkedList<Coordinates> getBoundingBox() {
		return boundingBox;
	}

	public RGB getColor() {
		return color;
	}

	public void setColor(RGB color) {
		this.color = color;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Checks for fulfilled antecedents and applies their consequences. Cascading effects handled on next timestep.
	 * @return The updated list of genes - cell's genelist should be set equal to this result after this method is called.
	 */
	public HashMap<String, Gene> applyCons(){ //use list of genes that have recently changed, to calculate effects
		HashMap<String, Gene> effects = new HashMap<String, Gene>(); //will hold all changes to be made to the cell
		for(String s: recentlyChanged.keySet()){ //look through list of genes that have been changed
			Gene g = genes.get(s);
			//System.out.println("checking a new recently changed gene " + s);
			for(Consequence c: g.getRelevantCons()){ //for each, consider the consequences that contain it as an antecedent
				boolean allFulfilled = true; //tracks whether all necessary antecedents for a particular consequence are fulfilled
				Gene cons = c.getConsequence();
				//System.out.println("checking a new consequence " + cons.getName());
				checkAnte:
				for(Gene a: c.getAntecedents()){ //look at the antecedents for a particular consequence
					if(genes.get(a.getName()) == null){
						allFulfilled = false;
						//System.out.println("\tantecedent " + a.getName() + " isn't present");
						break checkAnte; // if cell doesn't contain this antecedent, stop immediately
					}
					//now that we know the antecedent gene exists the genes hashmap, we want to start using the instance from within the cell instead of the one in conslist.
					//the one in conslist might not have all the correct information, especially if there are mutations.
					Gene aInGenes = genes.get(a.getName());
					if(aInGenes.getState().isUnknown()){
						allFulfilled = false;
						//System.out.println("\tantecedent " + a.getName() + " is unknown");
						break checkAnte; //if any gene in the antecedents is unknown, stop immediately
					}
					if(genes.get(cons.getName()) == null){
						allFulfilled = false;
						//System.out.println("\tconsequence gene " + c.getConsequence().getName() + " not contained in " + name);
						break checkAnte; //if cell doesn't contain consequence gene, stop immediately
					}
					//now that we know the consequence gene exists in the genes hashmap, we want to start using the instance from within the cell instead of the one in conslist.
					Gene consInGenes = genes.get(c.getConsequence().getName());
					//consider removing below if, if systematic way to determine effect "winner" is found - this line causes first to win automatically
					if(effects.get(cons.getName()) != null){
						//System.out.println("\t" + cons.getName() + " is already going to be applied to " + name);
						allFulfilled = false;
						break checkAnte; // if an effect is already going to be applied to this gene, can stop immediately
					}
					if(!consInGenes.getState().isUnknown()){
						if(consInGenes.getState().isOn() == cons.getState().isOn()){
							allFulfilled = false;
							//System.out.println("\tconsequence gene " + cons.getName() + " already set to " + cons.getState().isOn() + " in " + name);
							break checkAnte; //if state of the gene is already set here, stop immediately
						}
					}
					if(aInGenes.getState().isOn() != a.getState().isOn()){ //check if the state of the gene in the cell is what it must be to fulfill antecedent
						allFulfilled = false; //if any are wrong, set flag. if get to end of for without setting to false, then all antecedents are fulfilled
						//System.out.println("\tantecedent " + a.getName() + " not fulfilled");
						//System.out.println("\t\tneeds to be " + a.getState().isOn() + " but actually is " + aInGenes.getState().isOn());
						break checkAnte; //stop looking through antecedents as soon as one contradictory one is found
					}
					Coordinates compartment = consInGenes.getLocation(); //holds location of the consequence gene
					//check that antecedent is in the same compartment as the consequence - by making sure that all antecedents match the consequence,
					//we also make sure that all antecedents are in the same compartment
					//only a problem if we're not in the center
					if(compartment.getAP() != Compartment.XCENTER || compartment.getDV() != Compartment.YCENTER || compartment.getLR() != Compartment.ZCENTER){
						if((compartment.getAP() != aInGenes.getLocation().getAP() && aInGenes.getLocation().getAP() != Compartment.XCENTER)
								|| (compartment.getDV() != aInGenes.getLocation().getDV() && aInGenes.getLocation().getDV() != Compartment.YCENTER)
								|| (compartment.getLR() != aInGenes.getLocation().getLR() && aInGenes.getLocation().getLR() != Compartment.ZCENTER)){
							//System.out.println(aInGenes.getName() + " not in right compartment");
							allFulfilled = false; //if not, the two genes can't interact
							break checkAnte;
						}
					}
				}
				if(allFulfilled){
					//System.out.println("\tsetting " + c.getConsequence().getName() + " to " + c.getConsequence().getState().isOn() + " in " + name);
					effects.put(c.getConsequence().getName(), c.getConsequence()); //put into effects to apply
				}
			}
		}
		if(effects.size() != 0){ //if changes were made, we want to add them to the cell
			for(String s: effects.keySet()){ //apply changes to the cell
				//System.out.println(s);
				genes.get(s).setState(effects.get(s).getState());
				
			}
		}
		//now carry out "special" rules involving pie's absence
		if(!this.genes.keySet().contains("pie-1")){
			if(this.genes.keySet().contains("skn-1")){
				this.genes.get("skn-1").setState(new GeneState(true));
				effects.put("skn-1", this.genes.get("skn-1"));
			}
			else if(this.genes.keySet().contains("pal-1")){
				this.genes.get("pal-1").setState(new GeneState(true));
				effects.put("pal-1", this.genes.get("pal-1"));
			}
		}
		this.recentlyChanged = effects; //cell must track recent changes for the next call to applyCons
		return effects;
	}
	
	/**
	 * Per cell effects that occur on a timestep. Updates the relevantCons list and calls applyCons.
	 * @param stage The number of cells present in the shell right now
	 * @param recentGrowth indicates whether the shell has gained new cells since the last timestep
	 * @return The updated cell
	 */
	public Cell timeLapse(int stage, boolean recentGrowth){
		for(String s: this.genes.keySet()){
			genes.put(s, genes.get(s).updateCons(stage, recentGrowth));
		}
		this.applyCons();
		this.selected = true;
		return this;
	}
	
	/**
	 * Draws the cell to the PApplet
	 */
	public void drawCell(){
		window.pushMatrix();
		window.translate(this.center.getX(), this.center.getY(), this.center.getZ());
		float smallSide = this.lengths.getSmallest();
		Coordinates scaling = this.lengths.lengthsToScale();
		window.scale(scaling.getX(), scaling.getY(), scaling.getZ());
		if(selected) window.fill(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
		else window.fill((float) (this.color.getRed()/2.0), (float) (this.color.getGreen()/2.0), (float) (this.color.getBlue()/2.0));
		window.sphere(smallSide);		
		window.popMatrix();
	}
	
	/**
	 * Returns the string that is printed to the screen when information about the cell is requested by the user
	 * @return String containing list of genes and their states
	 */
	public String getInfo(){
		String parsInfo = this.name+" genes present:\n\n";
		String otherInfo = "\n\n";
		for(String s: this.genes.keySet()){	
			if(this.genes.get(s).getName().contains("par")){
				if(this.genes.get(s).getState().isUnknown()) parsInfo += s + " unknown\n";
				else if(this.genes.get(s).getState().isOn()) parsInfo += s + " active\n";
				else parsInfo += s + " inactive\n";
			}
			else{
				if(this.genes.get(s).getState().isUnknown()) otherInfo += s + " unknown\n";
				else if(this.genes.get(s).getState().isOn()) otherInfo += s + " active\n";
				else otherInfo += s + " inactive\n";
			}
			window.fill(255, 255, 255);
		}
		return parsInfo+otherInfo;
	}
	
	/**
	 * Populates the initial gene list from a CSV
	 * @param file the name of the CSV as a string
	 * @return The genes list as populated
	 */
	public HashMap<String, Gene> readGeneInfo(String file, Shell shell) throws FileReadErrorException, InvalidFormatException{
		String name = null;
		GeneState state = null;
		Coordinates location = null;
		HashMap<String, Gene> genes = new HashMap<String, Gene>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			int row = 1;
			while((line = reader.readLine()) != null){ //read one line
				String[] geneInfo = line.split(","); //split line into an array using commas as separators
				name = geneInfo[0]; //name of the gene is in the first cell in the row
				for(int i=0; i<name.length(); i++){ //check that gene name only contains particular characters
					int c = (int) name.charAt(i);
					if((c != 45) && (c < 97 || c > 122) && (c < 48 || c > 57)){ //lower case, numbers, hyphen accepted
						reader.close();
						throw new InvalidFormatException(FormatProblem.INVALIDNAME, row, 0);
					}
				}				
				//second cell is gene state. should only be A, I, U, or a number
				if(geneInfo[1].equals("A")) state = new GeneState(true); //if A, gene set to active
				else if(geneInfo[1].equals("I")) state = new GeneState(false); //I is inactive
				else if(geneInfo[1].equals("U")) state = new GeneState(); //U is unknown indefinitely
				else try{
					state = new GeneState(Integer.parseInt(geneInfo[1])); //if a number, gene set to unknown but will become known at the given time
				}
				catch(NumberFormatException e){ //if it's not A/I/U or a number, it's invalid input
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDSTATE, row, 1);
				}
				//next three cells contain compartment of the gene
				Compartment x = Compartment.XCENTER;
				Compartment y = Compartment.YCENTER;
				Compartment z = Compartment.ZCENTER;
				if(geneInfo[2].equals("anterior")) x = Compartment.ANTERIOR;
				else if(geneInfo[2].equals("posterior")) x = Compartment.POSTERIOR;
				else if(!geneInfo[2].equals("center")){ //if it's anything else, it's invalid input
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, 2);
				}
				if(geneInfo[3].equals("dorsal")) y = Compartment.DORSAL;
				else if(geneInfo[3].equals("ventral")) y = Compartment.VENTRAL;
				else if(!geneInfo[3].equals("center")){
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, 3);
				}
				if(geneInfo[4].equals("left")) z = Compartment.LEFT;
				else if(geneInfo[4].equals("right")) z = Compartment.RIGHT;
				else if(!geneInfo[4].equals("center")){
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, 4);
				}
				location = new Coordinates(x, y, z);
				int i = 0;
				HashMap <String, Coordinates> changes = new HashMap<String, Coordinates>();
				Compartment newX = Compartment.XCENTER;
				Compartment newY = Compartment.YCENTER;
				Compartment newZ = Compartment.ZCENTER;
				String changeTime = "";
				for(String s: geneInfo){ //row might be over now, but if not, the remaining cells hold data for genes that switch compartments during the sim
					//first three are the compartment it switches into
					if(i > 4){ //get past the first few cells not pertaining to switches
						switch(i % 4){
						case 0:
							if(s.equals("left")) newZ = Compartment.LEFT;
							else if(s.equals("right")) newZ = Compartment.RIGHT;
							else if(!s.equals("center")){ //if it's anything else, it's invalid input
								reader.close();
								throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, i);
							}
							changes.put(changeTime, new Coordinates(newX, newY, newZ));
							break;
						case 1:
							changeTime = s; //the division in which the change takes place
							error:
							if(!shell.getDivisions().keySet().contains(s)){ //the cell name must exist in the events queue or...
								for(String cellName: shell.getDivisions().keySet()){ //if it's one of the "end state" cells (ie one that is created but never divides), it won't be in the queue
									DivisionData data = shell.getDivisions().get(cellName);
									if(s.equals(shell.nameCalc(cellName, data.getAxis(), true)) || s.equals(shell.nameCalc(cellName, data.getAxis(), false))){ //so check that it's a valid child of a cell that is in the queue
										break error; //if so, no error is occurring
									}
								}
								reader.close();
								throw new InvalidFormatException(FormatProblem.INVALIDCELL, row, i);
							}
							break;							
						case 2:
							if(s.equals("anterior")) newX = Compartment.ANTERIOR;
							else if(s.equals("posterior")) newX = Compartment.POSTERIOR;
							else if(!s.equals("center")){
								reader.close();
								throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, i);
							}
							break;
						case 3:
							if(s.equals("dorsal")) newY = Compartment.DORSAL;
							else if(s.equals("ventral")) newY = Compartment.VENTRAL;
							else if(!s.equals("center")){
								reader.close();
								throw new InvalidFormatException(FormatProblem.INVALIDCOMPARTMENT, row, i);
							}
							break;
						}
					}
					i++;
				}
				genes.put(name, new Gene(name, state, location, changes).populateCons()); //make a gene with all the info	
				row++;
			}
			reader.close();
		}
		catch (FileNotFoundException e){
			throw new FileReadErrorException(file);
		}
		catch (IOException e){
			throw new FileReadErrorException(file);
		}
		this.genes = genes;
		return genes;
	}
	
	/**
	 * generates the 3D cube bounding the cell, used in object picking
	 * @param shrinkage a number from 0-1 that indicates how much of the actual length we want to use
	 * since the bounding box is necessarily larger than the cell, using the full box (shrinkage == 1) will produce many "false positives"
	 * if we shrink a little bit, we get some false negatives, but far fewer false positives.
	 * I have found that shrinkage == .85 works nicely.
	 * @return the BoundingBox3D object that represents the cube and all of its edgewise relationships
	 */
	public BoundingBox3D genBox(float shrinkage){
		float xLength = this.lengths.getX() * shrinkage; //the length along the x direction of the cell, multiplied by shrinkage to bring it down
		float yLength = this.lengths.getY() * shrinkage;
		float zLength = this.lengths.getZ() * shrinkage;
		
		//generate each of the points of the cube from the cell's center point and its x/y/z lengths
		Coordinates rightTopFront = new Coordinates((float) (this.center.getX() + xLength/2.0), (float) (this.center.getY() + yLength/2.0), (float) (this.center.getZ() + zLength/2.0));
		Coordinates rightTopBack = new Coordinates((float) (this.center.getX() + xLength/2.0), (float) (this.center.getY() + yLength/2.0), (float) (this.center.getZ() - zLength/2.0));
		Coordinates rightBottomFront = new Coordinates((float) (this.center.getX() + xLength/2.0), (float) (this.center.getY() - yLength/2.0), (float) (this.center.getZ() + zLength/2.0));
		Coordinates rightBottomBack = new Coordinates((float) (this.center.getX() + xLength/2.0), (float) (this.center.getY() - yLength/2.0), (float) (this.center.getZ() - zLength/2.0));
		Coordinates leftTopFront = new Coordinates((float) (this.center.getX() - xLength/2.0), (float) (this.center.getY() + yLength/2.0), (float) (this.center.getZ() + zLength/2.0));
		Coordinates leftTopBack = new Coordinates((float) (this.center.getX() - xLength/2.0), (float) (this.center.getY() + yLength/2.0), (float) (this.center.getZ() - zLength/2.0));
		Coordinates leftBottomFront = new Coordinates((float) (this.center.getX() - xLength/2.0), (float) (this.center.getY() - yLength/2.0), (float) (this.center.getZ() + zLength/2.0));
		Coordinates leftBottomBack = new Coordinates((float) (this.center.getX() - xLength/2.0), (float) (this.center.getY() - yLength/2.0), (float) (this.center.getZ() - zLength/2.0));
		
		//call the constructor for BoundingBox3D which handles all the cyclical data
		return new BoundingBox3D(rightTopFront, leftBottomFront, rightBottomBack, rightBottomFront, leftTopFront, rightTopBack, leftBottomBack, leftTopBack); 
	}
	
	/**
	 * orders the points around the outside of cube such that lines could be drawn between the points to form 1 closed polygon
	 * @param points the list of points to be ordered
	 * @return the list of points in order
	 */
	LinkedList<Coordinates> orderPoints(LinkedList<Coordinates> points){
		LinkedList<Coordinates> pointsDup1 = new LinkedList<Coordinates>(); //first we need to duplicate the points list twice; this is a destructive algorithm and, for reasons that will be explained later, it may need to be called up to three times
		LinkedList<Coordinates> pointsDup2 = new LinkedList<Coordinates>();
		
		for(Coordinates t: points){ //populate the two duplicate points lists
			pointsDup1.add(t);
			pointsDup2.add(t);
		}
		
		LinkedList<Coordinates> ordered = new LinkedList<Coordinates>();		
		ordered.add(points.getFirst()); //start with a random point
		points.removeFirst(); //remove it from the list of remaining points
		
		try{
			if(points.size() == 3){ //if there were 4 points (originall) in the points list, call determinePathFour
				ordered = ordered.getFirst().determinePathFour(new LinkedList<Integer>(), points, ordered);
			}
			else if(points.size() == 5){//if there were 6, call determinePathSix. Note that only 4 or 6 points are possible (draw a picture)
				ordered = ordered.getFirst().determinePathSix(0, 0, new LinkedList<Integer>(), points, ordered);
			}
		}
		catch(VersionException e){ //determinepathsix has 3 different versions and sometimes version 0 won't work. in this case, it will throw a version exception
			try{
				ordered = new LinkedList<Coordinates>(); //we'll need to start over with one of the duplicate lists.		
				ordered.add(pointsDup1.getFirst());
				pointsDup1.removeFirst();
				ordered = ordered.getFirst().determinePathSix(1, 0, new LinkedList<Integer>(), pointsDup1, ordered);
			}
			catch(VersionException f){ //if version 1 doesn't work, use version 2. it will work.
				try{
					ordered = new LinkedList<Coordinates>();		
					ordered.add(pointsDup2.getFirst());
					pointsDup2.removeFirst();
					ordered = ordered.getFirst().determinePathSix(2, 0, new LinkedList<Integer>(), pointsDup2, ordered);
				}
				catch(VersionException g){}
			}
		}
		
		this.boundingBox = ordered;
		return ordered;
	}
	
	/**
	 * calls all of the functions necessary to generate the bounding polygon for object picking.
	 * this should be called on a mouse click when picking is about to be run.
	 * @return the cell with all its new parameters populated.
	 */
	public Cell boundSphere(){
		BoundingBox3D theBox = this.genBox((float) .85);
		LinkedList<Coordinates> maxes = this.window.selectMaxPoints(theBox);
		this.orderPoints(maxes);
		return this;
	}
}