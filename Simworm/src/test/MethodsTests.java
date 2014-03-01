package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Test;







import processing.BasicVisual;
import dataStructures.Axes;
import dataStructures.Cell;
import dataStructures.CellChangesData;
import dataStructures.Compartment;
import dataStructures.Coordinates;
import dataStructures.DivisionData;
import dataStructures.Gene;
import dataStructures.GeneState;
import dataStructures.Shell;

public class MethodsTests {
	BasicVisual testVis = new BasicVisual();
	Shell testShell = new Shell(testVis);
	
	@Test
	public void firstDivision() {
		ArrayList<CellChangesData> cellChanges = new ArrayList<CellChangesData>();
		HashMap<String, Coordinates> abChanges = new HashMap<String, Coordinates>();
		abChanges.put("pkc-3", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-3", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		abChanges.put("par-6", new Coordinates(Compartment.XCENTER, Compartment.YCENTER, Compartment.ZCENTER));
		
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
		testShell = new Shell(testVis);
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
		System.out.println("Begin test");
		
		Cell testCell = new Shell(testVis).getCells().get("p-0");
		
		testCell.applyCons();
		
		System.out.println("end test");
		
		System.out.println("pie-1 is " + testCell.getGenes().get("pie-1").getState().isOn());
		System.out.println("skn-1 is " + testCell.getGenes().get("skn-1").getState().isOn());
		System.out.println("pal-1 is " + testCell.getGenes().get("pal-1").getState().isOn());
		System.out.println("wrm-1 is " + testCell.getGenes().get("wrm-1").getState().isOn());
		System.out.println("lit-1 is " + testCell.getGenes().get("lit-1").getState().isOn());
		System.out.println("pop-1 is " + testCell.getGenes().get("pop-1").getState().isOn());
		System.out.println("pha-4 is " + testCell.getGenes().get("pha-4").getState().isOn());
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
	
	@Test
	public void mutationsTest(){
		testShell = new Shell(testVis);
		Cell c = testShell.getCells().get("p-0");
		testShell.calcMutation(c);
	}
}
