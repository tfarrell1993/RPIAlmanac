package com.glacier.rpialmanac;

public class Location {
	private String name;
	private Double latitude, longitude;
	private String address;
	private String locationType;
	private int key;
	
	public Location(String aName, Double aLatitude, Double aLongitude, String aAddress, String locType, int id) {
		name = aName;
		latitude = aLatitude;
		longitude = aLongitude;
		address = aAddress;
		locationType = locType;
		key = id;
	}
	
	public Location(){
		name = "";
		latitude = 0.0;
		longitude = 0.0;
		address = "";
		locationType = null;
		key = 0;
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
	
	public int getKey() {
		return key;
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
