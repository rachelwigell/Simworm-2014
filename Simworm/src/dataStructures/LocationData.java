package dataStructures;

import java.util.HashMap;

public class LocationData {
	Coordinates initialLocation; //start location
	HashMap<String, Coordinates> changes; //holds changes, key is the cell division in which the change takes place, value is the new location
	
	/**
	 * Constructor for a locationData object - holds information about genes that change location
	 * @param initialLocation The location of the gene at the time of creation of the shell
	 * @param changedLocation The location that it changes to
	 * @param changeAfterDivision The name of the cell who division triggers the change in location
	 */
	public LocationData(Coordinates initialLocation, HashMap<String, Coordinates> changes){
		this.initialLocation = initialLocation;
		this.changes = changes;
	}

	//getters
	public Coordinates getInitialLocation() {
		return initialLocation;
	}

	public HashMap<String, Coordinates> getChanges() {
		return changes;
	}
}
