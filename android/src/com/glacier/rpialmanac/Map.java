package com.glacier.rpialmanac;

import java.util.ArrayList;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class Map extends Activity {
	
	private static ArrayList<Location> locations = null;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        // Retrieve locations from the DB if necessary
        if (locations == null) {
        	new LocationRetriever().execute();
        }
        
		/*	//Allows user to long click on map to add new location
			map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
	        	@Override
	            public void onMapLongClick(LatLng point) {
	                MarkerOptions marker = new MarkerOptions().position(
	                        new LatLng(point.latitude, point.longitude))
	                        .title("New Marker")
	                        .draggable(true)
	                        .snippet("This is a test snippit");
	                map.addMarker(marker);
	            }
	        });*/
    }
    
    protected void displayAllPins() {
    	if (locations == null) {
    		Log.w("GLACIER", "null locations");
    		return;
    	}
    	
    	final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    	map.clear();
    	for (int i = 0; i < locations.size(); i++) {
    		Location location = locations.get(i);
    		map.addMarker(new MarkerOptions().title(location.getName())
    				.position(new LatLng(location.getLatitude(), location.getLongitude())));
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
    
    public void addNewLocation(View view){
    	Intent intent = new Intent(this, AddLocationActivity.class);
    	startActivity(intent);
    }
    
    private class LocationRetriever extends AsyncTask<Void, Integer, Void> {
    	
    	protected void onPreExecute() {
    		pDialog = new ProgressDialog(Map.this);
    		pDialog.setMessage("Retrieving locations...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}

		protected Void doInBackground(Void... params) {
			locations = RetrieveLocations.getAllLocations();
			
			return null;
		}
		
		protected void onPostExecute(Void param) {
			Map.this.displayAllPins();
			pDialog.dismiss();
		}
    	
    }
}
