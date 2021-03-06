package com.glacier.rpialmanac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetrieveLocations {
  // Constant for server interaction
  private static final String RETRIEVE_URL = "http://glacier.net76.net/retrieve.php";

  // JSON object
  static JSONObject jobj;

  // Retrieves locations and returns arraylist of locations
  public static ArrayList<Location> getAllLocations(Void... params) {
    String JSON = retrieveJSON();
    if (JSON == null) {
      return null;
    }

    ArrayList<Location> locations = parseLocationsFromJSON(JSON);

    return locations;
  }

  // Retrieve locations from server in JSON string
  private static String retrieveJSON() {
    HttpClient client = new DefaultHttpClient();
    HttpResponse response = null;
    try {
      response = client.execute(new HttpGet(RETRIEVE_URL));
    } catch (IOException e) {
      // For any error when trying to get the HTTP response
      return null;
    }
    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      // Some other server-side issue
      return null;
    }

    // OutputStream to get response from server
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      response.getEntity().writeTo(stream);
      stream.close();
    } catch (IOException e) {
      return null;
    }

    String dirtyJSON = stream.toString();
    int endIndex = dirtyJSON.indexOf(']');
    if (endIndex == -1) {
      // Something is wrong with our JSON
      return null;
    }

    // Removes excess characters from server analytics
    String JSON = dirtyJSON.substring(0, dirtyJSON.indexOf(']') + 1);

    return JSON;
  }

  // Parse each location out of entire JSON response from server
  private static ArrayList<Location> parseLocationsFromJSON(String JSON) {
    ArrayList<Location> locations = new ArrayList<Location>();
    JSONArray jsonArray;

    // Converts JSON string into a JSON array
    try {
      jsonArray = new JSONArray(JSON);
    } catch (JSONException e) {
      // Bad JSON
      return null;
    }

    // Iterates through JSON array, makes JSON object out of each array entry
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject;
      try {
        jsonObject = jsonArray.getJSONObject(i);
      } catch (JSONException e) {
        // Bad JSON
        return null;
      }

      // Parses location data from JSON object
      try {
        Location location = new Location(jsonObject.getString("name"), Double.parseDouble(jsonObject.getString("latitude")),
            Double.parseDouble(jsonObject.getString("longitude")), jsonObject.getString("address"), jsonObject.getString("type"), 
            jsonObject.getInt("id"), Double.parseDouble(jsonObject.getString("rating")), jsonObject.getInt("numberOfRatings"));
        locations.add(location);
      } catch (Exception e) {
        // JSON or number format issue
        return null;
      }
    }

    return locations;
  }
}
