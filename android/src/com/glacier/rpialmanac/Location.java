package com.glacier.rpialmanac;

public class Location {
	private String name;
	private double lattitude, longitude;
	private String address;
	private LocationType locationType;
	
	public Location(String aName, double aLattitude, double aLongitude, String aAddress, LocationType aLocationType) {
		name = aName;
		lattitude = aLattitude;
		longitude = aLongitude;
		address = aAddress;
		locationType = aLocationType;
	}
	
	public String getName() {
		return name;
	}
	
	public double getLattitude() {
		return lattitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getAddress() {
		return address;
	}
	
	public LocationType getLocationType() {
		return locationType;
	}
	
	public void setName(String aName) {
		name = aName;
	}
	
	public void setLattitude(double aLattitude) {
		lattitude = aLattitude;
	}
	
	public void setLongitude(double aLongitude) {
		longitude = aLongitude;
	}
	
	public void setAddress(String aAddress) {
		address = aAddress;
	}
	
	public void setLocationType(LocationType aLocationType) {
		locationType = aLocationType;
	}
}
