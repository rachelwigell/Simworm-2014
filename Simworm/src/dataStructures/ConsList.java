package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class ConsList { //holds data about antecedents and consequences
		public List<Consequence> AandC = new ArrayList<Consequence>();
		public List<Consequence> startLate = new ArrayList<Consequence>();
		
	public ConsList(){
		AandC.add(new Consequence(new Gene[] {new Gene("pie-1", new GeneState(true)), new Gene("pal-1", new GeneState(true))}, new Gene("pal-1", new GeneState(false)), 1, 0)); //0 means no end
		AandC.add(new Consequence(new Gene[] {new Gene("pie-1", new GeneState(true)), new Gene("skn-1", new GeneState(true))}, new Gene("skn-1", new GeneState(false)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("skn-1", new GeneState(true)), new Gene("pal-1", new GeneState(true))}, new Gene("pal-1", new GeneState(false)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mex-3", new GeneState(true)), new Gene("pal-1", new GeneState(true))}, new Gene("pal-1", new GeneState(false)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("par-2", new GeneState(true)), new Gene("pkc-3", new GeneState(true))}, new Gene("pkc-3", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-2", new GeneState(true)), new Gene("par-6", new GeneState(true))}, new Gene("par-6", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-2", new GeneState(true)), new Gene("par-3", new GeneState(true))}, new Gene("par-3", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-1", new GeneState(true)), new Gene("par-6", new GeneState(true))}, new Gene("pkc-3", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-1", new GeneState(true)), new Gene("par-6", new GeneState(true))}, new Gene("par-6", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-1", new GeneState(true)), new Gene("par-3", new GeneState(true))}, new Gene("par-3", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-3", new GeneState(true)), new Gene("par-6", new GeneState(true)), new Gene("pkc-3", new GeneState(true)), new Gene("par-2", new GeneState(true))}, new Gene("par-2", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("par-3", new GeneState(true)), new Gene("par-1", new GeneState(true))}, new Gene("par-1", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("mex-5", new GeneState(true)), new Gene("par-3", new GeneState(false))}, new Gene("par-3", new GeneState(true)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("mex-5", new GeneState(true)), new Gene("par-6", new GeneState(false))}, new Gene("par-3", new GeneState(true)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("mex-5", new GeneState(true)), new Gene("pkc-3", new GeneState(false))}, new Gene("pkc-3", new GeneState(true)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("mex-5", new GeneState(true)), new Gene("pie-1", new GeneState(true))}, new Gene("pie-1", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("skn-1", new GeneState(true)), new Gene("MS signal", new GeneState(false))}, new Gene("MS signal", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("tbx-37", new GeneState(true)), new Gene("tbx-38", new GeneState(true)), new Gene("pha-4", new GeneState(false))}, new Gene("pha-4", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-3", new GeneState(true)), new Gene("mom-1", new GeneState(true)), new Gene("mom-2", new GeneState(false))}, new Gene("mom-2", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-5", new GeneState(true)), new Gene("mom-4", new GeneState(false))}, new Gene("mom-4", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-4", new GeneState(true)), new Gene("lit-1", new GeneState(false))}, new Gene("lit-1", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-4", new GeneState(true)), new Gene("wrm-1", new GeneState(false))}, new Gene("wrm-1", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-4", new GeneState(true)), new Gene("wrm-1", new GeneState(true)), new Gene("lit-1", new GeneState(true)), new Gene("pop-1", new GeneState(true))}, new Gene("pop-1", new GeneState(false)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("mom-2", new GeneState(true)), new Gene("mom-5", new GeneState(false))}, new Gene("mom-5", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("skn-1", new GeneState(true)), new Gene("med-2", new GeneState(false))}, new Gene("med-2", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("skn-1", new GeneState(true)), new Gene("med-1", new GeneState(false))}, new Gene("med-1", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("par-1", new GeneState(true)), new Gene("mex-5", new GeneState(true))}, new Gene("mex-5", new GeneState(false)), 1, 26));
		AandC.add(new Consequence(new Gene[] {new Gene("wrm-1", new GeneState(true)), new Gene("lit-1", new GeneState(true)), new Gene("pop-1", new GeneState(false))}, new Gene("pop-1", new GeneState(true)), 1, 0));
		AandC.add(new Consequence(new Gene[] {new Gene("par-1", new GeneState(true)), new Gene("mex-3", new GeneState(true))}, new Gene("mex-3", new GeneState(false)), 1, 0));
		
		startLate.add(new Consequence(new Gene[] {new Gene("apx-1", new GeneState(true)), new Gene("glp-1", new GeneState(false))}, new Gene("glp-1", new GeneState(true)), 4, 4));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("tbx-37", new GeneState(true))}, new Gene("tbx-37", new GeneState(false)), 4, 4));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("tbx-38", new GeneState(true))}, new Gene("tbx-38", new GeneState(false)), 4, 4));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("ref-1", new GeneState(false))}, new Gene("ref-1", new GeneState(true)), 4, 4));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("ref-1", new GeneState(false))}, new Gene("ref-1", new GeneState(true)), 12, 12));
		startLate.add(new Consequence(new Gene[] {new Gene("MS signal", new GeneState(true)), new Gene("glp-1", new GeneState(false))}, new Gene("glp-1", new GeneState(true)), 12, 12));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("lag-2", new GeneState(true))}, new Gene("lag-2", new GeneState(false)), 12, 12));
		startLate.add(new Consequence(new Gene[] {new Gene("glp-1", new GeneState(true)), new Gene("lin-12", new GeneState(false))}, new Gene("lin-12", new GeneState(true)), 12, 12));
		startLate.add(new Consequence(new Gene[] {new Gene("par-4", new GeneState(true)), new Gene("mex-3", new GeneState(true))}, new Gene("mex-3", new GeneState(false)), 4, 4));
	}
}
