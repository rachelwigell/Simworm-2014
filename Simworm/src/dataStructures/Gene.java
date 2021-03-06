package dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.BasicVisual;

public class Gene /*implements Comparable<Gene>*/{
	private String name;
	private GeneState state; //active or inactive
	private List<Consequence> relevantCons; //consequences that involve this gene as an antecedent
	Coordinate location; //compartment in cell
	HashMap<String, Coordinate> changes; //optional field for genes that change compartments during the course of the sim
	BasicVisual window;
	
	/**
	 * Constructor for genes that change compartment during the course of development
	 * @param name Name of the gene
	 * @param state Active/inactive state of the gene
	 * @param location  Compartment in which the gene is located
	 * @param changes Info on compartment that the gene moves to and what time the move occurs
	 * @param window the PApplet this gene will be in
	 */
	public Gene(String name, GeneState state, Coordinate location, HashMap<String, Coordinate> changes, BasicVisual window){
		this.name = name;
		this.state = state;		
		this.location = location;
		this.changes = changes;
		this.window = window;
	}
	
	/**
	 * Cnstructor for a "simple gene" which only has name and state - used to avoid storing excess info in antecedents and consequences which only need name and state
	 * @param name Name of the gene
	 * @param state Active/inactive state of the gene
	 */
	public Gene(String name, GeneState state){
		this.name = name;
		this.state = state;
	}
	
	/**
	 * Duplication constructor for gene objects
	 * @param toDup the Gene to duplicate
	 */
	public Gene(Gene toDup){
		this.name = toDup.name;
		this.state = new GeneState(toDup.state);
		if(toDup.location != null){
			this.location = new Coordinate(toDup.location);
		}
		if(toDup.changes != null){
			HashMap<String, Coordinate> map = new HashMap<String, Coordinate>();
			for(String s: toDup.changes.keySet()){
				map.put(s, new Coordinate(toDup.changes.get(s)));
			}
			this.changes = map;
		}
	}

	/**
	 * Populates relevantCons
	 * Must be called before any gene's list of relevant consequences should be used. Otherwise this list will be null.
	 * Only the gene instances that are in a cell's gene list generally need to call this
	 * @return The gene with its populated list
	 */
	public Gene populateCons(){
		this.relevantCons = new ArrayList<Consequence>();
		ConsequentsList allCons = window.conslist; //cannot be a field in Gene to avoid cyclical data
		for(Consequence c: allCons.antecedentsAndConsequents){
			for(Gene g: c.getAntecedents()){
				if (g.name.equals(this.name)){
					relevantCons.add(c); //genes in consequences contain ONLY NAME AND STATE to avoid cyclical data and redundant data
				}
			}
		}
		return this;
	}
	
	/**
	 * Updates the relevantCons list to remove consequences whose time period is over, or add new ones whose time periods have begun
	 * Should be called every time step
	 * @param stage The cell stage that the simulation is currently at (number of cells present)
	 * @param recentGrowth indicates whether the shell has gained new cells since the last timestep
	 * @return The gene with its relevantCons list updated
	 */
	public Gene updateCons(int stage, boolean recentGrowth){
		int i = 0;
		ConsequentsList allCons = window.conslist;
		
		//go through the relevantCons list and find ones that have expired
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(Consequence c: this.relevantCons){
			if(c.getEndStage() < stage && c.getEndStage() != 0){ //0 is what we put for end if we never want the rule to end
				toRemove.add(i); //compile a list of rules that should be removed
			}
			i++;
		}
		//now actually remove those rules
		for(Integer j: toRemove){
			this.relevantCons.remove(j);
		}
		//now add rules that start late and have just become active on this time step
		if(recentGrowth){ //to prevent duplicate versions of the rule from being added every time step in which the number of cells stays the same
			for(Consequence c: allCons.startLate){
				if(c.getStartStage() == stage){
					this.relevantCons.add(c);
				}
			}
		}
		return this;
	}
	
	//getters and setters
	public String getName() {
		return name;
	}
	
	public GeneState getState() {
		return state;
	}
	
	public List<Consequence> getRelevantCons(){
		return relevantCons;
	}

	public Coordinate getLocation() {
		return location;
	}

	public HashMap<String, Coordinate> getChanges() {
		return changes;
	}

	public Gene setState(GeneState state) {
		this.state = state;
		return this;
	}

	public Gene setLocation(Coordinate location) {
		this.location = location;
		return this;
	}
	
	public Gene setName(String name){
		this.name = name;
		return this;
	}
}