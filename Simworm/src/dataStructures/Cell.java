package dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.Coordinates;

import processing.core.*;

public class Cell {
	PApplet window;
	private String name;
	private Coordinates center;
	private Coordinates lengths; //a little misleading, this isn't a set of coordinates, it's the length of the cell in each direction
	private double volume;
	private String parent;
	private HashMap<String, Gene> genes;
	private List<Gene> recentlyChanged = new ArrayList<Gene>();
	
	public Cell(PApplet window, String name, Coordinates center, Coordinates lengths, double volume, String parent, HashMap<String, Gene> genes){
		this.window = window;
		this.name = name;
		this.center = center;
		this.lengths = lengths;
		this.volume = volume;
		this.parent = parent;
		this.genes = genes;
		
		for(String s: genes.keySet()){
			recentlyChanged.add(genes.get(s));
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

	public double getVolume() {
		return volume;
	}

	public HashMap<String, Gene> getGenes() {
		return genes;
	}
	
	//MUST be handed gene instances from inside of cell, as these are the only ones fully initialized past name/state
	//collect changes to be made to cell, get applied at end of function. cascading effects handled on next timestep
	public List<Gene> applyCons(){ //use list of genes that have recently changed, to calculate effects
		ArrayList<Gene> effects = new ArrayList<Gene>(); //will hold all changes to be made to the cell
		for(Gene g: recentlyChanged){ //look through list of genes that have been changed
			for(Consequence c: g.getRelevantCons()){ //for each, consider the consequences that contain it as an antecedent
				boolean allFulfilled = true; //tracks whether all necessary antecedents for a particular consequence are fulfilled
				checkAnte:
				for(Gene a: c.getAntecedents()){ //look at the antecedents for a particular consequence
					if(a.getState().isUnknown()) break checkAnte; //if any gene in the antecedents is unknown, stop immediately
					if(!genes.keySet().contains(a.getName())) break checkAnte; //if the cell doesn't contain one of the antecedent genes, stop immediately
					if(genes.get(a.getName()).getState().isOn() != a.getState().isOn()){ //check if the state of the gene in the cell is what it must be to fulfill antecedent
						allFulfilled = false; //if any are wrong, set flag. if get to end of for without setting to false, then all antecedents are fulfilled
						break checkAnte; //stop looking through antecedents as soon as one contradictory one is found
					}
				
				}
				if(allFulfilled){
					if(!c.getConsequence().getState().isUnknown()){
						System.out.println("\t\t" + c.getConsequence().getName() + " will be set to " + c.getConsequence().getState().isOn());
					}
					effects.add(c.getConsequence().populateCons()); //must populate the relevantCons list for this gene instance since it will be added to the cell
				}
			}
		}
		if(effects.size() != 0){ //if changes were made, we want to add them to the cell
			for(Gene g: effects){ //apply changes to the cell
				genes.put(g.getName(), g);
				
			}
		}
		this.recentlyChanged = effects; //cell must track recent changes for the next call to applyCons
		return effects;
	}
	
	//checks for fulfilled antecedents and applies them
	public Cell timeLapse(int stage){
		System.out.println("\tChecking genes in cell " + this.name);
		for(String s: this.genes.keySet()){
			genes.put(s, genes.get(s).updateCons(stage));
		}
		this.applyCons();
		return this;
	}
	
	public void drawCell(){
		window.pushMatrix();
		window.translate(this.center.getX(), this.center.getY(), this.center.getZ());
		float smallSide = this.lengths.getSmallest();
		Coordinates scaling = this.lengths.lengthsToScale();
		window.scale(scaling.getX(), scaling.getY(), scaling.getZ());
		window.sphere(smallSide);
		window.popMatrix();
	}
	
}
