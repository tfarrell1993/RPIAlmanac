package com.glacier.rpialmanac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RetrieveLocations {
	
	static JSONObject jobj;
	private static final String RETRIEVE_URL = "http://glacier.net76.net/retrieve.php";
	
	public static ArrayList<Location> getAllLocations(Void... params) {
		String JSON = retrieveJSON();
		if (JSON == null) {
			return null;
		}
		
		Log.w("GLACIER", JSON);
		ArrayList<Location> locations = parseLocationsFromJSON(JSON);
		
		return locations;
	}
	
	private static String retrieveJSON() {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(new HttpGet(RETRIEVE_URL));
		} catch (IOException e) {
			// For any error when trying to get the HTTP response
			return null;
		}
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// Some other server-side issue
			return null;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			response.getEntity().writeTo(stream);
			stream.close();
		} catch (IOException e) {
			return null;
		}
		
		String dirtyJSON = stream.toString();
		int endIndex = dirtyJSON.indexOf(']');
		if (endIndex == -1) {
			// Something is wrong with our JSON
			return null;
		}
		String JSON = dirtyJSON.substring(0, dirtyJSON.indexOf(']') + 1);
		
		return JSON;
	}
	
	private static ArrayList<Location> parseLocationsFromJSON(String JSON) {
		ArrayList<Location> locations = new ArrayList<Location>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(JSON);
		} catch (JSONException e) {
			// Bad JSON
			Log.w("GLACIER", "1");
			return null;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				// Bad JSON
				Log.w("GLACIER", "2");
				return null;
			}
			try {
				Location location = new Location(jsonObject.getString("name"), Double.parseDouble(jsonObject.getString("latitude")),
						Double.parseDouble(jsonObject.getString("longitude")), jsonObject.getString("address"), jsonObject.getString("type"));
				locations.add(location);
			} catch (Exception e) {
				// JSON or number format issue
				Log.w("GLACIER", "3");
				return null;
			}
		}
		
		return locations;
	}
}
