package com.example.haidangdam.watershed.controller.fragment_list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.haidangdam.watershed.R;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by haidangdam on /19/17.
 * A Map fragment for
 */

public class MapFragmentWatershed extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_CODE = 2;
    private final int CITY = 15;
    MapView mapView;
    GoogleMap gMap;
    LocationRequest locationRequest;
    Location location;
    GeoLocation destinationLocation;
    private boolean permissionDenied = false;
    private GoogleApiClient mGoogleApiClient;
    int callMapDraw = 0;
    int time = 0;
    /**
     * Instantiate the fragment
     *
     * @return
     */
    public static MapFragmentWatershed newInstance() {
        MapFragmentWatershed mapFragment = new MapFragmentWatershed();
        return mapFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        Log.d("Watershed", "Create map fragment view");
        View v = inflator.inflate(R.layout.map_fragment_layout, container, false);
        mapView = (MapView) v.findViewById(R.id.mapview_admin_fragment_layout);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        //get MapView display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
           public void onMapReady(GoogleMap googleMap) {
                Log.d("Watershed", "Calling map async");
                gMap = googleMap;
                gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        gMap.getUiSettings().setAllGesturesEnabled(true);
                        gMap.setMyLocationEnabled(true);
                        return false;
                    }
                });

            }
        });
        createLocationRequest();

        putUserInCurrentLocation();

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * Put the map point at the current location of the user
     */
    private void putUserInCurrentLocation() {
        if (location != null && time == 0) {
            Log.d("Location", "Location is not null");
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            EventBus.getDefault().post(location);
            gMap.addMarker(new MarkerOptions().position(current).title("Current location"));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, CITY));
            time++;
        } else {
            Log.d("Location", "not going to the if");
        }
    }

    /**
     * Create location request for the map
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        connectGoogleApiClient();
    }

    /**
     * Connect with the Google API
     */
    private void connectGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).
                    addApi(LocationServices.API)
                    .addConnectionCallbacks(this).build();
        }
        checkLocationSetting();
        mGoogleApiClient.connect();
    }

    /**
     * Check to see if the user have set the GPS. If the user did not => make a dialog to ask the permission
     * to set the GPS to make it faster to get the location from the user.
     */
    private void checkLocationSetting() {
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.
                checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult results) {
                final Status status = results.getStatus();
                final LocationSettingsStates locationSettingsStates = results.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    /**
     * Callback when calling the dialog
     *
     * @param requestCode the request code for checking the GPS from the device
     * @param resultCode  the result from the callback
     * @param data        any data that might be insert while calling back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult: ", "goToOnActivityResult");
        switch (requestCode) {
            case REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "Location enable by user", Toast.LENGTH_LONG).show();
                        mGoogleApiClient.connect();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), "Unsuccessful to enable location", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }

    /**
     * When the device succesfully connect to the api (client is ready)
     *
     * @param connetionHint
     */
    @Override
    public void onConnected(Bundle connetionHint) {
        Log.d("Connected", "Google API connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    /**
     * Callback from the onConnected method to get the location from the request
     *
     * @param location The location changed from the onConnected method
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        putUserInCurrentLocation();
    }

    /**
     * connect When the device unsuccessfully connected
     *
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Connection failed", "Connection failed:" + result.getErrorMessage());
        Toast.makeText(getContext(), "Connection failed: " + result.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("Connection suspended: ", "Connection suspended");
        mGoogleApiClient.connect();
    }

    /*
     * Calling this method when the fragment is first started (check the lifecycle for more information)
     */
    //@Override
    //public void onStart() {
        //super.onStart();

    //}


     @Subscribe
     public void getNewLocation(GeoLocation loc) {
        if (callMapDraw == 0) {
            destinationLocation = loc;
            String url = drawPolylineOnMapURL();
            gMap.addMarker(new MarkerOptions().position(new LatLng(destinationLocation.latitude,
                destinationLocation.longitude)).title("stination"));
            callMapDraw++;
            MyAsyncTaskMapDownloading myAsyncTask = new MyAsyncTaskMapDownloading(getApplicationContext());
            myAsyncTask.execute(drawPolylineOnMapURL());
        }

     }

     /**
     * This method use to insert the query url for Google Map to return back regarding the user location
     * and his/her choice's destination.
     *
     * @returnMap String url for google map request
     */
    private String drawPolylineOnMapURL() {
        String str_origin = "origin="+location.getLatitude() +","+ location.getLongitude();
        String str_dest = "destination="+destinationLocation.latitude+","+destinationLocation.longitude;
        String sensor = "sensor=false";

        //Adding Alternative parameter

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&"
                + "mode=walking";
        Log.d("Watershed app", "Google Map: " + url);
        return url;
    }

    /**
     * Doing this background thread to post the url to the internet and receive the
     * result back
     */
    private class MyAsyncTaskMapDownloading extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Context context;

        public MyAsyncTaskMapDownloading(Context context) {
            this.context = context;
        }

        /**
         * Show the progress dialog when doing the background thread.
         */
        @Override
        protected void onPreExecute() {
            Log.d("Watershed app", "Watershed app asynctask");
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Download from internet");
            progressDialog.show();
        }

        /**
         * Do this in the background to "talk" to Google Data
         *
         * @param args the url that is given to query google
         * @return the data that google return
         */
        @Override
        protected String doInBackground(String... args) {
            String data = "";
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            try {
                data = downloadFromGoogle(args[0]);
            } catch (Exception e) {
                Log.d("Watershed Error:", "Exception while reading: " + e.toString());
            }
            return data;
        }

        /**
         * Downloading data from google
         *
         * @param args the url to "ask" Google
         * @return the String representing the "answer"
         * @throws IOException
         */
        private String downloadFromGoogle(String args) throws IOException {
            String data = "";
            InputStream stream = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(args);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                stream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuffer sb = new StringBuffer();
                String stringInBr = "";
                while ((stringInBr = br.readLine()) != null) {
                    sb.append(stringInBr);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("Map Watershed", "Error while downloading map data from google server" + e.toString());
            } finally {
                stream.close();
                connection.disconnect();
            }
            return data;
        }

        /**
         * This is called after finishing the background thread. Here, we called another background thread to decode
         * the JSON result that we received from the google
         *
         * @param result the result receive back from the background
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parse = new ParserTask();
            parse.execute(result);
            progressDialog.dismiss();
        }
    }

    /**
     * This background class is used to draw the polyline based on the String that we receive from
     */
    private class ParserTask extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routesGoogleMap = new ArrayList<>();
            try {
                jObject = new JSONObject(jsonData[0]);

                // Starts parsing data
                routesGoogleMap = parse(jObject);
            } catch (Exception e) {
                Log.d("Parser Task", "Error in parser task while decoding JSON from google map: " + e.toString());
            }
            return routesGoogleMap;
        }

        private List<List<HashMap<String, String>>> parse(JSONObject jobject) {
            List<List<HashMap<String, String>>> routesJSON = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jsonRoutes;
            JSONArray jsonLegs;
            JSONArray jsonSteps;
            try {
                jsonRoutes = jobject.getJSONArray("routes");
                for (int i  = 0; i < jsonRoutes.length(); i++) {
                    jsonLegs = (JSONArray) ((JSONObject) jsonRoutes.get(i)).get("legs");
                    List<HashMap<String, String>> legsJSONroutes = new ArrayList<>();
                    for (int t = 0; t < jsonLegs.length(); t++) {
                        jsonSteps = (JSONArray) ((JSONObject) jsonLegs.get(t)).get("steps");
                        for (int k = 0; k < jsonSteps.length(); k++) {
                            String polyline = (String) ((JSONObject) (((JSONObject) jsonSteps.get(k)).get("polyline"))).get("points");
                            Log.d("Watershed app", "Current point in steps JSON " + k);
                            List<LatLng> decoded = decodePoly(polyline);
                            for (LatLng a : decoded) {
                                HashMap<String, String> pointsDecoded = new HashMap<>();
                                pointsDecoded.put("lat", "" + a.latitude);
                                pointsDecoded.put("lng", "" + a.longitude);
                                legsJSONroutes.add(pointsDecoded);
                            }
                        }
                    }
                    routesJSON.add(legsJSONroutes);

                }
            } catch (Exception e) {
                Log.d("Parse JSON ParserTask", "Error while parsing JSON " + e.toString());
            }
            return routesJSON;
        }

        /**
         * Decode the string code based on algorithm
         *
         * @param encoded the String code
         * @return List of LatLng position for the
         */
        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> decodedPath = PolyUtil.decode(encoded);
            return decodedPath;
        }

        // Draw the Polyline on the Map
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = new PolylineOptions();
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                Log.d("Watershed app", "Draw Polyline");
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.RED);

                Log.d("Parser Task Map", "onPostExecute finish modifying lineOptions");
            }

            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
            } else {
                Log.d("Parser Task Map", "polyline not drawn because it is null");
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

}