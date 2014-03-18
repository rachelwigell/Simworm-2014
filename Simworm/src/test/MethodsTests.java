package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Test;

import processing.BasicVisual;
import dataStructures.Cell;
import dataStructures.CellChangesData;
import dataStructures.Compartment;
import dataStructures.ConsList;
import dataStructures.Coordinates;
import dataStructures.DivisionData;
import dataStructures.Gene;
import dataStructures.GeneState;
import dataStructures.Shell;

public class MethodsTests {
	BasicVisual testVis = new BasicVisual();
	Shell testShell = new Shell(testVis, new HashMap<String, Boolean>());
	
	@Test
	public void firstDivision() {
		ArrayList<CellChangesData> cellChanges = new ArrayList<CellChangesData>();
		HashMap<String, Coordinates> abChanges = new HashMap<String, Coordinates>();
		abChanges.put("pkc-3", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-3", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-6", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("mex-5", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("mex-3", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		HashMap<String, Coordinates> p1Changes = new HashMap<String, Coordinates>();
		p1Changes.put("lgl-1", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		p1Changes.put("skn-1", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		p1Changes.put("pal-1", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		
		DivisionData data = testShell.getDivisions().get("p-0");
		cellChanges.add(testShell.cellDivision(data));
		for(CellChangesData d: cellChanges){
			for(String s: d.cellsRemoved){
				testShell.getCells().remove(s);
			}
			for(Cell c: d.cellsAdded){
				testShell.getCells().put(c.getName(), c);
			}
		}
		Cell ab = testShell.getCells().get("ab");
		Cell p1 = testShell.getCells().get("p-1");
		assertTrue(ab.getCenter().getX() == 150 && p1.getCenter().getX() == 400 && ab.getLengths().getX() == 300 && p1.getLengths().getX() == 200);
	}
	
	//@Test no longer works now that we converted to hashmap - doesn't run in order
	public void runEventsQueue(){
		System.out.println("Start events queue: new shell created");
		for(String s: testShell.getDivisions().keySet()){
			DivisionData d = testShell.getDivisions().get(s);
			testShell.cellDivision(d);
		}
	}
	
	@Test
	public void testGeneInitiation(){
		Gene testGene = new Gene("pal-1", new GeneState(true), new Coordinates(Compartment.XCENTER,Compartment.YCENTER,Compartment.ZCENTER));
		testGene.populateCons();
		assertEquals(3, testGene.getRelevantCons().size());
	}
	
	@Test
	public void testApplyingConsequences(){
		Cell testCell = testShell.getCells().get("p-0");
		HashMap<String, Gene> effects = testCell.applyCons();
		assertTrue(effects.containsKey("skn-1"));
		assertTrue(effects.containsKey("mom-2"));
		assertTrue(effects.containsKey("pal-1"));
		assertEquals(3, effects.size());
	}
	
	@Test
	public void testTimeLapse(){
		System.out.println("Begin time lapse tests");
		for(int i = 0; i<45; i++){
			testShell.timeStep();
		}
	}
	
	@Test
	public void enumTests(){
		Coordinates testCoor = new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER);
		assertEquals(Compartment.POSTERIOR, testCoor.getAP());
		Gene testGene = new Gene("par-1", new GeneState(true), new Coordinates(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER)).populateCons();
		assertEquals(Compartment.POSTERIOR, testGene.getLocation().getAP());
	}
	
	@Test
	public void randomTests(){
		float prob = (float) .28;
		Random r = new Random();
		int possibilities = Math.round(1/prob);
		assertEquals(4, possibilities);
		int randInt = r.nextInt(possibilities);
		assertTrue(randInt == 0 || randInt == 1 || randInt == 2 || randInt == 3);
	}
	
	@Test
	public void testInheritance(){
		
		
		CellChangesData cellChanges;
		
		assertEquals(1, testShell.getCells().keySet().size());
		int numPars = 0;
		for(String s: testShell.getCells().get("p-0").getGenes().keySet()){
			Gene g = testShell.getCells().get("p-0").getGenes().get(s);
			if(g.getName().contains("par")) numPars++;
		}
		assertEquals(6, numPars);
		DivisionData d = testShell.getDivisions().get("p-0");
		cellChanges = testShell.cellDivision(d);
		
		for(String s: cellChanges.cellsRemoved){
			testShell.getCells().remove(s);
		}
		for(Cell c: cellChanges.cellsAdded){
			testShell.getCells().put(c.getName(), c);
		}
		
		assertEquals(2, testShell.getCells().keySet().size());
		numPars = 0;
		for(String s: testShell.getCells().get("p-1").getGenes().keySet()){
			Gene g = testShell.getCells().get("p-1").getGenes().get(s);
			if(g.getName().contains("par")) numPars++;
		}
		assertEquals(4, numPars);
		numPars = 0;
		for(String s: testShell.getCells().get("ab").getGenes().keySet()){
			Gene g = testShell.getCells().get("ab").getGenes().get(s);
			if(g.getName().contains("par")) numPars++;
		}
		assertEquals(4, numPars);
		d = testShell.getDivisions().get("ab");
		cellChanges = testShell.cellDivision(d);
		
		for(String s: cellChanges.cellsRemoved){
			testShell.getCells().remove(s);
		}
		for(Cell c: cellChanges.cellsAdded){
			testShell.getCells().put(c.getName(), c);
		}
		
		numPars = 0;
		for(String s: testShell.getCells().get("ab-a").getGenes().keySet()){
			Gene g = testShell.getCells().get("ab-a").getGenes().get(s);
			if(g.getName().contains("par")) numPars++;
		}
		assertEquals(4, numPars);
		numPars = 0;
		for(String s: testShell.getCells().get("ab-p").getGenes().keySet()){
			Gene g = testShell.getCells().get("ab-p").getGenes().get(s);
			if(g.getName().contains("par")) numPars++;
		}
		assertEquals(4, numPars);
	}
	
	@Test
	public void instancesTest(){
		Gene g = new Gene("test", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		Gene g2 = g;
		g2.setState(new GeneState(false));
		assertFalse(g.getState().isOn());
		
		Gene h = new Gene("test", new GeneState(true), new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		Gene h2 = new Gene(h.getName(), h.getState(), h.getLocation());
		h2.setState(new GeneState(false));
		assertTrue(h.getState().isOn());
	}
	
	//@Test tests deprecated method
	public void mutationsTest(){
		//Cell c = testShell.getCells().get("p-0");
		//testShell.calcMutation(c);
	}
	
	@Test
	public void perShellMutationTest1(){
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", true);
		mutants.put("par-2", true);
		mutants.put("par-3", true);
		mutants.put("par-4", true);
		mutants.put("par-5", true);
		mutants.put("par-6", false);
		mutants.put("pkc-3",  false);
		Shell mutantShell = new Shell(testVis, mutants);
		mutantShell.perShellMutations();
		Cell c = mutantShell.getCells().get("p-0");
		assertFalse(c.getGenes().containsKey("par-1"));
		assertFalse(c.getGenes().containsKey("par-2"));
		assertFalse(c.getGenes().containsKey("par-3"));
		assertFalse(c.getGenes().containsKey("par-4"));
		assertFalse(c.getGenes().containsKey("par-5"));
		assertTrue(c.getGenes().containsKey("par-6"));
		assertTrue(c.getGenes().containsKey("pkc-3"));
		int gen1Time = 0;
		int gen2Time = 0;
		int gen3Time = 0;
		int gen4Time = 0;
		for(String s: mutantShell.getDivisions().keySet()){
			DivisionData d = mutantShell.getDivisions().get(s);
			//if(d.getParent().equals("p-0")) System.out.println("p-0: " + d.getD1Percentage());
			assertTrue(d.getD1Percentage() >= .4 && d.getD1Percentage() <= .6);
			switch(d.getGeneration()){
			case 1:
				if(gen1Time == 0) gen1Time = d.getTime();
				else assertEquals(gen1Time, d.getTime());
				break;
			case 2:
				if(gen2Time == 0) gen2Time = d.getTime();
				else assertEquals(gen2Time, d.getTime());
				break;
			case 3:
				if(gen3Time == 0) gen3Time = d.getTime();
				else assertEquals(gen3Time, d.getTime());
				break;
			case 4:
				if(gen4Time == 0) gen4Time = d.getTime();
				else assertEquals(gen4Time, d.getTime());
				break;
			}
		}
	}
	
	@Test
	public void perShellMutationTest2(){
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", false);
		mutants.put("par-2", false);
		mutants.put("par-3", false);
		mutants.put("par-4", true);
		mutants.put("par-5", false);
		mutants.put("par-6", false);
		mutants.put("pkc-3",  false);
		Shell mutantShell = new Shell(testVis, mutants);
		mutantShell.perShellMutations();
		Cell c = mutantShell.getCells().get("p-0");
		assertTrue(c.getGenes().containsKey("par-1"));
		assertTrue(c.getGenes().containsKey("par-2"));
		assertTrue(c.getGenes().containsKey("par-3"));
		assertFalse(c.getGenes().containsKey("par-4"));
		assertTrue(c.getGenes().containsKey("par-5"));
		assertTrue(c.getGenes().containsKey("par-6"));
		assertTrue(c.getGenes().containsKey("pkc-3"));
		int gen1Time = 0;
		int gen2Time = 0;
		int gen3Time = 0;
		int gen4Time = 0;
		for(String s: mutantShell.getDivisions().keySet()){
			DivisionData d = mutantShell.getDivisions().get(s);
			if(d.getParent().equals("p-0")) assertTrue(d.getD1Percentage() == .6);
			assertTrue(d.getD1Percentage() >= .4 && d.getD1Percentage() <= .6);
			switch(d.getGeneration()){
			case 1:
				if(gen1Time == 0) gen1Time = d.getTime();
				else assertEquals(gen1Time, d.getTime());
				break;
			case 2:
				if(gen2Time == 0) gen2Time = d.getTime();
				else assertEquals(gen2Time, d.getTime());
				break;
			case 3:
				if(gen3Time == 0) gen3Time = d.getTime();
				else assertEquals(gen3Time, d.getTime());
				break;
			case 4:
				if(gen4Time == 0) gen4Time = d.getTime();
				else assertEquals(gen4Time, d.getTime());
				break;
			}
		}
	}
	
	@Test
	public void perCellMutationsTest(){
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", true);
		mutants.put("par-2", true);
		mutants.put("par-3", true);
		mutants.put("par-4", true);
		mutants.put("par-5", true);
		mutants.put("par-6", false);
		mutants.put("pkc-3",  false);
		Shell mutantShell = new Shell(testVis, mutants);
		mutantShell.perCellMutations(mutantShell.startGenes);
	}
	
	@Test
	public void moreInstancesTests(){
		Coordinates c = null;
		Coordinates d = new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		Coordinates e = new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		c = d;
		assertTrue(c == d); //not primitive - comparing pointers
		assertTrue(c.equals(d));
		assertFalse(c.equals(e));
		assertFalse(c == e);
		assertTrue(c.getX() == e.getX()); //primitive - comparing values
	}
	
	@Test
	public void par1MutantTest(){
		HashMap<String, Gene> genes = testShell.getCells().get("p-0").getGenes();
		genes = testShell.par1Mutations(genes);
		assertTrue(genes.containsKey("skn-1"));
		assertTrue(genes.get("skn-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("skn-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("skn-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("skn-1 " + genes.get("skn-1").getLocation().getAP());
		assertFalse(genes.containsKey("pie-1"));
		assertTrue(genes.containsKey("glp-1"));
		assertTrue(genes.get("glp-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("glp-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("glp-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("glp-1 " + genes.get("glp-1").getLocation().getAP());
		assertTrue(genes.containsKey("par-3"));
		assertTrue(genes.get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + genes.get("par-3").getLocation().getAP());
		assertTrue(genes.containsKey("mex-3"));
		assertTrue(genes.get("mex-3").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("mex-3").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("mex-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-3 " + genes.get("mex-3").getLocation().getAP());
		assertTrue(genes.containsKey("mex-5"));
		assertTrue(genes.get("mex-5").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("mex-5").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("mex-5").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-5 " + genes.get("mex-5").getLocation().getAP());
	}
	
	@Test
	public void par2MutantTest(){
		HashMap<String, Gene> genes = testShell.getCells().get("p-0").getGenes();
		genes = testShell.par2Mutations(genes);
		assertTrue(genes.containsKey("glp-1"));
		assertTrue(genes.get("glp-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("glp-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("glp-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("glp-1 " + genes.get("glp-1").getLocation().getAP());
		assertTrue(genes.containsKey("par-3"));
		assertTrue(genes.get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + genes.get("par-3").getLocation().getAP());
		assertTrue(genes.containsKey("par-1"));
		assertTrue(genes.get("par-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-1 " + genes.get("par-1").getLocation().getAP());
	}
	
	@Test
	public void par3MutantTest(){
		HashMap<String, Gene> genes = testShell.getCells().get("p-0").getGenes();
		genes = testShell.par3Mutations(genes);
		assertTrue(genes.containsKey("glp-1"));
		assertTrue(genes.get("glp-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("glp-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("glp-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("glp-1 " + genes.get("glp-1").getLocation().getAP());
		assertTrue(genes.containsKey("par-2"));
		assertTrue(genes.get("par-2").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-2").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-2").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-2 " + genes.get("par-2").getLocation().getAP());
		assertTrue(genes.containsKey("par-1"));
		assertTrue(genes.get("par-1").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-1").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-1 " + genes.get("par-1").getLocation().getAP());
	}
	
	
	@Test
	public void par4MutantTest(){
		HashMap<String, Gene> genes = testShell.getCells().get("p-0").getGenes();
		genes = testShell.par4Mutations(genes);
		assertTrue(genes.containsKey("glp-1"));
		assertTrue(genes.get("glp-1").getLocation().getAP() == Compartment.XCENTER);
	}
	
	@Test
	public void par5MutantTest(){
		HashMap<String, Gene> genes = testShell.getCells().get("p-0").getGenes();
		testShell.par5Mutations(genes);
		genes = testShell.getCells().get("p-0").getGenes();
		assertTrue(genes.containsKey("mex-5"));
		assertTrue(genes.get("mex-5").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("mex-5").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("mex-5").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-5 " + genes.get("mex-5").getLocation().getAP());
		//boolean mex5AB = true;
		//boolean mex5P1 = true;
		//if(genes.get("mex-5").getLocation().getAP() == Compartment.POSTERIOR) mex5AB = false;
		//if(genes.get("mex-5").getLocation().getAP() == Compartment.ANTERIOR) mex5P1 = false; 
		assertTrue(genes.containsKey("par-3"));
		assertTrue(genes.get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				genes.get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				genes.get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + genes.get("par-3").getLocation().getAP());
		
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", false);
		mutants.put("par-2", false);
		mutants.put("par-3", false);
		mutants.put("par-4", false);
		mutants.put("par-5", true);
		mutants.put("par-6", false);
		mutants.put("pkc-3", false);
		testShell.mutants = mutants;
		CellChangesData d = testShell.cellDivision(testShell.getDivisions().get("p-0"));
		for(String s: d.cellsRemoved){
			testShell.getCells().remove(s);
		}
		for(Cell c: d.cellsAdded){
			testShell.getCells().put(c.getName(), c);
		}
		
		genes = testShell.getCells().get("p-1").getGenes();
		//System.out.println("cell division");
		//if(mex5P1) System.out.println("mex-5 p-1 " + genes.get("mex-5").getLocation().getAP());
		genes = testShell.getCells().get("ab").getGenes();
		//if(mex5AB) System.out.println("mex-5 ab " + genes.get("mex-5").getLocation().getAP());	
	}
	
	@Test 
	public void testInstantiation(){
		//primitive
		Compartment x = Compartment.XCENTER;
		Coordinates test = new Coordinates(x, Compartment.YCENTER, Compartment.ZCENTER);
		x = null;
		assertTrue(test.getAP() == Compartment.XCENTER);
		//non-primitive
		GeneState s = new GeneState(true);
		Gene g = new Gene("test", s);
		s = null;
		assertFalse(g.getState().equals(null));
	}
	
	@Test
	public void testReadingCSV(){
		ConsList list = new ConsList();
		//list.AandC = new ArrayList<Consequence>();
		//list.startLate = new ArrayList<Consequence>();
		//list.readAandCInfo("AandC.csv");
		assertEquals(29, list.AandC.size());
		assertEquals("pal-1", list.AandC.get(0).getConsequence().getName());
		assertEquals("pie-1", list.AandC.get(0).getAntecedents()[0].getName());
		assertEquals("mex-3", list.AandC.get(28).getConsequence().getName());
		assertEquals("mex-3", list.AandC.get(28).getAntecedents()[1].getName());
		assertFalse(list.AandC.get(0).getConsequence().getState().isOn());
		assertTrue(list.AandC.get(28).getAntecedents()[1].getState().isOn());
		assertEquals("pie-1", list.AandC.get(1).getAntecedents()[0].getName());
		Gene g = new Gene("par-6", new GeneState(true), new Coordinates(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER));
		g.populateCons();
	}
	
	@Test
	public void testHashMapInstances(){
		Coordinates testCoor = new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		HashMap<String, Coordinates> test = new HashMap<String, Coordinates>();
		test.put("a", testCoor);
		testCoor = null;
		assertNotNull(test.get("a"));
		
	}
	
	//test push
	//second test
}
