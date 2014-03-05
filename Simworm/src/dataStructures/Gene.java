package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class Gene /*implements Comparable<Gene>*/{
	private String name;
	private GeneState state; //active or inactive
	//concentration data affects the genes but we don't have enough information in order to populate this in a useful way.
	//uncomment if concentration data is ever found
	//private double concentration;
	private List<Consequence> relevantCons; //consequences that involve this gene as an antecedent
	Coordinates location; //compartment in cell
	
	public Gene(String name, GeneState state, /*double concentration,*/ Coordinates location){
		this.name = name;
		this.state = state;		
		//this.concentration = concentration;
		this.location = location;
	}
	
	//constructor for a "simple gene" which doesn't hold as much info - used to avoid storing excess info in antecedents and consequences
	public Gene(String name, GeneState state){
		this.name = name;
		this.state = state;
	}
	
	//populates relevantCons
	//must be called before any gene's list of relevant consequences should be used. otherwise it is a "shallow" gene just containing the name/isOn/concentration/location info
	//only the gene instances that are in a cell's gene list generally need to call this
	public Gene populateCons(){
		relevantCons = new ArrayList<Consequence>();
		ConsList allCons = new ConsList(); //cannot be a field in Gene to avoid cyclical data
		for(Consequence c: allCons.AandC){
			for(Gene g: c.getAntecedents()){
				if (g.name.equals(this.name)){
					relevantCons.add(c); //genes in consequences contain ONLY NAME AND STATE to avoid cyclical data
				}
			}
		}
		return this;
	}
	
	//updates the relevantCons list to remove consequences whose time period is over, or add new ones whose time periods have begun
	//should be called every time step
	public Gene updateCons(int time){
		int i = 0;
		ConsList allCons = new ConsList();
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(Consequence c: this.relevantCons){
			if(c.getEndStage() > time && c.getEndStage() != 0){
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
	
	//getters
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

	public Gene setState(GeneState state) {
		this.state = state;
		return this;
	}

	public Gene setLocation(Coordinates location) {
		this.location = location;
		return this;
	}
	
	//genes get sorted by concentration
	/*public int compareTo(Gene to){
		if(this.concentration > to.concentration) return 1;
		if(this.concentration == to.concentration) return 0;
		else return -1;
	}*/
}
