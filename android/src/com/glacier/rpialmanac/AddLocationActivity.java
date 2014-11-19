package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddLocationActivity extends Activity implements OnClickListener {
  // Constants needed for server interaction
  private static final String POST_URL = "http://glacier.net76.net/post.php";
  private static final String TAG_SUCCESS = "success";
  private static final String TAG_KEY = "id";

  // Views for activity
  private EditText name;
  private EditText addr;
  private Spinner typeSpinner;
  private Button submit;

  // JSON parser class
  private JSONParser jsonParser = new JSONParser();
  //JSON object from server
  private JSONObject json;

  // Progress Dialog
  private ProgressDialog pDialog;

  // location data
  private Double lat;
  private Double lng;
  private Location location;

  // Response success variable from server
  int success;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_location);

    // Get position from dropped marker
    Bundle bundle = getIntent().getExtras();
    lat = bundle.getDouble("latitude");
    lng = bundle.getDouble("longitude");

    // Initialize text boxes
    name = (EditText)findViewById(R.id.locationName);
    addr = (EditText)findViewById(R.id.locationAddress);

    // Initialize spinner for location types
    typeSpinner = (Spinner)findViewById(R.id.type_spinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    typeSpinner.setAdapter(adapter);

    // Initialize button
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

  // If back button was pressed
  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  // User clicks submit button
  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.submit) {
      if(!name.getText().toString().matches("") && !addr.getText().toString().matches("")) {
        // Executes AsyncTask to post new location
        new AddLocation().execute();

        // Waits for response from server
        while(success == 0) {}

        // Packages success and new location to return to map activity
        Intent intent = new Intent();
        intent.putExtra("success",success);
        intent.putExtra("jsonLocation", new Gson().toJson(location));
        setResult(RESULT_OK, intent);
        finish();
      } else {
        // Message if user does not fill out all text boxes
        Context context = getApplicationContext();
        CharSequence text = "Please enter all data. If you are unsure of the location address, please enter 'on campus' or 'unknown'.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
      }
    }
  }

  // AsyncTask to post new location to database
  private class AddLocation extends AsyncTask<String, Integer, String>{
    // Starts progress dialog before starting task
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(AddLocationActivity.this);
      pDialog.setMessage("Adding new location...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(true);
      pDialog.show();
    }

    // Communicates with server in background and parses response
    @Override
    protected String doInBackground(String... args) {
      // Gets location information from user interface
      String locName = name.getText().toString();
      String locAddr = addr.getText().toString();
      String locType = typeSpinner.getSelectedItem().toString();

      try {
        // Packages up data for location to be added to database
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", locName));
        params.add(new BasicNameValuePair("address", locAddr));
        params.add(new BasicNameValuePair("type", locType));
        params.add(new BasicNameValuePair("latitude", lat.toString()));
        params.add(new BasicNameValuePair("longitude", lng.toString()));
        json = jsonParser.makeHttpRequest(POST_URL, "POST", params);

        success = json.getInt(TAG_SUCCESS);
        int key = json.getInt(TAG_KEY);
        location = new Location(locName,lat,lng,locAddr,locType,key,0.0,0);
      } catch(Exception e){
        e.printStackTrace();
      }

      return null; 	
    }

    // After completing background task Dismiss the progress dialog
    protected void onPostExecute(String file_url) { 
      pDialog.dismiss();	    	 
    }
  }
}
