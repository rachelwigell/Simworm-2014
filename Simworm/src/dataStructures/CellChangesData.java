package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class CellChangesData {
	public List<String> cellsRemoved;
	public List<Cell> cellsAdded;
	
	/**
	 * Constructor for cellChangesData - holds information after a cell division about which cells are now gone and which ones will be added
	 * @param cellsRemoved Cells that divided and are now gone
	 * @param cellsAdded Cells that were created during division
	 */
	public CellChangesData(ArrayList<String> cellsRemoved, ArrayList<Cell> cellsAdded){
		this.cellsRemoved = cellsRemoved;
		this.cellsAdded = cellsAdded;
	}
}