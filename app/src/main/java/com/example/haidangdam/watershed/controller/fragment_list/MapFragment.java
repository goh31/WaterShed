package com.example.haidangdam.watershed.controller.fragment_list;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.haidangdam.watershed.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by haidangdam on /19/17.
 * A Map fragment for the main activity
 */

public class MapFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    MapView mapView;
    GoogleMap gMap;
    private boolean permissionDenied = false;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    private final int CITY = 15;
    /**
     * Instantiate the fragment
     * @return
     */
    public static MapFragment newInstance() {
        MapFragment mapFragment = new MapFragment();
        return mapFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View v = inflator.inflate(R.layout.map_fragment_layout, container, false);
        mapView = (MapView) v.findViewById(R.id.mapview_admin_fragment_layout);

        mapView.onCreate(savedInstanceState);
        if (savedInstanceState != null) {

        }
        mapView.onResume(); //get MapView display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
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

    /**
     * Put the map point at the current location of the user
     */
    private void putUserInCurrentLocation() {
        if (location != null) {
            Log.d("Location", "Location is not null");
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.addMarker(new MarkerOptions().position(current).title("Current location"));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, CITY));
        } else {
            Log.d("Location", "location is null");
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
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).
                addApi(LocationServices.API)
                .addConnectionCallbacks(this).build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    /**
     * When the device succesfully connect to the api (client is ready)
     * @param connetionHint
     */
    @Override
    public void onConnected(Bundle connetionHint) {
        Log.d("Connected", "Google API connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    /**
     * Callback from the onConnected method to get the location from the request
     * @param location The location changed from the onConnected method
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        putUserInCurrentLocation();
    }

    /**
     * connect When the device unsuccessfully connected
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

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

}