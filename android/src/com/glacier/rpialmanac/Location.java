package com.glacier.rpialmanac;

public class Location {
	private String name;
	private Double latitude, longitude;
	private String address;
	private LocationType locationType;
	
	public Location(String aName, Double alatitude, Double aLongitude, String aAddress, LocationType aLocationType) {
		name = aName;
		latitude = alatitude;
		longitude = aLongitude;
		address = aAddress;
		locationType = aLocationType;
	}
	
	public Location(){
		name = "";
		latitude = 0.0;
		longitude = 0.0;
		address = "";
		locationType = null;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getlatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
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
	
	public void setlatitude(Double alatitude) {
		latitude = alatitude;
	}
	
	public void setLongitude(Double aLongitude) {
		longitude = aLongitude;
	}
	
	public void setAddress(String aAddress) {
		address = aAddress;
	}
	
	public void setLocationType(LocationType aLocationType) {
		locationType = aLocationType;
	}
}
