package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class Gene /*implements Comparable<Gene>*/{
	private String name;
	private GeneState state; //active or inactive
	private List<Consequence> relevantCons; //consequences that involve this gene as an antecedent
	Coordinates location; //compartment in cell
	LocationData changes; //optional field for genes that change compartments during the course of the sim
	
	/**
	 * Constructor for genes that don't change compartment
	 * @param name Name of the gene
	 * @param state Active/inactive state of the gene (also can be unknown)
	 * @param location Compartment in which the gene is located
	 */
	public Gene(String name, GeneState state, Coordinates location){
		this.name = name;
		this.state = state;		
		this.location = location;
	}
	
	/**
	 * Constructor for genes that change compartment during the course of development
	 * @param name Name of the gene
	 * @param state Active/inactive state of the gene
	 * @param location  Compartment in which the gene is located
	 * @param changes Info on compartment that the gene moves to and what time the move occurs
	 */
	public Gene(String name, GeneState state, Coordinates location, LocationData changes){
		this.name = name;
		this.state = state;		
		this.location = location;
		this.changes = changes;
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
	 * Populates relevantCons
	 * Must be called before any gene's list of relevant consequences should be used. Otherwise this list will be null.
	 * Only the gene instances that are in a cell's gene list generally need to call this
	 * @return The gene with its populated list
	 */
	public Gene populateCons(){
		relevantCons = new ArrayList<Consequence>();
		ConsList allCons = new ConsList(); //cannot be a field in Gene to avoid cyclical data
		for(Consequence c: allCons.AandC){
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
	 * TODO inefficient now that we're reading from CSV and might not be working exactly right - see testApplyingConsequences
	 * @param time The cell stage that the simulation is currently at (number of cells present)
	 * @return The gene with its relevantCons list updated
	 */
	public Gene updateCons(int time){
		int i = 0;
		ConsList allCons = new ConsList();
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(Consequence c: this.relevantCons){
			if(c.getEndStage() > time && c.getEndStage() != 0){ //0 is what we put for end if we never want the rule to end
				toRemove.add(i);
			}
			i++;
		}
		for(Integer j: toRemove){
			this.relevantCons.remove(j);
		}
		i = 0;
		toRemove = new ArrayList<Integer>();
		for(Consequence c: allCons.startLate){
			if(c.getStartStage() <= time){
				this.relevantCons.add(c);
				toRemove.add(i);
			}
			i++;
		}
		for(Integer j: toRemove){
			allCons.startLate.remove(j);
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

	public Coordinates getLocation() {
		return location;
	}

	public LocationData getChanges() {
		return changes;
	}

	public Gene setState(GeneState state) {
		this.state = state;
		return this;
	}

	public Gene setLocation(Coordinates location) {
		this.location = location;
		return this;
	}
}
