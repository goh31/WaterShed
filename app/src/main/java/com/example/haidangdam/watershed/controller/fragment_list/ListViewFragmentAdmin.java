package com.example.haidangdam.watershed.controller.fragment_list;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.haidangdam.watershed.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import model.WaterData;

import static android.R.id.list;

public class ListViewFragmentAdmin extends ListFragment {
    ListView listView;
    public static Location currentLocation;
    GeoQuery geoQuery;
    GeoFire geoFire;
    private static Set<String> waterResourceNearby;
    private static ArrayList<WaterData> waterDataList;
    ListLocationAdapter locationAdapter;
    private DatabaseReference waterDatabaseRef;

    public static ListViewFragmentAdmin newInstance() {
        ListViewFragmentAdmin a = new ListViewFragmentAdmin();
        return a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentLocation = new Location("dummy service");
        View rootView = inflater.inflate(R.layout.list_view_admin_location_layout, container, false);
        listView = (ListView) rootView.findViewById(list);
        waterDatabaseRef = FirebaseDatabase.getInstance().getReference().child("waterResources");
        waterDataList = new ArrayList<>();
        EventBus.getDefault().register(this);
        if (waterDataList.size() > 1) {
            Collections.sort(waterDataList, new Comparator<WaterData>() {
                @Override
                public int compare(WaterData a, WaterData b) {
                    return (int) (distanceFromCurrent(a.getGeoLocation()) - distanceFromCurrent(b.getGeoLocation()));
                }
            });
        }
        Log.d("WaterShed", "set up list view");
        locationAdapter = new ListLocationAdapter(this.getContext(), waterDataList);
        listView.setAdapter(locationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if (waterDataList.get(position) == null) {
                    Log.d("Water Data List", "Water Data List position is null");
                }
                EventBus.getDefault().post(waterDataList.get(position).getGeoLocation());
                Fragment mapFragment = MapFragmentWatershed.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.main_activity_worker_view_pager, mapFragment);
            }
        });
        return rootView;
    }

    /**
     * @param location
     * @return
     */
    private double distanceFromCurrent(GeoLocation location) {
        double radius = 6378137;
        if (location != null) {
            Log.d("Watershed", "Location is null");
        }
        double deltaLat = currentLocation.getLatitude() - location.latitude;
        double deltaLon = currentLocation.getLongitude() - location.longitude;
        double angle = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(deltaLat / 2), 2) +
                        Math.cos(currentLocation.getLatitude()) * Math.cos(location.latitude) +
                        Math.pow(Math.sin(deltaLon / 2), 2)));
        return radius * angle;
    }


    /**
     *
     */
    private void getDatabaseWithLocation() {
        Log.d("WaterShed", "getDatabaseWithLocation intialize");
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                for (String key : waterResourceNearby) {
                    Log.d("Haidang", "Dam");
                    waterDatabaseRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            WaterData data = snapshot.getValue(WaterData.class);
                            waterDataList.add(data);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.d("WaterShed app", "Value Event Listener Error: " + error.getMessage());
                        }

                    });
                }
                locationAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * @param location
     */
    @Subscribe
    public void getLocationDataFromMap(Location location) {
        currentLocation.set(location);
        setupGeoFire();
    }

    private void setupGeoFire() {
        geoFire = new GeoFire(waterDatabaseRef);
        // In the future, let the user decide where they want to be.
        geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),
                currentLocation.getLongitude()), 3);
        waterResourceNearby = new HashSet<String>();
        Log.d("WaterShed app", "setupGeoFire");
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String locationName, GeoLocation location) {
                waterResourceNearby.add(locationName);
                Log.d("WaterShed", "There is a location added: " + locationName);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("WaterShed app", "There is an error: " + error.getMessage());
            }

            @Override
            public void onKeyExited(String key) {
                waterResourceNearby.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // not implement this method since our location is not moving (might change if user move)
            }

            @Override
            public void onGeoQueryReady() {
                getDatabaseWithLocation();
                Log.d("WaterShed app", "All key data has been loaded and events have bee fired");
            }
        });
    }


    public static class ListLocationAdapter extends ArrayAdapter<WaterData> {
        private Context ctx;
        ArrayList<WaterData> water;
        public ListLocationAdapter(Context ctx, ArrayList<WaterData> waterData) {
            super(ctx, 0, waterData);
            water = waterData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("Watershed", "create list view element");
            WaterData waterData = water.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_view,
                        parent, false);
            }
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_view_item_text);
            TextView waterLevelTextView = (TextView) convertView.findViewById(R.id.water_level_item_text);
            nameTextView.setText(waterData.getName());
            waterLevelTextView.setText("Water Drinking Level: " + waterData.DrinkingLevel());
            return convertView;
        }
    }


}
