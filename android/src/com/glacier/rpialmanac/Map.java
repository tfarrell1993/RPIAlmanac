package com.glacier.rpialmanac;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Map extends Activity{

	private static ArrayList<Location> locations = null;
	JSONParser jsonParser = new JSONParser();
	//Progress Dialog
	private ProgressDialog pDialog;
	private Button addLoc, deleteLoc;
	MarkerOptions markerOpt;
	Marker newMarker;
	Double lat,lng;
	GoogleMap map;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.729861, -73.676767), 16));
        map.setMyLocationEnabled(true);
        addLoc = (Button)findViewById(R.id.addLocation);
        deleteLoc = (Button)findViewById(R.id.deleteLocation);
        
        // Retrieve locations from the DB if necessary
        if (locations == null) {
        	new LocationRetriever().execute();
        }
        
        //User long clicks on map. Drops new draggable marker. Add and delete button visible
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
        	@Override
            public void onMapLongClick(LatLng point) {
        		lat = point.latitude;
        		lng = point.longitude;
                markerOpt = new MarkerOptions().position(new LatLng(lat, lng)).title("New Marker").draggable(true);
                newMarker = map.addMarker(markerOpt);
                addLoc.setVisibility(View.VISIBLE);
                deleteLoc.setVisibility(View.VISIBLE);
            }
        });
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
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
    
    public void addNewLocation(View view){
    	Intent intent = new Intent(this, AddLocationActivity.class);
    	Bundle b = new Bundle();
    	

        if(lat!=null && lng!=null){
	    	b.putDouble("latitude", lat);
	    	b.putDouble("longitude", lng);
	    	intent.putExtras(b);
	    	startActivity(intent);
        }
        
        addLoc.setVisibility(View.INVISIBLE);
        deleteLoc.setVisibility(View.INVISIBLE);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
    }
    
    public void deleteNewLocation(View view){
    	newMarker.remove();
    	addLoc.setVisibility(View.INVISIBLE);
        deleteLoc.setVisibility(View.INVISIBLE);
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
