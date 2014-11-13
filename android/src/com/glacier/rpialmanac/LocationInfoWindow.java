package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
	
	private static final String POST_URL = "http://glacier.net76.net/postComment.php";
	private static final String TAG_SUCCESS = "success";
	private ProgressDialog pDialog;
	private TextView name, addr, type;
	private EditText newComment;
	private Button postComment;
	private ListView commentList;
	private int success = 0;
	private ArrayList<String> comments = new ArrayList<String>();
	Location location;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	//JSON object from server
	JSONObject json;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_info_window);
		
		//Convert bundled location object from clicked info window into string
		String jsonMyObject = "";
		Bundle b = getIntent().getExtras();
		if (b != null) {
		   jsonMyObject = b.getString("jsonLocation");
		}
		location = new Gson().fromJson(jsonMyObject, Location.class);
		
		name = (TextView)findViewById(R.id.locName);
		name.setText(location.getName());
		addr = (TextView)findViewById(R.id.locAddr);
		addr.setText("  " + location.getAddress());
		type = (TextView)findViewById(R.id.locType);
		type.setText("  " + location.getLocationType());
		
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
				// TODO Auto-generated method stub
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
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void displayNewComment() {
		
		
	}
	
	private class postComment extends AsyncTask<Void, Integer, Void> {
			
		//Displays a progress pinwheel
		protected void onPreExecute() {
    		pDialog = new ProgressDialog(LocationInfoWindow.this);
    		pDialog.setMessage("Posting comment...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}
		
		//Post comment to database
		protected Void doInBackground(Void... args) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			String comm = newComment.getText().toString();
			params.add(new BasicNameValuePair("id", Integer.toString(location.getKey())));
			params.add(new BasicNameValuePair("comment", comm));
			json = jsonParser.makeHttpRequest(POST_URL, "POST", params);		

			try {
				success = Integer.parseInt(json.getString(TAG_SUCCESS));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//displayNewComment();
			
			return null;
		}
		
		//After posting new comment close progress dialog
		protected void onPostExecute(Void param) {
			pDialog.dismiss();
		}
	}
}
