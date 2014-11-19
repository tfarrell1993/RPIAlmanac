package com.glacier.rpialmanac;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

public class PostRating {

  // Constants for server interaction
  private static final String POST_URL = "http://glacier.net76.net/postRating.php";
  private static final String TAG_SUCCESS = "success";

  // Rating variables and server IO
  private static int newRating = 0;
  private int success;
  private int locId;
  private double updatedRating;
  private double currentRating;
  private static int numberOfRatings;

  // JSON parser class
  JSONParser jsonParser = new JSONParser();
  //JSON object from server
  JSONObject json;

  public PostRating(int rating, double curRate, int id, int numRatings) {
    newRating = rating;
    locId = id;
    numberOfRatings = numRatings;
    currentRating = curRate;
    new postNewRating().execute();
  }

  public double getUpdatedRating() {
    return updatedRating;
  }

  // Returns when the success response from the server is retrieved
  public boolean isDone() {
    while(success != 1) {}
    return true;
  }

  // Calculates new rating and rounds to one decimal place
  private void updateRating() {
    double tempUpdated = ((currentRating * numberOfRatings) + newRating) / (numberOfRatings + 1);
    tempUpdated = Math.round(tempUpdated * 10);
    updatedRating = tempUpdated / 10;
  }

  // AsyncTask to post comment to server
  private class postNewRating extends AsyncTask<Void, Integer, Void> {
    //Post comment to database in background
    protected Void doInBackground(Void... args) {
      // Update location's rating
      updateRating();
     
      // Packages location ID and comment to send to database
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("id", Integer.toString(locId)));
      params.add(new BasicNameValuePair("rating", String.valueOf(updatedRating)));
      json = jsonParser.makeHttpRequest(POST_URL, "POST", params);

      // Retrieve success response from server
      try {
        success = Integer.parseInt(json.getString(TAG_SUCCESS));
      } catch (Exception e) {
        // (Unsuccessful)
      }
      return null;
    }
  }
}
