package dataStructures;

public class LocationData {
	Coordinates initialLocation; //start location
	Coordinates changedLocation; //new location
	String changeDivision; //the division that the change occurs during
	
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
