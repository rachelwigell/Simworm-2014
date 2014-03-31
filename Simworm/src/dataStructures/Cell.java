package dataStructures;

import java.util.HashMap;

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

	public void setColor(RGB color) {
		this.color = color;
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
		window.fill(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
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
}
