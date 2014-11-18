package com.glacier.rpialmanac;

public class Location {
  
  // Location object variables
  private String name;
  private Double latitude, longitude, rating;
  private String address;
  private String locationType;
  private int numberOfRatings;
  private int key;

  // Constructor
  public Location(String aName, Double aLatitude, Double aLongitude, String aAddress, String locType, int id, Double r, int numRatings) {
    name = aName;
    latitude = aLatitude;
    longitude = aLongitude;
    address = aAddress;
    locationType = locType;
    key = id;
    rating = r;
    numberOfRatings = numRatings;
  }

  // Default constructor
  public Location(){
    name = "";
    latitude = 0.0;
    longitude = 0.0;
    address = "";
    locationType = null;
    key = 0;
    rating = 0.0;
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

  public double getRating() {
    return rating;
  }

  public int getNumberOfRatings() {
    return numberOfRatings;
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

  // Set new rating and increment number of ratings
  public void setRating(Double r) {
    rating = r;
    numberOfRatings += 1;
  }

  public void setNumberOfRatings(int numRatings) {
    numberOfRatings = numRatings;
  }
}
