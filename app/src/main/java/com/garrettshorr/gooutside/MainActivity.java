package com.garrettshorr.gooutside;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView mLocationText;
    private GoogleApiClient mGoogleApiClient;
    private List<AdventurePlace> places;

    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationText = (TextView) findViewById(R.id.text_location);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
       places = new ArrayList<>();
        try {
            JSONObject test = new JSONObject(Credentials.TEST_JSON);
             places =
                    AdventurePlace.parseJSONAdventurePlaces(test);
            mLocationText.setText(places.toString());
        } catch (Exception e) {
            mLocationText.setText("STUPID");
            e.printStackTrace();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            Log.e(TAG, "SO MUCH NULL :(");
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.e(TAG, "LOOK AT ME: " + location.toString());
//        mLocationText.setText(location.toString());
//        AdventureFinder af = new AdventureFinder(location.getLatitude(), location.getLongitude());
//        af.execute();

        AdventurePlace a = places.get(1);
        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+location.getLatitude()+","+
                location.getLongitude()+"&daddr="+a.getLatitude()+","
                +a.getLongitude();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(Intent.createChooser(intent, "Select an application"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private class AdventureFinder extends AsyncTask<Void, Void, String> {

        private double latitude, longitude;
        private String jsonText;

        public AdventureFinder(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            jsonText = "";
        }

        @Override
        protected String doInBackground(Void... params) {
            findAdventures(latitude, longitude);
            return null;
        }

        private void findAdventures(double latitude, double longitude) {
            URL url = null;
            String urlStr = createApiCall(latitude, longitude);
            try {
                url = new URL(urlStr);
                Reader reader = new InputStreamReader(url.openStream());
                Scanner s = new Scanner(reader);
                //String json = "";
                while (s.hasNextLine()) {
                    jsonText += s.nextLine();
                }
                JSONObject result = new JSONObject(jsonText);
                jsonText = result.get("name").toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //mLocationText.setText(jsonText);
        }

        private String createApiCall(double latitude, double longitude) {
            return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    latitude + "," + longitude + "&radius=1000000&type=restaurant&name=cruise&key=" +
                    Credentials.API_KEY;
        }
    }
}