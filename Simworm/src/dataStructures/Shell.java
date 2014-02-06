package dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shell{
	
	private HashMap<String, Cell> cells;
	private Coordinates dimensions;
	private List<DivisionData> divisions = new ArrayList<DivisionData>(); //holds the order in which cells divide, so division can progress in a loop later
	private double volume;
	int simTime = 1;
	
	public Shell(){
		//info about the shell itself
		this.cells = new HashMap<String, Cell>();
		this.dimensions = new Coordinates(50, 30, 30);
		this.volume = 50*30*30;
		
		//info about p-0, which fills the whole shell at the start
		Coordinates startCenter = new Coordinates(25, 15, 15);
		Coordinates startLengths = new Coordinates(50, 30, 30);
		HashMap<String, Gene> startGenes = new HashMap<String, Gene>();
		
		//populate the cell's genes
				startGenes.put("par-1", new Gene("par-1", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("par-2",  new Gene("par-2", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("par-3", new Gene("par-3", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("par-6", new Gene("par-6", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("pkc-3", new Gene("pkc-3", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
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
				startGenes.put("pie-1", new Gene("pie-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("pal-1", new Gene("pal-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("med-1", new Gene("med-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("med-2", new Gene("med-2", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("wrm-1", new Gene("wrm-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("lit-1", new Gene("lit-1", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("pop-1", new Gene("pop-1", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("tbx-37", new Gene("tbx-37", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("mex-3", new Gene("mex-3", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("mom-5",  new Gene("mom-5", new GeneState(false), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("par-4", new Gene("par-4", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());
				startGenes.put("par-5", new Gene("par-5", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER)).populateCons());

		Cell start = new Cell("p-0", startCenter, startLengths, this.volume, null, startGenes);
		this.cells.put(start.getName(), start);
		
		//populate the divisions data
		divisions.add(new DivisionData("p-0", .6, Axes.X, 10));
		divisions.add(new DivisionData("ab", .5, Axes.X, 27));
		divisions.add(new DivisionData("p-1", .6, Axes.X, 28));
		divisions.add(new DivisionData("ab-a", .5, Axes.Z, 45));
		divisions.add(new DivisionData("ab-p", .5, Axes.Z, 45));
		divisions.add(new DivisionData("ems", .6, Axes.X, 49));
		divisions.add(new DivisionData("p-2", .6, Axes.X, 52));
		divisions.add(new DivisionData("ab-al", .5, Axes.X, 60));
		divisions.add(new DivisionData("ab-ar", .5, Axes.X, 60));
		divisions.add(new DivisionData("ab-pl", .5, Axes.X, 60));
		divisions.add(new DivisionData("ab-pr", .5, Axes.X, 65));
		divisions.add(new DivisionData("ms", .5, Axes.X, 68));
		divisions.add(new DivisionData("e", .5, Axes.X, 68));
		divisions.add(new DivisionData("c", .5, Axes.X, 80));
		divisions.add(new DivisionData("ab-ala", .5, Axes.X, 78));
		divisions.add(new DivisionData("ab-alp", .5, Axes.X, 78));
		divisions.add(new DivisionData("ab-ara", .5, Axes.X, 85));
		divisions.add(new DivisionData("ab-arp", .5, Axes.X, 78));
		divisions.add(new DivisionData("ab-pla", .5, Axes.X, 85));
		divisions.add(new DivisionData("ab-plp", .5, Axes.X, 77));
		divisions.add(new DivisionData("ab-pra", .5, Axes.X, 85));
		divisions.add(new DivisionData("ab-prp", .5, Axes.X, 85));
		divisions.add(new DivisionData("p-3", .5, Axes.X, 78));
		divisions.add(new DivisionData("ms-a", .5, Axes.X, 95));
		divisions.add(new DivisionData("ms-p", .5, Axes.X, 95));
	}
	
	public HashMap<String, Cell> getCells() {
		return cells;
	}

	public Coordinates getDimensions() {
		return dimensions;
	}

	public List<DivisionData> getDivisions() {
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
		switch(axis){
		case X:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				if(g.getLocation().getAP() != Compartment.POSTERIOR && daughter1 || g.getLocation().getAP() != Compartment.ANTERIOR && !daughter1){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()));
				}
			}
			break;
		case Y:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				if(g.getLocation().getDV() != Compartment.VENTRAL && daughter1 || g.getLocation().getDV() != Compartment.DORSAL && !daughter1){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()));
				}
			}
			break;
		case Z:
			for(String s: parentGenes.keySet()){
				Gene g = parentGenes.get(s);
				if(g.getLocation().getLR() != Compartment.LEFT && daughter1 || g.getLocation().getLR() != Compartment.RIGHT && !daughter1){
					childGenes.put(g.getName(), new Gene(g.getName(), g.getState(), g.getLocation()));
				}
			}
			break;
		}
		return childGenes;
	}

	//simulates division of cell (currently a cube) by calculating new names, centers, dimensions, and gene states of daughter cells
	//daughter1 is always the more anterior, dorsal, or right child
	public void cellDivision(String parent, double d1Percentage, Axes axis){
		Cell ofInterest = this.cells.get(parent);
		if (ofInterest == null){
			System.out.println("Cell not found");
		}
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
		switch(axis){
			case X:
				d1Center = new Coordinates((d1Percentage - 1) * (ofInterest.getLengths().getX() / 2.0) + ofInterest.getCenter().getX(),
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates(d1Percentage*ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter1 = new Cell(d1name, d1Center, d1Lengths, d1Percentage*ofInterest.getVolume(), parent, d1genes);
				d2Center = new Coordinates(d1Percentage * ofInterest.getLengths().getX() / 2.0 + ofInterest.getCenter().getX(),
						ofInterest.getCenter().getY(), ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates((1-d1Percentage)*ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter2 = new Cell(d2name, d2Center, d2Lengths, (1-d1Percentage)*ofInterest.getVolume(), parent, d2genes);
				this.cells.remove(parent);
				this.cells.put(d1name, daughter1);
				this.cells.put(d2name, daughter2);
				System.out.println("Cell " + parent + " split into " +
				d1name + "  with center point on x axis " + d1Center.getX() + " and length in x direction " + d1Lengths.getX() + " and " +
				d2name + " with center point on x axis " + d2Center.getX() + " and length in x direction " + d2Lengths.getX());
				break;
			case Y:
				d1Center = new Coordinates(ofInterest.getCenter().getX(),
						(d1Percentage - 1) * (ofInterest.getLengths().getY() / 2.0) + ofInterest.getCenter().getY(),
						ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), d1Percentage*ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter1 = new Cell(d1name, d1Center, d1Lengths, d1Percentage*ofInterest.getVolume(), parent, d1genes);
				d2Center = new Coordinates(ofInterest.getCenter().getX(),
						d1Percentage * ofInterest.getLengths().getY() / 2.0 + ofInterest.getCenter().getY(),
						ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), (1-d1Percentage)*ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter2 = new Cell(d2name, d2Center, d2Lengths, (1-d1Percentage)*ofInterest.getVolume(), parent, d2genes);
				this.cells.remove(parent);
				this.cells.put(d1name, daughter1);
				this.cells.put(d2name, daughter2);
				System.out.println("Cell " + parent + " split into " +
						d1name + "  with center point on y axis " + d1Center.getY() + " and length in y direction " + d1Lengths.getY() + " and " +
						d2name + " with center point on y axis " + d2Center.getY() + " and length in y direction " + d2Lengths.getY());
				break;
			case Z:
				d1Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						(d1Percentage - 1) * (ofInterest.getLengths().getZ() / 2.0) + ofInterest.getCenter().getZ());
				d1Lengths = new Coordinates(ofInterest.getLengths().getX(), ofInterest.getLengths().getY(), d1Percentage*ofInterest.getLengths().getZ());
				daughter1 = new Cell(d1name, d1Center, d1Lengths, d1Percentage*ofInterest.getVolume(), parent, d1genes);
				d2Center = new Coordinates(ofInterest.getCenter().getX(), ofInterest.getCenter().getY(),
						d1Percentage * ofInterest.getLengths().getZ() / 2.0 + ofInterest.getCenter().getZ());
				d2Lengths = new Coordinates(ofInterest.getLengths().getX(), (1-d1Percentage)*ofInterest.getLengths().getY(), ofInterest.getLengths().getZ());
				daughter2 = new Cell(d2name, d2Center, d2Lengths, (1-d1Percentage)*ofInterest.getVolume(), parent, d2genes);
				this.cells.remove(parent);
				this.cells.put(d1name, daughter1);
				this.cells.put(d2name, daughter2);
				System.out.println("Cell " + parent + " split into " +
						d1name + "  with center point on z axis " + d1Center.getZ() + " and length in z direction " + d1Lengths.getZ() + " and " +
						d2name + " with center point on z axis " + d2Center.getZ() + " and length in z direction " + d2Lengths.getZ());
				break;
		}		
	}
	
	//runs cell timeStep on each cell and then checks for cell divisions
	public void timeStep(){
		System.out.println("Beginning new time step");
		System.out.println("Beginning check of genes in all cells");
		for(String s: this.cells.keySet()){
			cells.put(s, cells.get(s).timeLapse(cells.size()));		
		}
		System.out.println("Beginning cell divisions, if any");
		for(DivisionData d: divisions){
			if(d.getTime() == simTime){
				cellDivision(d.getParent(), d.getD1Percentage(), d.getAxis());
			}
		}
		simTime++;
	}
}
