package com.glacier.rpialmanac;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Map extends Activity implements OnInfoWindowClickListener{

	// Storing locations
	private static ArrayList<Location> locations = null;
	private static HashMap<Marker, Location> markerLocationMap;

	//Progress Dialog
	private ProgressDialog pDialog;
	
	// Views
	private Button addLoc, deleteLoc, searchButton;
	private EditText searchBox = null;
	GoogleMap map;
	
	// Potential new marker data
	Marker newMarker;
	Double newMarkerLat, newMarkerLng;
	int addLocationSuccess;
	
	// Filters
	boolean filter_academic = true, filter_food = true, filter_landmark = true, filter_university = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        // Initialize map, move camera to RPI, enable my location
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.729861, -73.676767), 16));
        map.setMyLocationEnabled(true);
        
        // Initialize buttons in activity
        addLoc = (Button)findViewById(R.id.addLocation);
        deleteLoc = (Button)findViewById(R.id.deleteLocation);
        searchBox = (EditText)findViewById(R.id.searchBox);
        searchButton = (Button)findViewById(R.id.searchButton);
        
        // Retrieve locations from the DB if necessary
        if (locations == null) {
        	new LocationRetriever().execute();
        }
        
        //Listener for when user clicks on marker info window
        map.setOnInfoWindowClickListener(this);
        map.clear();
        
        //User long clicks on map. Drops new draggable marker. Add and delete button visible
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
        	@Override
            public void onMapLongClick(LatLng point) {
        		newMarkerLat = point.latitude;
        		newMarkerLng = point.longitude;
        		MarkerOptions markerOpt = new MarkerOptions().position(new LatLng(newMarkerLat, newMarkerLng)).title("New Marker").draggable(true);
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
    
    //Adds all pins in locations array to map
    protected void displayAllPins() {
    	if (locations == null) {
    		Log.w("GLACIER", "null locations");
    		return;
    	}
    	map.setInfoWindowAdapter(new LocationBubble());
    	map.clear();
    	markerLocationMap = new HashMap<Marker, Location>();
    	
    	// Displays each retrieved location and adds to marker/location hashmap
    	for (int i = 0; i < locations.size(); i++) {
    		Location location = locations.get(i);
    		if ((filter_academic && location.getLocationType().equals("Academic")) ||
					(filter_food && location.getLocationType().equals("Food")) ||
					(filter_landmark && location.getLocationType().equals("Landmark")) ||
					(filter_university && location.getLocationType().equals("University Building")))
			{
				Marker marker = map.addMarker(new MarkerOptions().title(location.getName())
						.position(new LatLng(location.getLatitude(), location.getLongitude())).snippet("Click to read more"));
				markerLocationMap.put(marker, location);
			}
    	}
    }
    
    // Called when user clicks search button
    // Checks locations for match
    public void searchForLocation(View view) {
    	String searchEntry = searchBox.getText().toString();
    	Boolean matched = false;
    	
    	//Splits search into individual words to better search for location
    	String [] searchWords = searchEntry.split(" ");
    	
    	// list of locations matching search
    	HashMap<Marker,Location> matchedEntries = null;
    	    	
    	//Iterates through hashmap and each word in searchBox for a match
    	if(markerLocationMap != null) {
	    	for(HashMap.Entry<Marker,Location> entry : markerLocationMap.entrySet()) {
				for(int i=0;i<searchWords.length;i++) {
					
					// Check if location name contains one of the words typed into search
					// If match occurs, center map on location and display info window
					if(entry.getValue().getName().toLowerCase().contains(searchWords[i].toLowerCase())) {
						matched = true;
						LatLng point = entry.getKey().getPosition();
		        		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 17));
		        		entry.getKey().showInfoWindow();
		        		searchBox.setText(null);
		        		return;
					}
		    	}
			}
	    	
	    	// If there are no matches, dialog box appears asking if user would like to add it
	    	if(!matched) {
	    		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    		    @Override
	    		    public void onClick(DialogInterface dialog, int which) {
	    		        switch (which){
	    		        case DialogInterface.BUTTON_POSITIVE:
	    		            // Yes button clicked
	    		        	// Instructs user how to add new location via Toast message
	    		        	searchBox.setText(null);
	    		        	Context context = getApplicationContext();
	    		        	CharSequence text = "Click and hold map to drop new location.";
	    		        	int duration = Toast.LENGTH_SHORT;

	    		        	Toast toast = Toast.makeText(context, text, duration);
	    		        	toast.show();
	    		            break;

	    		        case DialogInterface.BUTTON_NEGATIVE:
	    		            //No button clicked
	    		        	// Closes dialog
	    		        	searchBox.setText(null);
	    		        	return;
	    		        }
	    		    }
	    		};
	    		
	    		// Alert Dialog box builder
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage("There are 0 search results for " + searchBox.getText().toString() + ". Would you like to create a new location?")
	    			.setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	    	}
    	}
    	else {
    		// Display error message if hashmap of all locations is empty
    		Context context = getApplicationContext();
        	CharSequence text = "Error retrieving locations";
        	int duration = Toast.LENGTH_SHORT;

        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
    		return;
    	}
    }
    
    //Method called when user clicks the confirm button after adding new marker
    public void addNewLocation(View view){
    	Intent intent = new Intent(this, AddLocationActivity.class);
    	Bundle b = new Bundle();
    	
    	//Bundles latitude and longitude position to send to AddLocation activity
    	if(newMarkerLat!=null && newMarkerLng!=null){
	    	b.putDouble("latitude", newMarkerLat);
	    	b.putDouble("longitude", newMarkerLng);
	    	intent.putExtras(b);
	    	
	    	//Starts activity which returns an intent when finished
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
    				
    				// Retrieves data from intent
    				addLocationSuccess = res.getInt("success");
    				String json = res.getString("jsonLocation");
    				
    				// Extracts location from json encoded location
        			Location location = new Gson().fromJson(json, Location.class);
        			
        			if(addLocationSuccess == 1){
        				locations.add(location);
        				
        				// Hides confirm and delete buttons, centers camera on new marker
        				// Adds to hashmap and shows the info window
        	            addLoc.setVisibility(View.INVISIBLE);
        	            deleteLoc.setVisibility(View.INVISIBLE);
        	            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newMarkerLat, newMarkerLng), 17));
        	            newMarker.setSnippet("Click to read more");
        	            newMarker.setTitle(location.getName());
        	            markerLocationMap.put(newMarker, location);
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
		Intent intent = new Intent(Map.this, LocationInfoWindow.class);

		// Find location clicked on and send to info window activity
		for(int i = 0; i<locations.size(); i++){
			Location location = locations.get(i);
			if(location.getLatitude() == marker.getPosition().latitude) {
				intent.putExtra("jsonLocation", new Gson().toJson(location));
				startActivity(intent);	
			}
		}	
	}	
	
	public void onTypeToggle(View view) {
		if (view == findViewById(R.id.typeToggle1)) {
			filter_academic = !filter_academic;
		}
		else if (view == findViewById(R.id.typeToggle2)) {
			filter_food = !filter_food;
		}
		else if (view == findViewById(R.id.typeToggle3)) {
			filter_landmark = !filter_landmark;
		}
		else if (view == findViewById(R.id.typeToggle4)) {
			filter_university = !filter_university;
		}
		displayAllPins();
	}
	
	// Custom details bubble for each marker
	private class LocationBubble implements InfoWindowAdapter {

		public View getInfoContents(Marker marker) {
			View view = getLayoutInflater().inflate(R.layout.bubble, null);
			Location location = markerLocationMap.get(marker);
			
			TextView titleView = (TextView)view.findViewById(R.id.bubble_title);
			titleView.setText(location.getName());
			
			TextView categoryView = (TextView)view.findViewById(R.id.bubble_category);
			categoryView.setText(location.getLocationType());
			
			return view;
		}

		public View getInfoWindow(Marker marker) {
			return null;
		}

	}
}
