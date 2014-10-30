package com.glacier.rpialmanac;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddLocationActivity extends Activity implements OnClickListener{
	
	private EditText name, addr;
	private Spinner typeSpinner;
	private Button submit;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	private static final String LOGIN_URL = "http://glacier.net76.net/config.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	// Progress Dialog
	private ProgressDialog pDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		name = (EditText)findViewById(R.id.locationName);
		addr = (EditText)findViewById(R.id.locationAddress);
		typeSpinner = (Spinner)findViewById(R.id.type_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(adapter);
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(this);
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
		
	 @Override
	    public void onClick(View v) {
	    	int id = v.getId();
			if (id == R.id.submit) {
				new AddLocation().execute();
			}
	    }
	 
	 private class AddLocation extends AsyncTask<String, Integer, String>{
	    	
		 @Override
		 protected void onPreExecute() {
			 super.onPreExecute();
			 /*pDialog = new ProgressDialog(AddLocationActivity.this);
			 pDialog.setMessage("Adding new location...");
			 pDialog.setIndeterminate(false);
			 pDialog.setCancelable(true);
			 pDialog.show();*/
		 }
    	@Override
    	protected String doInBackground(String... args) {
    	
	    	int success;
	    	String locName = name.getText().toString();
			String locAddr = addr.getText().toString();
			String locType = typeSpinner.getSelectedItem().toString();
			//Location aLocation = new Location(locName,42.740627,-73.678837,locAddr,locType);
			Location aLocation = new Location(locName,42.740627,-73.678837,locAddr, "");
			Gson gson = new Gson();
			String jsonStr = gson.toJson(aLocation);
			
			/*try{*/
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("key", jsonStr));
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
				
				// check your log for json response
				/*Log.d("Add location attempt", json.toString());
				 
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
				Log.d("New location successfully added!", json.toString());
				return json.getString(TAG_MESSAGE);
				}else{
				Log.d("Add location Failure!", json.getString(TAG_MESSAGE));
				return json.getString(TAG_MESSAGE);
				}*/
			//}
			/*catch(JSONException e){
				e.printStackTrace();
	    	}*/
			
			return null;    	
    	}
    	
    	/**
    	* After completing background task Dismiss the progress dialog
    	* **/
    	protected void onPostExecute(String file_url) { 
    		//pDialog.dismiss();	    	 
    		finish();
    	}
    }
}
