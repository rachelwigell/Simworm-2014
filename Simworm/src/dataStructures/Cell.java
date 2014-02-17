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
	
	Coordinates sphereLocation;
	
	public Cell(BasicVisual window, String name, Coordinates center, Coordinates lengths, String parent, HashMap<String, Gene> genes, RGB color, DivisionData divide){
		this.window = window;
		this.name = name;
		this.center = center;
		this.lengths = lengths;
		this.parent = parent;
		this.genes = genes;
		this.color = color;
		this.divide = divide;
		
		for(String s: genes.keySet()){
			recentlyChanged.put(s, genes.get(s));
		}
		//I think everything is fine at the time of cell creation but more pars get added somehow to ab before its children are calculated
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

	//MUST be handed gene instances from inside of cell, as these are the only ones fully initialized past name/state
	//collect changes to be made to cell, get applied at end of function. cascading effects handled on next timestep
	public HashMap<String, Gene> applyCons(){ //use list of genes that have recently changed, to calculate effects
		HashMap<String, Gene> effects = new HashMap<String, Gene>(); //will hold all changes to be made to the cell
		for(String s: recentlyChanged.keySet()){ //look through list of genes that have been changed
			Gene g = recentlyChanged.get(s);
			//System.out.println("checking a new recently changed gene " + s);
			for(Consequence c: g.getRelevantCons()){ //for each, consider the consequences that contain it as an antecedent
				boolean allFulfilled = true; //tracks whether all necessary antecedents for a particular consequence are fulfilled
				//System.out.println("checking a new consequence");
				checkAnte:
				for(Gene a: c.getAntecedents()){ //look at the antecedents for a particular consequence
					if(genes.get(a.getName()) == null){
						allFulfilled = false;
						//System.out.println("\tan antecedent isn't present");
						break checkAnte; // if cell doesn't contain this antecedent, stop immediately
					}
					if(genes.get(a.getName()).getState().isUnknown()){
						allFulfilled = false;
						//System.out.println("\tan antecedent is unknown");
						break checkAnte; //if any gene in the antecedents is unknown, stop immediately
					}
					if(genes.get(c.getConsequence().getName()) == null){
						allFulfilled = false;
						//System.out.println("\tconsequence gene " + c.getConsequence().getName() + " not contained in " + name);
						break checkAnte; //if cell doesn't contain consequence gene, stop immediately
					}
					//consider removing below if, if systematic way to determine effect "winner" is found - this line causes first to win automatically
					if(effects.get(c.getConsequence().getName()) != null){
						//System.out.println("\t" + c.getConsequence().getName() + " is already going to be applied to " + name);
						allFulfilled = false;
						break checkAnte; // if the effect is already going to be applied, can stop immediately
					}
					if(genes.get(c.getConsequence().getName()).getState().isOn() == c.getConsequence().getState().isOn()){
						allFulfilled = false;
						//System.out.println("\tconsequence gene " + c.getConsequence().getName() + " already set to " + genes.get(c.getConsequence().getName()).getState().isOn() + " in " + name);
						break checkAnte; //if state of the gene is already set here, stop immediately
					}
					if(genes.get(a.getName()).getState().isOn() != a.getState().isOn()){ //check if the state of the gene in the cell is what it must be to fulfill antecedent
						allFulfilled = false; //if any are wrong, set flag. if get to end of for without setting to false, then all antecedents are fulfilled
						//System.out.println("\tantecedent not fulfilled");
						break checkAnte; //stop looking through antecedents as soon as one contradictory one is found
					}
				
				}
				if(allFulfilled){
					//System.out.println("\tputting " + c.getConsequence().getName() + " into " + name);
					effects.put(c.getConsequence().getName(), c.getConsequence().populateCons()); //must populate the relevantCons list for this gene instance since it will be added to the cell
				}
			}
		}
		if(effects.size() != 0){ //if changes were made, we want to add them to the cell
			for(String s: effects.keySet()){ //apply changes to the cell
				//System.out.println(s);
				genes.put(s, effects.get(s));
				
			}
		}
		this.recentlyChanged = effects; //cell must track recent changes for the next call to applyCons
		return effects;
	}
	
	//checks for fulfilled antecedents and applies them
	public Cell timeLapse(int stage){
		for(String s: this.genes.keySet()){
			genes.put(s, genes.get(s).updateCons(stage));
		}
		this.applyCons();
		return this;
	}
	
	public void drawCell(){
		window.displayView.pushMatrix();
		window.displayView.translate(this.center.getX(), this.center.getY(), this.center.getZ());
		float smallSide = this.lengths.getSmallest();
		Coordinates scaling = this.lengths.lengthsToScale();
		window.displayView.scale(scaling.getX(), scaling.getY(), scaling.getZ());
		window.displayView.fill(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
		window.displayView.sphere(smallSide);		
		window.displayView.popMatrix();
	}
	
	public String getInfo(){
		String info = this.name+"\n";
		for(String s: this.genes.keySet()){
			if(this.genes.get(s).getState().isUnknown()) info = info + s + " unknown\n";
			else if(this.genes.get(s).getState().isOn()) info = info + s + " active\n";
			else info = info + s + " inactive\n";
		}
		return info;
	}	
}
