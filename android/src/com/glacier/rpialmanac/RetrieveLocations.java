package com.glacier.rpialmanac;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
											
public class RetrieveLocations {
	
	public static ArrayList<Location> getLocations() throws InterruptedException, ExecutionException, JSONException {
		
		ArrayList<Location> results = new ArrayList<Location>();
		String json = new LocationRetriever().execute().get();
		String current = "";
		for (int i = 0; i < json.length(); i++)
		{
		current += json.charAt(i);
		if(json.charAt(i) == '}')
		{
		JSONObject jsonobj = new JSONObject(current);
		Location location = new Location(jsonobj.getString("name"), Double.parseDouble(jsonobj.getString("latitude")), Double.parseDouble(jsonobj.getString("longitude")), jsonobj.getString("address"), LocationType.Academic);
		results.add(location);
		Log.v("GLACIER", "****");
		}
		}

		return results;
	}
	
	public static class LocationRetriever extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... args) {

		    String responseString = null;
			try
			{
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet("http://glacier.net76.net/retrieve.php"));
		    StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        responseString = out.toString();
		}
			}
			catch (Exception e) { }
			
			return responseString;
		    
	}

}
}