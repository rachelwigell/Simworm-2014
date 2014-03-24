package dataStructures;

public class LocationData {
	Coordinates initialLocation; //start location
	Coordinates changedLocation; //new location
	String changeDivision; //the division that the change occurs during
	
	/**
	 * Constructor for a locationData object - holds information about genes that change location
	 * @param initialLocation The location of the gene at the time of creation of the shell
	 * @param changedLocation The location that it changes to
	 * @param changeAfterDivision The name of the cell who division triggers the change in location
	 */
	public LocationData(Coordinates initialLocation, Coordinates changedLocation, String changeAfterDivision){
		this.initialLocation = initialLocation;
		this.changedLocation = changedLocation;
		this.changeDivision = changeAfterDivision;
	}

	//getters
	public Coordinates getInitialLocation() {
		return initialLocation;
	}

	public Coordinates getChangedLocation() {
		return changedLocation;
	}

	public String getChangeDivision() {
		return changeDivision;
	}
}
