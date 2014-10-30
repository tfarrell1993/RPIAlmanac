package com.glacier.rpialmanac;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
 
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
 
public class Map extends Activity {
 
    TextView resultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.729861, -73.676767), 16));
        
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
        	@Override
            public void onMapLongClick(LatLng point) {
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude))
                        .title("New Marker")
                        .draggable(true)
                        .snippet("This is a test snippit");
                map.addMarker(marker);
            }
        });
        
        //new AddLocation().execute();
        //resultView = (TextView) findViewById(R.id.result);
        new addLocation().execute("");
        map.addMarker(new MarkerOptions().anchor(0.5f, 1.0f).title("RPI Union").position(new LatLng(42.729861, -73.676767))); // Anchors the marker on the bottom left
    }
    
   
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.map, menu);
                return true;
        }
   
    private class addLocation extends AsyncTask<String, Integer, String>{
       
        public addLocation(){
                //TODO Auto-generated constructor stub
        }
        @Override
        protected String doInBackground(String... params) {
       
                String result = "";
                InputStream isr = null;
               
                try{
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("http://glacier.net76.net/config.php"); //YOUR PHP SCRIPT ADDRESS
                        org.apache.http.HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        isr = entity.getContent();
                }
                catch(Exception e){
                        Log.e("log_tag", "Error in http connection "+e.toString());
                        resultView.setText("Couldnt connect to database");
                }
               
                //convert response to string
                    try{
                            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                             
                            while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                            }
                             
                            isr.close();
                            result=sb.toString();
                    }
                    catch(Exception e){
                        Log.e("log_tag", "Error  converting result "+e.toString());
                    }
                   
                    return result;
        }
                 
                protected void onPostExecute(Double result) {
                        //parse json data
                    try {
                        String s = "";
                        /*JSONArray jArray = new JSONArray(result);
                   
                        for(int i=0; i<jArray.length();i++){
                                JSONObject json = jArray.getJSONObject(i);
                                s = s +"Name : "+json.getString("id")+" "+json.getString("username");  
                        }
                        resultView.setText(s);*/
                    }
                    catch (Exception e) {
                    // TODO: handle exception
                    Log.e("log_tag", "Error Parsing Data "+e.toString());
                    }
        }
    }
    /*private class AddLocation extends AsyncTask<Void, Void, Void> {
       
        @Override
        protected Void doInBackground(Void... params) {
                Location aLocation = new Location();
                               
                return null;
        }
    }*/
}