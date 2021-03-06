package dataStructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import processing.BasicVisual;
import processing.Metaball;

public class Cell {
	BasicVisual window;
	private String name;
	private Coordinate lengths; //a little misleading, this isn't a set of coordinates, it's the length of the cell in each direction
	private HashMap<String, Gene> genes;
	private ArrayList<String> recentlyChanged = new ArrayList<String>();
	private RGB displayColor;
	private RGB uniqueColor;
	private DivisionData divide;
	private boolean selected;
	private Metaball representation;
	
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
	public Cell(BasicVisual window, String name, Coordinate center, Coordinate lengths, HashMap<String, Gene> genes, RGB color, DivisionData divide){
		this.window = window;
		this.name = name;
		this.lengths = lengths;
		this.genes = genes;
		this.displayColor = color;
		this.divide = divide;
		this.selected = false;
		this.uniqueColor = new RGB(window.currentColor.getRed(), window.currentColor.getGreen(), window.currentColor.getBlue());
		window.incrementCurrentColor();
		
		createRepresentation(center);
		allRecentlyChanged();
	}
	
	/**
	 * Duplication constructor for a cell
	 * @param toDup a cell object to duplicate
	 */
	public Cell(Cell toDup){
		this.window = toDup.window;
		this.name = toDup.name;
		this.lengths = new Coordinate(toDup.lengths);
		HashMap<String, Gene> genesmap = new HashMap<String, Gene>();
		for(String s: toDup.genes.keySet()){
			genesmap.put(s, new Gene(toDup.genes.get(s)));
		}
		this.genes = genesmap;
		this.displayColor = new RGB(toDup.displayColor);
		this.uniqueColor = new RGB(toDup.uniqueColor);
		if(toDup.divide != null){
			this.divide = new DivisionData(toDup.divide);
		}
		this.selected = false;
		ArrayList<String> changesmap = new ArrayList<String>();
		for(String s: toDup.recentlyChanged){
			changesmap.add(s);
		}
		this.representation = new Metaball(toDup.getRepresentation());
		this.recentlyChanged = changesmap;
	}
	
	/**
	 * Create a metaball to represent this cell
	 * @param center the center point of the metaball representation
	 * @return the metaball
	 */
	public Metaball createRepresentation(Coordinate center){
		float chargeCoefficient =  500;
		Metaball metaball = new Metaball(center.getX(), center.getY(), center.getZ(),
				chargeCoefficient*this.lengths.getX()*this.lengths.getY()*this.lengths.getZ(),
				this.displayColor.getRed(), this.displayColor.getGreen(), this.displayColor.getBlue());
		this.representation = metaball;
		return metaball;
	}
	
	/**
	 * Put all genes in the recently changed list
	 */
	public void allRecentlyChanged(){
		for(String s: genes.keySet()){
			recentlyChanged.add(s);
		}
	}
	
	//getters	
	public Metaball getRepresentation(){
		return representation;
	}
	
	public String getName() {
		return name;
	}

	public Coordinate getLengths() {
		return lengths;
	}

	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public ArrayList<String> getRecentlyChanged() {
		return recentlyChanged;
	}

	public DivisionData getDivide() {
		return divide;
	}

	public RGB getColor() {
		return displayColor;
	}

	public void setColor(RGB color) {
		this.displayColor = color;
		this.representation.setColor(color);
	}

	public boolean isSelected() {
		return selected;
	}
	
