package com.glacier.rpialmanac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListAdapter extends ArrayAdapter<String>{

  // Constant for server interaction
  private static final String RETRIEVE_URL = "http://glacier.net76.net/retrieveComments.php";

  private Context context;
  ArrayList<String> comments = new ArrayList<String>();
  int locId;
  private String cleanJson = "";

  // JSON parser class
  JSONParser jsonParser = new JSONParser();
  //JSON object from server
  JSONObject json;

  // Adapter constructor
  public CommentListAdapter(Context context,int id) {
    super(context,R.layout.rowlayout,id);
    this.context = context;
    locId = id;
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return comments.size();
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

  public void getComments() {
    comments.clear();

    //Retrieve comments
    new CommentRetriever().execute();

    // Waits for response from server, should find better way to do this
    while(cleanJson == "");

    // adds comma between curly braces so it can be made into jsonArray without errors
    cleanJson = cleanJson.replace("}{","},{");

    // Parse comments from json
    comments = parseCommentsFromJson(cleanJson);

    // Notifies adapter that new data was added
    notifyDataSetChanged();
  }

  // Called when new comment is added
  // Adds it to arraylist of comments
  @Override
  public void add(String newComment) {
    comments.add(0,newComment);

    // Notifies adapter that new data was added
    notifyDataSetChanged();
  }

  //Also probably good idea to add a date column to comment database so it can be displayed with each comment
  private ArrayList<String> parseCommentsFromJson(String json) {
    ArrayList<String> comm = new ArrayList<String>();
    JSONArray jsonArray;
    try {
      jsonArray = new JSONArray(json);
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
        comm.add(0,comment);
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

    //In the background, call RetrieveLocations class
    //Fill array of Locations with array from database
    protected Void doInBackground(Void... args) {
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("id", Integer.toString(locId)));
      HttpClient httpClient = new DefaultHttpClient();
      HttpResponse httpResponse = null;
      try {	

        HttpPost httpPost = new HttpPost(RETRIEVE_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient.execute(httpPost);	
      } catch (ClientProtocolException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        // Some other server-side issue
        return null;
      }

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      try {
        httpResponse.getEntity().writeTo(stream);
        stream.close();
      } catch (IOException e) {
        return null;
      }

      String dirtyJSON = stream.toString();
      String s2 = "[" + dirtyJSON;

      String s3 = s2.substring(0, s2.indexOf('<') - 1);
      cleanJson = s3 + "]";

      return null;
    }
  }
}
