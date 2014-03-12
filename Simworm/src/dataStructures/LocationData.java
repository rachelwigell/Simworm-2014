package dataStructures;

public class LocationData {
	Coordinates initialLocation;
	Coordinates changedLocation; //optional, for genes that change location at some cell division
	String changeDivision; //the division that the change occurs during

	public LocationData(Coordinates location){//for genes that don't move
		this.initialLocation = location;
	}
	
	public LocationData(Coordinates initialLocation, Coordinates changedLocation, String changeAfterDivision){
		this.initialLocation = initialLocation;
		this.changedLocation = changedLocation;
		this.changeDivision = changeAfterDivision;
	}

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
