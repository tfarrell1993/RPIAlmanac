package com.glacier.rpialmanac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Map extends Activity implements OnClickListener {

	private EditText user, pass;
	private Button addLoc;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	private static final String LOGIN_URL = "http://glacier.net76.net/config.php";
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        addLoc = (Button)findViewById(R.id.addLocation);
        //addLoc.setOnClickListener(this);
        try {
			ArrayList<Location> locations = RetrieveLocations.getLocations();
			final GoogleMap map = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();
			for (int i = 0; i < locations.size(); i++)
			{
			Location location = locations.get(i);
			map.addMarker(new MarkerOptions().position(new LatLng(location.getlatitude(), location.getLongitude())).title(location.getName()));
			}
			
			//Allows user to long click on map to add new location
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
	        });
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void onClick(View v) {
    	int id = v.getId();
		if (id == R.id.addLocation) {
			new AddLocation().execute();
		} else {
		}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
    
    public void addNewLocation(View view){
    	Log.v("alex","test");
    	Intent intent = new Intent(this, AddLocationActivity.class);
    	startActivity(intent);
    }
    
    private class AddLocation extends AsyncTask<String, Integer, String>{
    	
    	public AddLocation(){
    		//TODO Auto-generated constructor stub
    	}
    	@Override
    	protected String doInBackground(String... params) {
    	
	    	String result = "";
	    	InputStream isr = null;
	    	
	    	try{
		    	HttpClient httpclient = new DefaultHttpClient();
		    	HttpPost httppost = new HttpPost("http://glacier.net76.net/config.php"); //YOUR PHP SCRIPT ADDRESS 
		    	org.apache.http.HttpResponse response = httpclient.execute(httppost); 
		    	HttpEntity entity = response.getEntity(); 
		    	isr = entity.getContent();
	    	}
	    	catch(Exception e){
	    		Log.e("log_tag", "Error in http connection "+e.toString());
	    	}
	        
	    	//convert response to string
		    try{
			    BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
			    StringBuilder sb = new StringBuilder();
			    String line = null;
			     
			    while ((line = reader.readLine()) != null) {
			    sb.append(line + "\n");
			    }
			     
			    isr.close();
			    result=sb.toString();
		    }
		    catch(Exception e){
		    	Log.e("log_tag", "Error  converting result "+e.toString());
		    }
		    
		    onPostExecute(result);
		    return result;
    	}
		  
		@SuppressLint("NewApi") // FIXME
		protected void onPostExecute(Double result) { 
			//Log.v("alex","test");
			//parse json data
		    try {
		    	String s = "";
		    	JSONArray jArray = new JSONArray(result);
		    
		    	for(int i=0; i<jArray.length();i++){
		    		JSONObject json = jArray.getJSONObject(i);
		    		s = s +"Name : "+json.getString("id")+" "+json.getString("username");  
		    	}
		    } 
		    catch (Exception e) {
		    // TODO: handle exception
		    Log.e("log_tag", "Error Parsing Data "+e.toString());
		    }
    	}
    }
}
