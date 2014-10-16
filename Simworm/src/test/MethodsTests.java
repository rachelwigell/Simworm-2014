package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import processing.BasicVisual;
import dataStructures.Cell;
import dataStructures.CellChangesData;
import dataStructures.Compartment;
import dataStructures.ConsequentsList;
import dataStructures.Coordinate;
import dataStructures.DivisionData;
import dataStructures.Gene;
import dataStructures.GeneState;
import dataStructures.GeneStates;
import dataStructures.InvalidFormatException;
import dataStructures.Shell;

public class MethodsTests {
	BasicVisual testVis = new BasicVisual();
	HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
	Shell testShell;

	@Before
	public void init(){
		mutants.put("par-1", false);
		mutants.put("par-2", false);
		mutants.put("par-3", false);
		mutants.put("par-4", false);
		mutants.put("par-5", false);
		mutants.put("par-6", false);
		mutants.put("pkc-3", false);
		testShell = new Shell(testVis, mutants);
	}

	//note that many of these tests are sensitive to the current data in the CSV's; altering this data might cause failures

	@Test
	public void firstDivision() {
		ArrayList<CellChangesData> cellChanges = new ArrayList<CellChangesData>();
		HashMap<String, Coordinate> abChanges = new HashMap<String, Coordinate>();
		abChanges.put("pkc-3", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-3", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-6", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("mex-5", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("mex-3", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		HashMap<String, Coordinate> p1Changes = new HashMap<String, Coordinate>();
		p1Changes.put("lgl-1", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		p1Changes.put("skn-1", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		p1Changes.put("pal-1", new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));

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
		assertTrue(ab.getRepresentation().getCenter().getX() == 150 && p1.getRepresentation().getCenter().getX() == 400 && ab.getLengths().getX() == 300 && p1.getLengths().getX() == 200);
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
		Gene testGene = new Gene("pal-1", new GeneState(GeneStates.ACTIVE), new Coordinate(Compartment.XCENTER,Compartment.YCENTER,Compartment.ZCENTER), new HashMap<String, Coordinate>(), testVis);
		testGene.populateCons();
		assertEquals(3, testGene.getRelevantCons().size());
	}

	@Test
	public void testApplyingConsequences(){
		Cell testCell = testShell.getCells().get("p-0");
		HashMap<String, Gene> effects = testCell.applyCons();
		assertTrue(effects.containsKey("skn-1"));
		assertTrue(effects.containsKey("pal-1"));
		assertEquals(2, effects.size());
	}

	@Test
	public void testTimeLapse(){
		//		System.out.println("Begin time lapse tests");
		for(int i = 0; i<45; i++){
			testShell.timeStep();
		}
	}

	@Test
	public void enumTests(){
		Coordinate testCoor = new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER);
		assertEquals(Compartment.POSTERIOR, testCoor.getAP());
		Gene testGene = new Gene("par-1", new GeneState(GeneStates.ACTIVE), new Coordinate(Compartment.POSTERIOR, Compartment.YCENTER, Compartment.ZCENTER), new HashMap<String, Coordinate>(), testVis).populateCons();
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
		Gene g = new Gene("test", new GeneState(GeneStates.ACTIVE), new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER), new HashMap<String, Coordinate>(), testVis);
		Gene g2 = g;
		g2.setState(new GeneState(GeneStates.INACTIVE));
		assertFalse(g.getState().isOn());

		Gene h = new Gene("test", new GeneState(GeneStates.ACTIVE), new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER), new HashMap<String, Coordinate>(), testVis);
		Gene h2 = new Gene(h.getName(), h.getState(), h.getLocation(), h.getChanges(), testVis);
		h2.setState(new GeneState(GeneStates.INACTIVE));
		assertTrue(h.getState().isOn());
	}

	//@Test tests a deprecated method
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
		mutantShell.perCellMutations(mutantShell.getCells().get("p-0"));
	}

	@Test
	public void moreInstancesTests(){
		Coordinate c = null;
		Coordinate d = new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		Coordinate e = new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		c = d;
		assertTrue(c == d); //not primitive - comparing pointers
		assertTrue(c.equals(d));
		assertFalse(c.equals(e));
		assertFalse(c == e);
		assertTrue(c.getX() == e.getX()); //primitive - comparing values
	}

