package com.glacier.rpialmanac;

public class Location {
	private String name;
	private long lattitude, longitude;
	private String address;
	private LocationType locationType;
	
	public Location(String aName, long aLattitude, long aLongitude, String aAddress, LocationType aLocationType) {
		name = aName;
		lattitude = aLattitude;
		longitude = aLongitude;
		address = aAddress;
		locationType = aLocationType;
	}
	
	public String getName() {
		return name;
	}
	
	public long getLattitude() {
		return lattitude;
	}
	
	public long getLongitude() {
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
	
	public void setLattitude(long aLattitude) {
		lattitude = aLattitude;
	}
	
	public void setLongitude(long aLongitude) {
		longitude = aLongitude;
	}
	
	public void setAddress(String aAddress) {
		address = aAddress;
	}
	
	public void setLocationType(LocationType aLocationType) {
		locationType = aLocationType;
	}
}
