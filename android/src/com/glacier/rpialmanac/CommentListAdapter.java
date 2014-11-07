package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListAdapter extends ArrayAdapter<String>{

	private static final String RETRIEVE_URL = "http://glacier.net76.net/retrieveComments.php";
	private Context context;
	ArrayList<String> comments = new ArrayList<String>();
	int locId;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	//JSON object from server
	JSONObject json;
	
	public CommentListAdapter(Context context,int id) {
		super(context,R.layout.rowlayout,id);
		this.context = context;
		locId = id;
		Log.v("GLACIER","In adapter");
		new CommentRetriever().execute();
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		
		//Check if the convertview is null, if it is null it probably means that this 
		//is the first time the view has been displayed
		if(convertView == null) {
			convertView = View.inflate (context,R.layout.rowlayout, null);
		}
		
		//If it is not null, you can just reuse it from the recycler
		TextView textView = (TextView) convertView.findViewById(R.id.textView1);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imgView1);
	    
	    String comm = comments.get(position);
	    textView.setText(comm);

	    return convertView;
	  }
	
	private ArrayList<String> parseCommentsFromJson(JSONObject jobj) {
		ArrayList<String> comm = new ArrayList<String>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jobj);
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
				String comment = jsonObject.getString("comment");
				Log.v("GLACIER", comment);
				comm.add(comment);
			} catch (Exception e) {
				// JSON or number format issue
				Log.w("GLACIER", "3");
				return null;
			}
		}
		
		return comm;
	}
	
	 //AsyncTask to retrieve all existing comments in the SQL database
	private class CommentRetriever extends AsyncTask<Void, Integer, Void> {
		
		//Displays a progress pinwheel
		protected void onPreExecute() {
    		
    	}
		
		//In the background, call RetrieveLocations class
		//Fill array of Locations with array from database
		protected Void doInBackground(Void... args) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", Integer.toString(locId)));
			
			//Probably want to change to get at some point, but get does not work with params in JSONParser.java
			json = jsonParser.makeHttpRequest(RETRIEVE_URL, "POST", params);
			
			// ******FIX ME********
			//Only prints one comment. Probably because JSONParser.java only returns jsonobject with first comment
			Log.v("GLACIER",json.toString());
			comments = parseCommentsFromJson(json);
			
			//notifyDataSetChanged();
			
			return null;
		}
		
		//After retrieving all locations, display all pins on map and close progress dialog
		protected void onPostExecute(Void param) {
			
		}
	}
}
