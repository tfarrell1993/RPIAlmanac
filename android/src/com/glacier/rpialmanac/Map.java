package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

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

public class Map extends Activity implements OnInfoWindowClickListener{

	private static ArrayList<Location> locations = null;
	JSONParser jsonParser = new JSONParser();
	//Progress Dialog
	private ProgressDialog pDialog;
	private Button addLoc, deleteLoc;
	MarkerOptions markerOpt;
	Marker newMarker;
	Double lat,lng;
	int success;
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
        
        //Listener for when user clicks on marker info window
        map.setOnInfoWindowClickListener(this);
        
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_settings:
        	Intent intent = new Intent(this, SettingsWindow.class);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    //Adds all pins in locations array to map
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
    		.position(new LatLng(location.getLatitude(), location.getLongitude())).snippet("Click to read more"));
    	}
    }
    
    //Method called when user clicks the confirm button after adding new marker
    public void addNewLocation(View view){
    	Intent intent = new Intent(this, AddLocationActivity.class);
    	Bundle b = new Bundle();
    	
    	//Bundles latitude and longitude position to send to AddLocation activity
        if(lat!=null && lng!=null){
	    	b.putDouble("latitude", lat);
	    	b.putDouble("longitude", lng);
	    	intent.putExtras(b);
	    	startActivityForResult(intent,90);
        }
    }
    
    //Called when the AddLocation activity is finished
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch(requestCode) {
    	case 90:
    		if(resultCode == RESULT_OK) {
    			Bundle res = data.getExtras();
    			if(res != null) {
    				success = res.getInt("success");
        			Log.v("GLACIER", "Success after activity return: " + success);
        			String json = res.getString("jsonLocation");
        			Location location = new Gson().fromJson(json, Location.class);
        			Log.v("GLACIER","Name: " + location.getName());
        			
        			if(success == 1){
        				locations.add(location);
        				
        				//Hides confirm and delete buttons, centers camera on new marker, and shows the info window
        	            addLoc.setVisibility(View.INVISIBLE);
        	            deleteLoc.setVisibility(View.INVISIBLE);
        	            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
        	            newMarker.setSnippet("Click to read more");
        	            newMarker.setTitle(location.getName());
        	            newMarker.showInfoWindow();
        			}
    			}
    		}
    		break;
    	}
    }
    
    //method called if delete button is pressed while dropping a new pin
    public void deleteNewLocation(View view){
    	newMarker.remove();
    	addLoc.setVisibility(View.INVISIBLE);
        deleteLoc.setVisibility(View.INVISIBLE);
    }
	
    //AsyncTask to retrieve all existing locations in the SQL database
	private class LocationRetriever extends AsyncTask<Void, Integer, Void> {
		
		//Displays a progress pinwheel
		protected void onPreExecute() {
    		pDialog = new ProgressDialog(Map.this);
    		pDialog.setMessage("Retrieving locations...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}
		
		//In the background, call RetrieveLocations class
		//Fill array of Locations with array from database
		protected Void doInBackground(Void... params) {
			locations = RetrieveLocations.getAllLocations();
			
			return null;
		}
		
		//After retrieving all locations, display all pins on map and close progress dialog
		protected void onPostExecute(Void param) {
			Map.this.displayAllPins();
			pDialog.dismiss();
		}
	}

	//If info window is clicked, open new LocationInfoWindow class
	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(Map.this,LocationInfoWindow.class);
		Bundle b = new Bundle();
		
		for(int i = 0; i<locations.size(); i++){
			Location location = locations.get(i);
			if(location.getLatitude() == marker.getPosition().latitude) {
				intent.putExtra("jsonLocation", new Gson().toJson(location));
				startActivity(intent);	
			}
		}	
	}	
}