	@Test
	public void par1MutantTest(){
		Cell c = testShell.par1Mutations(testShell.getCells().get("p-0"));
		assertTrue(c.getGenes().containsKey("skn-1"));
		assertTrue(c.getGenes().get("skn-1").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("skn-1").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("skn-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("skn-1 " + c.getGenes().get("skn-1").getLocation().getAP());
		assertFalse(c.getGenes().containsKey("pie-1"));
		//System.out.println("glp-1 " + c.getGenes().get("glp-1").getLocation().getAP());
		assertTrue(c.getGenes().containsKey("par-3"));
		assertTrue(c.getGenes().get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + c.getGenes().get("par-3").getLocation().getAP());
		assertTrue(c.getGenes().containsKey("mex-3"));
		assertTrue(c.getGenes().get("mex-3").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("mex-3").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("mex-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-3 " + c.getGenes().get("mex-3").getLocation().getAP());
		assertTrue(c.getGenes().containsKey("mex-5"));
		assertTrue(c.getGenes().get("mex-5").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("mex-5").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("mex-5").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-5 " + c.getGenes().get("mex-5").getLocation().getAP());
	}

	@Test
	public void par2MutantTest(){
		Cell c = testShell.par2Mutations(testShell.getCells().get("p-0"));
		assertTrue(c.getGenes().containsKey("par-3"));
		assertTrue(c.getGenes().get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + c.getGenes().get("par-3").getLocation().getAP());
		assertTrue(c.getGenes().containsKey("par-1"));
		assertTrue(c.getGenes().get("par-1").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-1").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-1 " + c.getGenes().get("par-1").getLocation().getAP());
	}

	@Test
	public void par3MutantTest(){
		Cell c = testShell.par3Mutations(testShell.getCells().get("p-0"));
		assertTrue(c.getGenes().containsKey("par-2"));
		assertTrue(c.getGenes().get("par-2").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-2").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-2").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-2 " + c.getGenes().get("par-2").getLocation().getAP());
		assertTrue(c.getGenes().containsKey("par-1"));
		assertTrue(c.getGenes().get("par-1").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-1").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-1").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-1 " + c.getGenes().get("par-1").getLocation().getAP());
	}


	@Test
	public void par4MutantTest(){
		testShell.par4Mutations(testShell.getCells().get("p-0"));
	}

	@Test
	public void par5MutantTest(){
		Cell c = testShell.par5Mutations(testShell.getCells().get("p-0"));
		assertTrue(c.getGenes().containsKey("mex-5"));
		assertTrue(c.getGenes().get("mex-5").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("mex-5").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("mex-5").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("mex-5 " + c.getGenes().get("mex-5").getLocation().getAP());
		//boolean mex5AB = true;
		//boolean mex5P1 = true;
		//if(c.getGenes().get("mex-5").getLocation().getAP() == Compartment.POSTERIOR) mex5AB = false;
		//if(c.getGenes().get("mex-5").getLocation().getAP() == Compartment.ANTERIOR) mex5P1 = false; 
		assertTrue(c.getGenes().containsKey("par-3"));
		assertTrue(c.getGenes().get("par-3").getLocation().getAP() == Compartment.ANTERIOR ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.XCENTER ||
				c.getGenes().get("par-3").getLocation().getAP() == Compartment.POSTERIOR);
		//System.out.println("par-3 " + c.getGenes().get("par-3").getLocation().getAP());

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
		for(Cell e: d.cellsAdded){
			testShell.getCells().put(e.getName(), e);
		}

		//genes = testShell.getCells().get("p-1").getGenes();
		//System.out.println("cell division");
		//if(mex5P1) System.out.println("mex-5 p-1 " + genes.get("mex-5").getLocation().getAP());
		//genes = testShell.getCells().get("ab").getGenes();
		//if(mex5AB) System.out.println("mex-5 ab " + genes.get("mex-5").getLocation().getAP());	
	}

	@Test 
	public void testInstantiation(){
		//primitive
		Compartment x = Compartment.XCENTER;
		Coordinate test = new Coordinate(x, Compartment.YCENTER, Compartment.ZCENTER);
		x = null;
		assertTrue(test.getAP() == Compartment.XCENTER);
		//non-primitive
		GeneState s = new GeneState(GeneStates.ACTIVE);
		Gene g = new Gene("test", s);
		s = null;
		assertNotNull(g.getState());
	}

	@Test
	public void testReadingCSV(){
		ConsequentsList list = new ConsequentsList();
		//list.antecedentsAndConsequents = new ArrayList<Consequence>();
		//list.startLate = new ArrayList<Consequence>();
		//list.readantecedentsAndConsequentsInfo("antecedentsAndConsequents.csv");
		assertEquals(18, list.antecedentsAndConsequents.size());
		assertEquals("pal-1", list.antecedentsAndConsequents.get(0).getConsequence().getName());
		assertEquals("pie-1", list.antecedentsAndConsequents.get(0).getAntecedents()[0].getName());
		assertEquals("pal-1", list.antecedentsAndConsequents.get(17).getConsequence().getName());
		assertEquals("skn-1", list.antecedentsAndConsequents.get(17).getAntecedents()[1].getName());
		assertFalse(list.antecedentsAndConsequents.get(0).getConsequence().getState().isOn());
		assertFalse(list.antecedentsAndConsequents.get(17).getAntecedents()[1].getState().isOn());
		assertEquals("pie-1", list.antecedentsAndConsequents.get(1).getAntecedents()[0].getName());
		Gene g = new Gene("par-6", new GeneState(GeneStates.ACTIVE), new Coordinate(Compartment.ANTERIOR, Compartment.YCENTER, Compartment.ZCENTER), new HashMap<String, Coordinate>(), testVis);
		g.populateCons();
	}

	@Test
	public void testHashMapInstances(){
		Coordinate testCoor = new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		HashMap<String, Coordinate> test = new HashMap<String, Coordinate>();
		test.put("a", testCoor);
		testCoor = null;
		assertNotNull(test.get("a"));

	}

	//Note that this test is slow and may very occasionally fail due to random variation	
	//@Test
	public void testMutantVariation1(){
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", true);
		mutants.put("par-2", false);
		mutants.put("par-3", false);
		mutants.put("par-4", false);
		mutants.put("par-5", false);
		mutants.put("par-6", false);
		mutants.put("pkc-3",  false);
		int sknCount = 0;
		int par3Count = 0;
		int mex3Count = 0;
		int mex5Count = 0;
		for(int i = 0; i < 200; i++){
			Shell mutantShell = new Shell(testVis, mutants);
			assertFalse(mutantShell.getCells().get("p-0").getGenes().containsKey("par-1") ||
					mutantShell.getCells().get("p-0").getGenes().containsKey("pie-1"));
			assertTrue(mutantShell.getCells().get("p-0").getGenes().containsKey("par-2") &&
					mutantShell.getCells().get("p-0").getGenes().containsKey("par-3") &&
					mutantShell.getCells().get("p-0").getGenes().containsKey("par-4") &&
					mutantShell.getCells().get("p-0").getGenes().containsKey("par-5") &&
					mutantShell.getCells().get("p-0").getGenes().containsKey("par-6") &&
					mutantShell.getCells().get("p-0").getGenes().containsKey("pkc-3"));
			while(mutantShell.getCells().size() < 2){
				mutantShell.timeStep();
			}
			if(mutantShell.getCells().get("p-1").getGenes().containsKey("skn-1")) sknCount++;
			if(mutantShell.getCells().get("p-1").getGenes().containsKey("par-3")) par3Count++;
			if(mutantShell.getCells().get("p-1").getGenes().containsKey("mex-3")) mex3Count++;
			if(mutantShell.getCells().get("p-1").getGenes().containsKey("mex-5")) mex5Count++;
		}		
		assertNotEquals(200, sknCount);
		assertNotEquals(200, par3Count);
		assertNotEquals(200, mex3Count);
		assertNotEquals(200, mex5Count);
		System.out.println(sknCount + " " + par3Count + " " + mex3Count + " " + mex5Count);
	}


	@Test
	public void testObjectInstantiation(){
		HashMap<Integer, GeneState> map = new HashMap<Integer, GeneState>();
		GeneState s = new GeneState(GeneStates.ACTIVE);
		map.put(1, s);
		assertTrue(map.get(1).isOn());
		s.setOn(false);
		assertFalse(map.get(1).isOn());
	}

	@Test
	public void testObjectInstantiation2(){
		HashMap<Integer, GeneState> map = new HashMap<Integer, GeneState>();
		GeneState s = new GeneState(GeneStates.ACTIVE);
		GeneState t;
		t = s;
		map.put(1, t);
		assertTrue(map.get(1).isOn());
		s.setOn(false);
		assertFalse(map.get(1).isOn());
	}

	@Test
	public void testObjectInstantiation3(){
		HashMap<Integer, Gene> map = new HashMap<Integer, Gene>();
		Gene g = new Gene("name", new GeneState(GeneStates.ACTIVE));
		Gene h = new Gene(g.getName(), g.getState());
		map.put(1, h);
		assertTrue(map.get(1).getState().isOn());
		g.setState(new GeneState(GeneStates.INACTIVE));
		assertTrue(map.get(1).getState().isOn());
	}

	@Test
	public void testObjectInstantiation4(){
		HashMap<Integer, Gene> map = new HashMap<Integer, Gene>();
		Gene g = new Gene("name", new GeneState(GeneStates.ACTIVE));
		Gene h = new Gene(g.getName(), g.getState());
		map.put(1, h);
		assertTrue(map.get(1).getState().isOn());
		g.getState().setOn(false);
		assertFalse(map.get(1).getState().isOn());
	}

	@Test
	public void testObjectInstatiation5(){
		HashMap<Integer, Gene> map = new HashMap<Integer, Gene>();
		Gene g = new Gene("name", new GeneState(GeneStates.ACTIVE));
		Gene h = new Gene(g.getName(), g.getState());
		map.put(1, h);
		g.setName("new");
		assertEquals("name", map.get(1).getName()); 
	}

	@Test
	public void testObjectInstatiation6(){
		Coordinate x = new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		Coordinate y = new Coordinate(x.getAP(), x.getDV(), x.getLR());
		x.setAP(Compartment.ANTERIOR);
		assertNotEquals(x.getAP(), y.getAP());
	}

	@Test
	public void testDupCoord(){
		Coordinate x = new Coordinate(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER);
		Coordinate y = new Coordinate(x);
		x.setAP(Compartment.ANTERIOR);
		assertNotEquals(x.getAP(), y.getAP());
	}

	@Test
	public void testDupCoord2(){
		Coordinate x = new Coordinate(1, 1, 1);
		Coordinate y = new Coordinate(x);
		x.setX(2);
		assertNotEquals(x.getX(), y.getX());
	}

	@Test
	public void testShellDup(){
		Shell aShell = new Shell(testVis, mutants);
		Shell anotherShell = new Shell(aShell);
		aShell.timeStep();
		assertFalse(aShell.getCells().get("p-0").getGenes().get("skn-1").getState().isOn());
		assertTrue(anotherShell.getCells().get("p-0").getGenes().get("skn-1").getState().isOn());
	}

	@Test
	public void testDup(){
		Gene g = new Gene("name", new GeneState(GeneStates.ACTIVE));
		Gene h = g;
		h.setName("new");
		assertEquals("new", g.getName());
	}

	@Test
	public void testDup2(){
		Gene g = new Gene("name", new GeneState(GeneStates.ACTIVE));
		Gene h = new Gene(g.getName(), g.getState());
		h.setName("new");
		assertEquals("name", g.getName());
	}

	//Very slow test - generates data for histograms
	//@Test
	public void generateMutantData(){
		double iterations = 1000;
		HashMap<String, Boolean> mutants = new HashMap<String, Boolean>();
		mutants.put("par-1", true);
		mutants.put("par-2", false);
		mutants.put("par-3", false);
		mutants.put("par-4", false);
		mutants.put("par-5", false);
		mutants.put("par-6", false);
		mutants.put("pkc-3",  false);
		double mseFate = 0;
		double cdFate = 0;
		double germFate = 0;
		double defFate = 0;
		HashMap<Integer, Integer> mseValues = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> cdValues = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> germValues = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> defValues = new HashMap<Integer, Integer>();
		int j = 0;
		for(int i = 0; i < iterations; i++){
			int mse = 0;
			int cd = 0;
			int germ = 0;
			int def = 0;
			Shell mutantShell = new Shell(testVis, mutants);
			while(mutantShell.simTime < 96){
				mutantShell.timeStep();
			}
			j++;
			System.out.println(j + "th shell processed: " + mutantShell.getCells().size() +" cells present");
			for(String c: mutantShell.getCells().keySet()){
				boolean germline = false;
				boolean MSE = false;
				boolean CD = false;
				Gene pie = mutantShell.getCells().get(c).getGenes().get("pie-1");
				if(pie != null){
					if(!pie.getState().isUnknown()){
						if(pie.getState().isOn()){
							germline = true;
						}
					}
				}
				Gene skn = mutantShell.getCells().get(c).getGenes().get("skn-1");
				if(skn != null){
					if(!skn.getState().isUnknown()){
						if(skn.getState().isOn()){
							MSE = true;
						}
					}
				}
				Gene pal = mutantShell.getCells().get(c).getGenes().get("pal-1");
				if(pal != null){
					if(!pal.getState().isUnknown()){
						if(pal.getState().isOn()){
							CD = true;
						}
					}
				}
				if(germline && !MSE && !CD) germ++;
				else if(!germline && MSE && !CD) mse++;
				else if(!germline && !MSE && CD) cd++;
				else def++;
			}
			if(mseValues.get(mse) == null) mseValues.put(mse, 1);
			else mseValues.put(mse, mseValues.get(mse)+1);
			if(cdValues.get(cd) == null) cdValues.put(cd, 1);
			else cdValues.put(cd, cdValues.get(cd)+1);
			if(germValues.get(germ) == null) germValues.put(germ, 1);
			else germValues.put(germ, germValues.get(germ)+1);
			if(defValues.get(def) == null) defValues.put(def, 1);
			else defValues.put(def, defValues.get(def)+1);

			assertEquals(26, germ+mse+cd+def);	
			mseFate += mse/26.0;
			cdFate += cd/26.0;
			germFate += germ/26.0;
			defFate += def/26.0;			
		}		
		assertTrue(mseFate+cdFate+germFate+defFate > iterations-2 && mseFate+cdFate+germFate+defFate < iterations+2);
		//System.out.println("MS/E: " + mseFate/iterations);
		//System.out.println("C/D: " + cdFate/iterations);
		//System.out.println("germline: " + germFate/iterations);
		//System.out.println("default: " + defFate/iterations);

		for(Integer i: mseValues.keySet()){
			System.out.println(mseValues.get(i) + " shells have " + i + " cells with MS/E cell fate");
		}
		for(Integer i: cdValues.keySet()){
			System.out.println(cdValues.get(i) + " shells have " + i + " cells with C/D cell fate");
		}
		for(Integer i: germValues.keySet()){
			System.out.println(germValues.get(i) + " shells have " + i + " cells with germline cell fate");
		}
		for(Integer i: defValues.keySet()){
			System.out.println(defValues.get(i) + " shells have " + i + " cells with default cell fate");
		}
	}

	//alter the sandbox file however you please to ensure that error checking is working properly
	@Test
	public void parsingSandbox(){
		try{
			ConsequentsList AC = new ConsequentsList();
			AC.readantecedentsAndConsequentsInfo("src/components/parsingSandbox.csv");
			//			testShell.readEventsQueue("src/components/parsingSandbox.csv");
			//			testShell.getCells().get("p-0").readGeneInfo("src/components/parsingSandbox.csv", testShell);
		}
		catch(Exception e){
		}
	}

	@Test
	public void getSuffix(){
		String prefix = "blah";
		String word2 = "blahgrhr";
		assertTrue(word2.startsWith(prefix));
		String suffix = word2.substring(prefix.length(), word2.length());
		assertEquals("grhr", suffix);
	}
	
	public Coordinate snapToGrid(boolean lower, Coordinate near, int gridSize, int W, int H, int D){
		float x = near.getX() - ((near.getX() + W/2) % gridSize);
		float y = near.getY() - ((near.getY() + H/2) % gridSize);
		float z = near.getZ() - ((near.getZ() + D/2) % gridSize);
		if(lower){
			return new Coordinate(x, y, z);
		}
		else{
			return new Coordinate(x+gridSize, y+gridSize, z+gridSize);
		}
	}
	
	@Test
	public void testSnapToGrid(){
		Coordinate testCoord = snapToGrid(true, new Coordinate(5, 5, 5), 12, 500, 300, 300);
		assertEquals((int) testCoord.getX(), 2);
		assertEquals((int) testCoord.getY(), -6);
		assertEquals((int) testCoord.getZ(), -6);
		testCoord = snapToGrid(false, new Coordinate(5, 5, 5), 12, 500, 300, 300);
		assertEquals((int) testCoord.getX(), 14);
		assertEquals((int) testCoord.getY(), 6);
		assertEquals((int) testCoord.getZ(), 6);
	}
	
	@Test
	public void testGenerationCounter(){		
		assertEquals(0, testShell.calculateGeneration("p-0"));
		assertEquals(1, testShell.calculateGeneration("ab"));
		assertEquals(1, testShell.calculateGeneration("p-1"));
		assertEquals(2, testShell.calculateGeneration("ab-a"));
		assertEquals(2, testShell.calculateGeneration("ab-p"));
		assertEquals(2, testShell.calculateGeneration("ems"));
		assertEquals(2, testShell.calculateGeneration("p-2"));
		assertEquals(3, testShell.calculateGeneration("ab-al"));
		assertEquals(3, testShell.calculateGeneration("ab-ar"));
		assertEquals(4, testShell.calculateGeneration("ab-ara"));
		assertEquals(4, testShell.calculateGeneration("ms-a"));
		assertEquals(3, testShell.calculateGeneration("p-3"));
		
		for(String d: testShell.getDivisions().keySet()){
			assertEquals(testShell.getDivisions().get(d).getGeneration(), testShell.calculateGeneration(d));
		}
	}
}