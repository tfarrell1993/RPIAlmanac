package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class PostRating {

  // Constants for server interaction
  private static final String POST_URL = "http://glacier.net76.net/postRating.php";
  private static final String TAG_SUCCESS = "success";

  private static int newRating = 0;
  private int success, locId;
  private double updatedRating, currentRating;
  private static int numberOfRatings;

  // JSON parser class
  JSONParser jsonParser = new JSONParser();
  //JSON object from server
  JSONObject json;

  public PostRating(int r, double curRate, int id, int numRatings) {
    newRating = r;
    locId = id;
    numberOfRatings = numRatings;
    currentRating = curRate;
    new postNewRating().execute();
  }

  public double getUpdatedRating() {
    return updatedRating;
  }

  public boolean isDone() {
    while(success != 1);
    return true;
  }

  private void updateRating() {
    double tempUpdated = ((currentRating*numberOfRatings)+newRating)/(numberOfRatings+1);
    tempUpdated = Math.round(tempUpdated*10);
    updatedRating = tempUpdated/10;
  }

  // AsyncTask to post comment to server
  private class postNewRating extends AsyncTask<Void, Integer, Void> {

    //Post comment to database in background
    protected Void doInBackground(Void... args) {

      updateRating();
      Log.v("GLACIER","Updated Rating in post: " + updatedRating);

      // Packages location ID and comment to send to database
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("id", Integer.toString(locId)));
      params.add(new BasicNameValuePair("rating", String.valueOf(updatedRating)));
      json = jsonParser.makeHttpRequest(POST_URL, "POST", params);		

      // Retrieve success response from server
      try {
        success = Integer.parseInt(json.getString(TAG_SUCCESS));
        Log.v("GLACIER", "success" + success);
      } catch (NumberFormatException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;
    }
  }
}
