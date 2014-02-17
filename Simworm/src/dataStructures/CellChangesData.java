package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class CellChangesData {
	List<String> cellsRemoved;
	List<Cell> cellsAdded;
	
	public CellChangesData(ArrayList<String> cellsRemoved, ArrayList<Cell> cellsAdded){
		this.cellsRemoved = cellsRemoved;
		this.cellsAdded = cellsAdded;
	}
}
