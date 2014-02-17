package dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.BasicVisual;

public class Shell{
	BasicVisual window; // window in which to display the shell
	private HashMap<String, Cell> cells;
	private Coordinates dimensions;
	private HashMap<String, DivisionData> divisions = new HashMap<String, DivisionData>(); //holds the information about when and how cells divide in theory (according to events queue)
	int simTime = 1;
	float mutationProb = 0; // number between 0 and 1 to indicate the probability of a mutation happening at any time
	
	public Shell(BasicVisual window){
		this.window = window;
		
		//info about the shell itself
		this.cells = new HashMap<String, Cell>();
		this.dimensions = new Coordinates(500, 300, 300);
		
		//info about p-0, which fills the whole shell at the start
		Coordinates startCenter = new Coordinates(250, 150, 150);
		Coordinates startLengths = new Coordinates(500, 300, 300);
		HashMap<String, Gene> startGenes = new HashMap<String, Gene>();
		
		//populate the cell's genes
		startGenes.put("par-1", new Gene("par-1", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("par-2",  new Gene("par-2", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("par-3", new Gene("par-3", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("par-4", new Gene("par-4", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("par-5", new Gene("par-5", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("par-6", new Gene("par-6", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pkc-3", new Gene("pkc-3", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mex-5", new Gene("mex-5", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("axp-1", new Gene("axp-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("glp-1", new Gene("glp-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("tbx-38", new Gene("tbx-38", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("ref-1", new Gene("ref-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("skn-1", new Gene("skn-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("MS signal", new Gene("MS signal", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("lag-2", new Gene("lag-2", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("lin-12", new Gene("lin-12", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pha-4", new Gene("pha-4", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mom-3", new Gene("mom-3", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mom-1", new Gene("mom-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mom-2", new Gene("mom-2", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mom-4", new Gene("mom-4", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pie-1", new Gene("pie-1", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pal-1", new Gene("pal-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("med-1", new Gene("med-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("med-2", new Gene("med-2", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("wrm-1", new Gene("wrm-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("lit-1", new Gene("lit-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pop-1", new Gene("pop-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("tbx-37", new Gene("tbx-37", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mex-3", new Gene("mex-3", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("mom-5",  new Gene("mom-5", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("pos-1", new Gene("pos-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
		startGenes.put("lgl-1", new Gene("lgl-1", new GeneState(false), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());

		//populate the divisions data
				divisions.put("p-0", new DivisionData("p-0", .6, Axes.X, 10));
				divisions.put("ab", new DivisionData("ab", .5, Axes.X, 27));
				divisions.put("p-1", new DivisionData("p-1", .6, Axes.X, 28));
				divisions.put("ab-a", new DivisionData("ab-a", .5, Axes.Z, 45));
				divisions.put("ab-p", new DivisionData("ab-p", .5, Axes.Z, 45));
				divisions.put("ems", new DivisionData("ems", .6, Axes.X, 49));
				divisions.put("p-2", new DivisionData("p-2", .6, Axes.X, 52));
				divisions.put("ab-al", new DivisionData("ab-al", .5, Axes.X, 60));
				divisions.put("ab-ar", new DivisionData("ab-ar", .5, Axes.X, 60));
				divisions.put("ab-pl", new DivisionData("ab-pl", .5, Axes.X, 60));
				divisions.put("ab-pr", new DivisionData("ab-pr", .5, Axes.X, 65));
				divisions.put("ms", new DivisionData("ms", .5, Axes.X, 68));
				divisions.put("e", new DivisionData("e", .5, Axes.X, 68));
				divisions.put("c", new DivisionData("c", .5, Axes.X, 80));
				divisions.put("ab-ala", new DivisionData("ab-ala", .5, Axes.X, 78));
				divisions.put("ab-alp", new DivisionData("ab-alp", .5, Axes.X, 78));
				divisions.put("ab-ara", new DivisionData("ab-ara", .5, Axes.X, 85));
				divisions.put("ab-arp", new DivisionData("ab-arp", .5, Axes.X, 78));
				divisions.put("ab-pla", new DivisionData("ab-pla", .5, Axes.X, 85));
				divisions.put("ab-plp", new DivisionData("ab-plp", .5, Axes.X, 77));
				divisions.put("ab-pra", new DivisionData("ab-pra", .5, Axes.X, 85));
				divisions.put("ab-prp", new DivisionData("ab-prp", .5, Axes.X, 85));
				divisions.put("p-3", new DivisionData("p-3", .5, Axes.X, 78));
				divisions.put("ms-a", new DivisionData("ms-a", .5, Axes.X, 95));
				divisions.put("ms-p", new DivisionData("ms-p", .5, Axes.X, 95));
				
				
		
		Cell start = new Cell(this.window, "p-0", startCenter, startLengths, null, startGenes, new RGB(250, 250, 250), divisions.get("p-0"));
		this.cells.put(start.getName(), start);		
	}
	
	public HashMap<String, Cell> getCells() {
		return cells;
	}

	public Coordinates getDimensions() {
		return dimensions;
	}

	public HashMap<String, DivisionData> getDivisions() {
		return divisions;
	}
	
	//determines the name of a daughter cell based on what the parent cell and what axis it's dividing along
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
	
	public HashMap<String, Gene> childGenes(String parent, Axes axis, boolean daughter1){
		HashMap<String, Gene> parentGenes = this.cells.get(parent).getGenes();
		HashMap<String, Gene> childGenes = new HashMap<String, Gene>();
		//System.out.println("calculating inheritance from parent " + parent);
		switch(axis){
		case X:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getAP();
				if((comp != Compartment.POSTERIOR && daughter1) || (comp != Compartment.ANTERIOR && !daughter1)){
					//if(g.getName().contains("par")) System.out.println(g.getName() + " coming from " + parent);
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()).populateCons());
				}
			}
			break;
		case Y:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getDV();
				if((comp != Compartment.VENTRAL && daughter1) || (comp != Compartment.DORSAL && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()).populateCons());
				}
			}
			break;
		case Z:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				Compartment comp = g.getLocation().getLR();
				if((comp != Compartment.LEFT && daughter1) || (comp != Compartment.RIGHT && !daughter1)){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()).populateCons());
				}
			}
			break;
		}
		return childGenes;
	}

	//simulates division of cell by calculating new names, centers, dimensions, and gene states of daughter cells
	//daughter1 is always the more anterior, dorsal, or right child
	public CellChangesData cellDivision(String parent, double d1Percentage, Axes axis){
		Cell ofInterest = this.cells.get(parent);
		Coordinates d1Center;
		Coordinates d1Lengths;
		Coordinates d2Center;
		Coordinates d2Lengths;
		Cell daughter1;
		Cell daughter2;
		String d1name = nameCalc(parent, axis, true);
		String d2name = nameCalc(parent, axis, false);
		HashMap<String, Gene> d1genes = childGenes(parent, axis, true);
		HashMap<String, Gene> d2genes = childGenes(parent, axis, false);
		CellChangesData changes = new CellChangesData(new ArrayList<String>(), new ArrayList<Cell>());
		switch(axis){
			case X:
				d1Center = new Coordinates((float) ((d1Percentage - 1) * (ofInterest.getLengths().getX() / 2.0) + ofInterest.getCenter().getX()),
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates((float) (d1Percentage*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, calcCellColor(d1genes), divisions.get(d1name));
				d2Center = new Coordinates((float) (d1Percentage * ofInterest.getLengths().getX() / 2.0 + ofInterest.getCenter().getX()),
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates((float) ((1-d1Percentage)*ofInterest.getLengths().getX()), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, calcCellColor(d2genes), divisions.get(d2name));
				changes.cellsRemoved.add(parent);
				changes.cellsAdded.add(daughter1);
				changes.cellsAdded.add(daughter2);
//				System.out.println("Cell " + parent + " split into " +
//				d1name + "  with center point on x axis " + d1Center.getX() + " and length in x direction " + d1Lengths.getX() + " and " +
//				d2name + " with center point on x axis " + d2Center.getX() + " and length in x direction " + d2Lengths.getX());
				break;
			case Y:
				d1Center = new Coordinates(ofInterest.getCenter().getX(),
						(float) ((d1Percentage - 1) * (ofInterest.getLengths().getY() / 2.0) + ofInterest.getCenter().getY()),
						ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), (float) (d1Percentage*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, calcCellColor(d1genes), divisions.get(d1name));
				d2Center = new Coordinates(ofInterest.getCenter().getX(),
						(float) (d1Percentage * ofInterest.getLengths().getY() / 2.0 + ofInterest.getCenter().getY()),
						ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), (float) ((1-d1Percentage)*ofInterest.getLengths().getY()), ofInterest.getLengths().getZ());
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, calcCellColor(d1genes), divisions.get(d2name));
				changes.cellsRemoved.add(parent);
				changes.cellsAdded.add(daughter1);
				changes.cellsAdded.add(daughter2);
//				System.out.println("Cell " + parent + " split into " +
//						d1name + "  with center point on y axis " + d1Center.getY() + " and length in y direction " + d1Lengths.getY() + " and " +
//						d2name + " with center point on y axis " + d2Center.getY() + " and length in y direction " + d2Lengths.getY());
				break;
			case Z:
				d1Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						(float) ((d1Percentage - 1) * (ofInterest.getLengths().getZ() / 2.0) + ofInterest.getCenter().getZ()));
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) (d1Percentage*ofInterest.getLengths().getZ()));
				daughter1 = new Cell(this.window, d1name, d1Center, d1Lengths, parent, d1genes, calcCellColor(d1genes), divisions.get(d1name));
				d2Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						(float) (d1Percentage * ofInterest.getLengths().getZ() / 2.0 + ofInterest.getCenter().getZ()));
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), (float) ((1-d1Percentage)*ofInterest.getLengths().getZ()));
				daughter2 = new Cell(this.window, d2name, d2Center, d2Lengths, parent, d2genes, calcCellColor(d2genes), divisions.get(d2name));
				changes.cellsRemoved.add(parent);
				changes.cellsAdded.add(daughter1);
				changes.cellsAdded.add(daughter2);
//				System.out.println("Cell " + parent + " split into " +
//						d1name + "  with center point on z axis " + d1Center.getZ() + " and length in z direction " + d1Lengths.getZ() + " and " +
//						d2name + " with center point on z axis " + d2Center.getZ() + " and length in z direction " + d2Lengths.getZ());
				break;
		}
		return changes;
	}
	
	//function color codes cells based on what par proteins they contain
	public RGB calcCellColor(HashMap<String, Gene> genes){
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
	
	public void drawAllCells(){
		for(String s: this.cells.keySet()){
			this.cells.get(s).drawCell();
		}
	}
	
	//runs cell timeStep on each cell and then checks for cell divisions
	public void timeStep(){
		//System.out.println("Beginning new time step " + simTime);
		//System.out.println("Beginning check of genes in all cells");
		for(String s: this.cells.keySet()){
			cells.put(s, cells.get(s).timeLapse(cells.size()));		
		}
		//System.out.println("Beginning cell divisions, if any");
		ArrayList<CellChangesData> cellChanges = new ArrayList<CellChangesData>();
		for(String s: cells.keySet()){
			Cell c = cells.get(s);
			if(c.getDivide().getTime() == simTime){ //need to get division data from the cell, not what's in Shell's division list, in case of mutations
				cellChanges.add(cellDivision(s, c.getDivide().getD1Percentage(), c.getDivide().getAxis()));
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
		simTime++;
	}
	
	@Deprecated
	//this method is inefficient because it works on a cell that is already calculated, goes through everything, and makes the changes to mutate it.
	//You should not call it. I have moved all of the pieces into other methods such that they can be calculated on the first pass.
	//The function remains because it is still helpful to see all of the pieces together, and read the comments
	public Cell calcMutation(Cell c){
		//if we have turned mutations off, return the cell unchanged
		if(mutationProb == 0) return c;
		//else continue on. mutationProb is a number between 0 and 1 inclusive that represents the probability of a mutation.
		//but random number generation works like rolling dice, what we need is the number of sides the dice should have.
		//this is found by dividing 1 by mutationProb. We need an int, so round it. The probability of action is 1 over the resultant
		int possibilities = Math.round(1/mutationProb);
		Random r = new Random();	
		
		//first, we deal with the possibility of mutated genes
		HashMap<String, Gene> mutatedGenes = new HashMap<String, Gene>(); //create a new hashmap to store the mutated genes
		for(String s: c.getGenes().keySet()){
			Gene toAdd = c.getGenes().get(s); //look at a gene
			//two parts to the if: first, we can only mutate genes that are known
			//secondly, we only act if our dice "roll" a 0. 0 is not arbitrary, it is the only number guaranteed to be within the range of the "roll."
			if(!toAdd.getState().isUnknown() && r.nextInt(possibilities) == 0){
				mutatedGenes.put(s, new Gene(s, new GeneState(!toAdd.getState().isOn()), toAdd.getLocation()).populateCons()); //add the same gene but with opposite state
			}
			else{
				mutatedGenes.put(s, toAdd); //otherwise add the gene unchanged
			}
		}
		
		//calculate color based on the new, mutated genes.
		RGB mutatedColor = calcCellColor(mutatedGenes);
		
		//now deal with the possibility of mutated division data
		DivisionData cData = c.getDivide();
		//check d1/d2 split
		double mutatedPercent = cData.getD1Percentage(); //set equal to the normal percent
		if(r.nextInt(possibilities) == 0){ //get a new random number, division mutation should be entirely independent of gene mutation
			//nextInt(11) will get a random number between 0 and 10 inclusive. Dividing by 10 will get a random number between 0 and 1
			//this is what we want because d1/d2 percentage is expressed on a 0-1 scale in increments of .1
			mutatedPercent = r.nextInt(11)/10.0;
		}
		//if the if does not occur, this value will remain at the normal percent as set earlier
		
		int mutatedTime = cData.getTime(); //much like percentage we start by setting equal to the normal percent and changing it only if the dice demand it
		if(r.nextInt(possibilities) == 0){
			mutatedTime = simTime + r.nextInt(96 - simTime) + 1; //this sets the new time to somewhere between the current time and the end of our events queue
		}
		
		//place all of the parts into a divisiondata structure
		DivisionData mutatedData = new DivisionData(cData.getParent(), mutatedPercent, cData.getAxis(), mutatedTime);
		//and finally place all of the parts into a cell to be returned. the non-mutatable attribute are drawn directly from the original cell.
		return new Cell(c.window, c.getName(), c.getCenter(), c.getLengths(), c.getParent(), mutatedGenes, mutatedColor, mutatedData);
	}
}
