package com.example.rpialmanac;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.example.


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GoogleMap mMap;
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		final LatLng FIELD = new LatLng(42.730357, -73.679951);
		Marker field = mMap.addMarker(new MarkerOptions().position(FIELD));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	   * AsyncTask for adding a new marker to the database  */
	private class addMarkerTask extends AsyncTask<Void, Void, Void> {
		/**
	     * Calls appropriate CloudEndpoint to add a new marker to the database.
	     *
	     * @param params the place where the user is adding a marker
	     */
	    @Override
	    protected Void doInBackground(Void... params) {
	      addMarker newMarker = new addMarker();
	      
	      // Set the ID of the store where the user is. 
	      // This would be replaced by the actual ID in the final version of the code. 
	      newMarker.setMarkerName("86 field");
	      newMarker.setMarkerType("recreation");
	      newMarker.setMarkerLat(42.730357);
	      newMarker.setMarkerLong(-73.679951);

	      addMarkerEndpoint.Builder builder = new addMarkerEndpoint.Builder(
	          AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
	          null);
	          
	      builder = CloudEndpointUtils.updateBuilder(builder);

	      Checkinendpoint endpoint = builder.build();
	      

	      try {
	        endpoint.insertCheckIn(checkin).execute();
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }

	      return null;
		
	}
	}
}