	public RGB getUniqueColor() {
		return uniqueColor;
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
		for(Consequence c: window.conslist.antecedentsAndConsequents){ //cells that have rules involving absence should always
			for(Gene g: c.getAntecedents()){ //be considered recently changed, since they can "disappear" on any cell division
				if(g.getState().getState() == GeneStates.NOTPRESENT){
					this.recentlyChanged.add(g.getName());
				}
			}
		}
		for(String s: recentlyChanged){ //look through list of genes that have been changed
			Gene g = genes.get(s);
			if(g == null){
				g = new Gene(s, null, null, new HashMap<String, Coordinate>(), this.window);
				g.populateCons();
			}
			for(Consequence c: g.getRelevantCons()){ //for each, consider the consequences that contain it as an antecedent
				boolean allFulfilled = true; //tracks whether all necessary antecedents for a particular consequence are fulfilled
				Gene cons = c.getConsequence();
				checkAnte:
				for(Gene a: c.getAntecedents()){ //look at the antecedents for a particular consequence
					if(genes.get(a.getName()) == null){
						if(a.getState().getState() != GeneStates.NOTPRESENT){ //unless the antecedent rule is "not present"...
							allFulfilled = false;
							break checkAnte; // if cell doesn't contain this antecedent, stop immediately
						}
					}
					if(genes.get(cons.getName()) == null){
						allFulfilled = false;
						break checkAnte; //if cell doesn't contain consequence gene, stop immediately
					}
					//now that we know the consequence gene exists in the genes hashmap, we want to start using the instance from within the cell instead of the one in conslist.
					Gene consInGenes = genes.get(c.getConsequence().getName());
					//consider removing below if, if systematic way to determine effect "winner" is found - this line causes first to win automatically
					if(effects.get(cons.getName()) != null){
						allFulfilled = false;
						break checkAnte; // if an effect is already going to be applied to this gene, can stop immediately
					}
					if(!consInGenes.getState().isUnknown() && !consInGenes.getState().isNotPresent()){
						if(consInGenes.getState().isOn() == cons.getState().isOn()){
							allFulfilled = false;
							break checkAnte; //if state of the gene is already set here, stop immediately
						}
					}
					//now that we know the antecedent gene exists the genes hashmap, we want to start using the instance from within the cell instead of the one in conslist.
					//the one in conslist might not have all the correct information, especially if there are mutations.
					if(a.getState().getState() != GeneStates.NOTPRESENT){
						Gene aInGenes = genes.get(a.getName());
						if(aInGenes.getState().isUnknown()){
							allFulfilled = false;
							break checkAnte; //if any gene in the antecedents is unknown, stop immediately
						}
						if(aInGenes.getState().isOn() != a.getState().isOn()){ //check if the state of the gene in the cell is what it must be to fulfill antecedent
							allFulfilled = false; //if any are wrong, set flag. if get to end of for without setting to false, then all antecedents are fulfilled
							break checkAnte; //stop looking through antecedents as soon as one contradictory one is found
						}
						Coordinate compartment = consInGenes.getLocation(); //holds location of the consequence gene
						//check that antecedent is in the same compartment as the consequence - by making sure that all antecedents match the consequence,
						//we also make sure that all antecedents are in the same compartment
						//only a problem if we're not in the center
						if(compartment.getAP() != Compartment.XCENTER || compartment.getDV() != Compartment.YCENTER || compartment.getLR() != Compartment.ZCENTER){
							if((compartment.getAP() != aInGenes.getLocation().getAP() && aInGenes.getLocation().getAP() != Compartment.XCENTER)
									|| (compartment.getDV() != aInGenes.getLocation().getDV() && aInGenes.getLocation().getDV() != Compartment.YCENTER)
									|| (compartment.getLR() != aInGenes.getLocation().getLR() && aInGenes.getLocation().getLR() != Compartment.ZCENTER)){
								allFulfilled = false; //if not, the two genes can't interact
								break checkAnte;
							}
						}
					}
					else{ //if the antecedent was that the gene should be absent...
						if(genes.get(a.getName()) != null){ //but it's present...
							if(genes.get(a.getName()).getState().getState() != GeneStates.NOTPRESENT){//and not set to NOTPRESENT...
								allFulfilled = false; //then our antecedent is not fulfilled
								break checkAnte; //can stop looking immediately.
							}
						}
					}
				}
				if(allFulfilled){
					effects.put(c.getConsequence().getName(), c.getConsequence()); //put into effects to apply
				}
			}
		}
		if(effects.size() != 0){ //if changes were made, we want to add them to the cell
			for(String s: effects.keySet()){ //apply changes to the cell
				genes.get(s).setState(effects.get(s).getState());
				
			}
		}
		this.recentlyChanged = new ArrayList<String>();
		this.recentlyChanged.addAll(effects.keySet()); //cell must track recent changes for the next call to applyCons
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
		this.selected = false;
		return this;
	}
	
