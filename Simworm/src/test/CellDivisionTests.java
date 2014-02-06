package test;

import static org.junit.Assert.*;

import org.junit.Test;

import dataStructures.Axes;
import dataStructures.Cell;
import dataStructures.Compartment;
import dataStructures.Coordinates;
import dataStructures.DivisionData;
import dataStructures.Gene;
import dataStructures.GeneState;
import dataStructures.Shell;

public class CellDivisionTests {
	Shell testShell = new Shell();
	
	/*@Test
	public void firstDivision() {
		testShell.cellDivision("p-0", .6, Axes.X);
		Cell ab = testShell.getCells().get("ab");
		Cell p1 = testShell.getCells().get("p-1");
		assertTrue(ab.getCenter().getX() == 15 && p1.getCenter().getX() == 40 && ab.getLengths().getX() == 30 && p1.getLengths().getX() == 20);
	}
	
	@Test
	public void runEventsQueue(){
		System.out.println("Start events queue: new shell created");
		testShell = new Shell();
		for(DivisionData d: testShell.getDivisions()){
			testShell.cellDivision(d.getParent(), d.getD1Percentage(), d.getAxis());
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
		Cell testCell = new Shell().getCells().get("p-0");
		
		testCell.applyCons();
		
		System.out.println("pie-1 is " + testCell.getGenes().get("pie-1").getState().isOn());
		System.out.println("skn-1 is " + testCell.getGenes().get("skn-1").getState().isOn());
		System.out.println("pal-1 is " + testCell.getGenes().get("pal-1").getState().isOn());
		System.out.println("wrm-1 is " + testCell.getGenes().get("wrm-1").getState().isOn());
		System.out.println("lit-1 is " + testCell.getGenes().get("lit-1").getState().isOn());
		System.out.println("pop-1 is " + testCell.getGenes().get("pop-1").getState().isOn());
		System.out.println("pha-4 is " + testCell.getGenes().get("pha-4").getState().isOn());
	}*/
	
	@Test
	public void testTimeLapse(){
		System.out.println("Begin time lapse tests");
		Shell testShell = new Shell();
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep(); //5
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep();
		testShell.timeStep(); //10
	}
}
