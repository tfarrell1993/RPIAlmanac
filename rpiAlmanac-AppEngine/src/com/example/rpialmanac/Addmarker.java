package com.example.rpialmanac;

import com.google.appengine.api.datastore.Key;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Addmarker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	private String markerName;
	private String markerType;
	private Double markerLat;
	private Double markerLong;
	
	public Key getKey() {
		return key;
	}
	
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}
	
	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}
	
	public void setMarkerLat(Double markerLat) {
		this.markerLat = markerLat;
	}
	
	public void setMarkerLong(Double markerLong) {
		this.markerLong = markerLong;
	}
}