	/**
	 * Draws the cell to the PApplet as ellipsoid shapes
	 * No longer used now that cells are being represented as metaballs
	 */
	@Deprecated
	public void drawCellEllipsoid(){
		window.pushMatrix();
		window.translate(this.getRepresentation().getCenter().getX(), this.getRepresentation().getCenter().getY(), this.getRepresentation().getCenter().getZ());
		float smallSide = this.lengths.getSmallest();
		Coordinate scaling = this.lengths.lengthsToScale();
		window.scale(scaling.getX(), scaling.getY(), scaling.getZ());
		if(selected) window.fill(this.displayColor.getRed(), this.displayColor.getGreen(), this.displayColor.getBlue());
		else window.fill((float) (this.displayColor.getRed()/2.0), (float) (this.displayColor.getGreen()/2.0), (float) (this.displayColor.getBlue()/2.0));
		window.sphere(smallSide);		
		window.popMatrix();
	}
	
	/**
	 * Draws the cell to the PApplet with its unique color
	 */	
	public void drawCellWithHiddenColor(){
		window.pushMatrix();
		window.noStroke();
		window.translate(this.getRepresentation().getCenter().getX(), this.getRepresentation().getCenter().getY(), this.getRepresentation().getCenter().getZ());
		float smallSide = this.lengths.getSmallest();
		Coordinate scaling = this.lengths.lengthsToScale();
		window.scale(scaling.getX(), scaling.getY(), scaling.getZ());
		window.fill(this.getUniqueColor().getRed(), this.getUniqueColor().getGreen(), this.getUniqueColor().getBlue());
		window.sphere(smallSide);		
		window.popMatrix();
	}
	
	/**
	 * Returns the string that is printed to the screen when information about the cell is requested by the user
	 * @return String containing list of genes and their states
	 */
	public String getInfo(){
		String parsInfo = this.name+" proteins present:\n\n";
		String otherInfo = "\n\n";
		for(String s: this.genes.keySet()){	
			if(this.genes.get(s).getState().getState() == GeneStates.NOTPRESENT) continue;
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
	 * @param shell the shell where these genes will be used
	 * @return The genes list as populated
	 * @throws FileReadErrorException if the given filename cannot be found
	 * @throws InvalidFormatException if there is a formatting problem in the file provided
	 */
	public HashMap<String, Gene> readGeneInfo(String file, Shell shell) throws FileReadErrorException, InvalidFormatException{
		String name = null;
		GeneState state = null;
		Coordinate location = null;
		HashMap<String, Gene> genes = new HashMap<String, Gene>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			int row = 1;
			while((line = reader.readLine()) != null){ //read one line
				String[] geneInfo = line.split(","); //split line into an array using commas as separators
				name = geneInfo[0]; //name of the gene is in the first cell in the row
				if(shell.validGenes.get(name) == null){ //check that the gene name is one listed by wormbase as being valid
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDGENENAME, row, 0);
				}				
				//second cell is gene state. should only be A, I, U, or a number
				if(geneInfo[1].equals("A")) state = new GeneState(GeneStates.ACTIVE); //if A, gene set to active
				else if(geneInfo[1].equals("I")) state = new GeneState(GeneStates.INACTIVE); //I is inactive
				else if(geneInfo[1].equals("U")) state = new GeneState(); //U is unknown indefinitely
				else if(geneInfo[1].equals("N")) state = new GeneState(GeneStates.NOTPRESENT); //if N, gene not present yet (created by transcription later)
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
				location = new Coordinate(x, y, z);
				int i = 0;
				HashMap <String, Coordinate> changes = new HashMap<String, Coordinate>();
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
							changes.put(changeTime, new Coordinate(newX, newY, newZ));
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
				genes.put(name, new Gene(name, state, location, changes, window).populateCons()); //make a gene with all the info	
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
}