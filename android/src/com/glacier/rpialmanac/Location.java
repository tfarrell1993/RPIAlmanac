package com.glacier.rpialmanac;

public class Location {
	private String name;
	private Double latitude, longitude;
	private String address;
	private String locationType;
	
	public Location(String aName, Double alatitude, Double aLongitude, String aAddress, String aLocationType) {
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
	
	public Double getLatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLocationType() {
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
	
	public void setLocationType(String aLocationType) {
		locationType = aLocationType;
	}
}
