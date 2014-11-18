package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationInfoWindow extends Activity {
	
	// Constants for server interaction
	private static final String POST_URL = "http://glacier.net76.net/postComment.php";
	private static final String TAG_SUCCESS = "success";
	
	//Progress dialog wheel
	private ProgressDialog pDialog;
	
	// Views for activity
	private TextView name, addr, type, rating;
	private EditText newComment;
	private Button postComment, addRating;
	private ListView commentList;
	
	// Success from server
	private int success = 0;
	
	// List of all comments
	private ArrayList<String> comments = new ArrayList<String>();
	
	// location
	Location location;
	
	// iterator value of location in list of location from map activity
	int iLoc;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	//JSON object from server
	JSONObject json;

	// Create activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_info_window);
		
		//Convert bundled location object from json into location
		String jsonMyObject = "";
		Bundle b = getIntent().getExtras();
		if (b != null) {
		   jsonMyObject = b.getString("jsonLocation");
		   iLoc = b.getInt("i");
		}
		location = new Gson().fromJson(jsonMyObject, Location.class);
		
		// Initialize textviews for location info
		name = (TextView)findViewById(R.id.locName);
		name.setText(location.getName());
		addr = (TextView)findViewById(R.id.locAddr);
		addr.setText("  " + location.getAddress());
		type = (TextView)findViewById(R.id.locType);
		type.setText("  " + location.getLocationType());
		rating = (TextView)findViewById(R.id.locRating);
		
		// Print rating
		if(location.getRating() != 0.0) {
			rating.setText(location.getRating() + " / 5 stars of " + location.getNumberOfRatings() + " ratings");
		}
		else {
			rating.setText("No Rating");
		}
		
		addRating = (Button)findViewById(R.id.addRating);		
		
		// New comment text box
		newComment = (EditText)findViewById(R.id.newComment);
		
		// Post comment button
		postComment = (Button)findViewById(R.id.postComment);
		postComment.setText("Post Comment");
		
		// Prepare comment list adapter to load comments
		commentList = (ListView)findViewById(R.id.commentList);
		final CommentListAdapter adapter = new CommentListAdapter(this, location.getKey());
		adapter.getComments();
	    commentList.setAdapter(adapter);
	    
		
	    // Listener for post comment button
		postComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Checks if user entered anything into textbox
				if(!newComment.getText().toString().matches("")) {
					new postComment().execute();
					
					//Must do in order to wait for server response
					//there is probably a better way to do it
					while(success == 0);
					
					//Check for successful add from server
					if(success == 1) {
						comments.add(newComment.getText().toString());
						adapter.add(newComment.getText().toString());
						newComment.setText(null);
					}
					else {
						Log.e("GLACIER","Error retrieving data from server");
					}
				}
				else {
				  // Create Toast message if user tries to submit comment without entering anything
				  Context context = getApplicationContext();
				  CharSequence text = "Please enter a valid comment.";
				  int duration = Toast.LENGTH_SHORT;

				  Toast toast = Toast.makeText(context, text, duration);
				  toast.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_info_window, menu);
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
	
	// Dialog box with input pops up asking user to input rating
	public void addRating(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Rating");
		alert.setMessage("Enter a whole number between 0 and 5 ");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			int value = 0;
			try {
			  value = Integer.parseInt(input.getText().toString());
			}
			catch (NumberFormatException e) {
			  value = -1;
			}
			
//			if(input.getText().toString() != "") {
//				value = Integer.parseInt(input.getText().toString());
//			}
//			else {
//				value = -1;
//			}
			
			// Checks for valid input
			if(value == 0 || value == 1 || value == 2 || value == 3 || value == 4 || value == 5) {
				PostRating post = new PostRating(value,location.getRating(),location.getKey(),location.getNumberOfRatings());
				
				// Waits for rating to be changed for server
				if(post.isDone()) {
					// Sets location's new rating and updates rating text view
					location.setRating(post.getUpdatedRating());
					rating.setText(post.getUpdatedRating() + " / 5 stars of " + location.getNumberOfRatings() + " ratings");
					
					// Packages new rating and location i in array of locations to return to map activity
					Intent intent = new Intent();
					intent.putExtra("rating",post.getUpdatedRating());
					intent.putExtra("i",iLoc);
					setResult(RESULT_OK, intent);
				}
			}
			else {
				// Display Toast message if user enters invalid input
			    Context context = getApplicationContext();
	        	CharSequence text = "Please enter a whole number between 0 and 5";
	        	int duration = Toast.LENGTH_SHORT;

	        	Toast toast = Toast.makeText(context, text, duration);
	        	toast.show();
			}
		}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	// When back button is pressed, map activity checks for result and this activity closes
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	// AsyncTask to post comment to server
	private class postComment extends AsyncTask<Void, Integer, Void> {
			
		//Displays a progress pinwheel
		protected void onPreExecute() {
    		pDialog = new ProgressDialog(LocationInfoWindow.this);
    		pDialog.setMessage("Posting comment...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}
		
		//Post comment to database in background
		protected Void doInBackground(Void... args) {
			
			// Packages location ID and comment to send to database
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			String comm = newComment.getText().toString();
			params.add(new BasicNameValuePair("id", Integer.toString(location.getKey())));
			params.add(new BasicNameValuePair("comment", comm));
			json = jsonParser.makeHttpRequest(POST_URL, "POST", params);		

			// Retrieve success response from server
			try {
				success = Integer.parseInt(json.getString(TAG_SUCCESS));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		//After posting new comment close progress dialog
		protected void onPostExecute(Void param) {
			pDialog.dismiss();
		}
	}
}
