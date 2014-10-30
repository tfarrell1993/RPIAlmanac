package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

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
	Double lat, lng;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	private static final String POST_URL = "http://glacier.net76.net/post.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	// Progress Dialog
	private ProgressDialog pDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		Bundle b = getIntent().getExtras();
		lat = b.getDouble("latitude");
		lng = b.getDouble("longitude");
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
				finish();
			}
	    }
	 
	 private class AddLocation extends AsyncTask<String, Integer, String>{
	    	
		 @Override
		 protected void onPreExecute() {
			 super.onPreExecute();
			 pDialog = new ProgressDialog(AddLocationActivity.this);
			 pDialog.setMessage("Adding new location...");
			 pDialog.setIndeterminate(false);
			 pDialog.setCancelable(true);
			 pDialog.show();
		 }
    	@Override
    	protected String doInBackground(String... args) {
    	
    		int success;
	    	String locName = name.getText().toString();
			String locAddr = addr.getText().toString();
			String locType = typeSpinner.getSelectedItem().toString();
			
			try{
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", locName));
				params.add(new BasicNameValuePair("address", locAddr));
				params.add(new BasicNameValuePair("type", locType));
				params.add(new BasicNameValuePair("latitude", lat.toString()));
				params.add(new BasicNameValuePair("longitude", lng.toString()));
				JSONObject json = jsonParser.makeHttpRequest(POST_URL, "POST", params);
				
				success = json.getInt(TAG_SUCCESS);
				Log.d("GLACIER",json.getString(TAG_MESSAGE));
			}
			catch(Exception e){
				e.printStackTrace();
	    	}
			
			return null; 	
    	}
    	
    	/**
    	* After completing background task Dismiss the progress dialog
    	* **/
    	protected void onPostExecute(String file_url) { 
    		pDialog.dismiss();	    	 
    	}
    }
}