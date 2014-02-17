package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class CellChangesData {
	public List<String> cellsRemoved;
	public List<Cell> cellsAdded;
	
	public CellChangesData(ArrayList<String> cellsRemoved, ArrayList<Cell> cellsAdded){
		this.cellsRemoved = cellsRemoved;
		this.cellsAdded = cellsAdded;
	}
}
