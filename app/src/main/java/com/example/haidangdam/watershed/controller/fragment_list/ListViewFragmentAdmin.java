package com.example.haidangdam.watershed.controller.fragment_list;


import static android.R.id.list;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.MainActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.WaterData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class ListViewFragmentAdmin extends Fragment {

  public static Location currentLocation;
  private static MainActivity instanceMain;
  RecyclerView recyclerView;
  GeoQuery geoQuery;
  GeoFire geoFire;
  ListLocationAdapter locationAdapter;
  int alreadyStart = 0;
  boolean allowToStart = false;
  private Set<String> waterResourceNearby;
  private ArrayList<WaterData> waterDataList;
  private DatabaseReference waterDatabaseRef;

  public static ListViewFragmentAdmin newInstance() {
    ListViewFragmentAdmin a = new ListViewFragmentAdmin();
    return a;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Log.d("WaterShed", "Start at onCreateView");
    allowToStart = true;
    currentLocation = new Location("dummy service");
    View rootView = inflater
        .inflate(R.layout.recycler_list_view_admin_location_layout, container, false);
    recyclerView = (RecyclerView) rootView.findViewById(list);
    waterDatabaseRef = FirebaseDatabase.getInstance().getReference().child("waterResources");
    waterDataList = new ArrayList<>();
    EventBus.getDefault().register(this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setOrientation(layoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);
    locationAdapter = new ListLocationAdapter(getActivity(), waterDataList);
    recyclerView.setAdapter(locationAdapter);
    instanceMain = (MainActivity) getActivity();
    return rootView;
  }

  /**
   * @param location
   * @return
   */
  private double distanceFromCurrent(GeoLocation location) {
    double radius = 6378137;
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
    waterDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        Log.d("Watershed app", "Add Location");
        for (String key : waterResourceNearby) {
          waterDatabaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshotChild) {
              WaterData data = snapshotChild.getValue(WaterData.class);
              waterDataList.add(data);
              getActivity().runOnUiThread(new Runnable() {
                public void run() {
                  locationAdapter.notifyDataSetChanged();
                }
              });
            }

            @Override
            public void onCancelled(DatabaseError error) {
              Log.d("WaterShed app", "Value Event Listener Error: " + error.getMessage());
            }
          });
        }
        Log.d("WaterShed app", "Finish retrieving data from database");
      }

      @Override
      public void onCancelled(DatabaseError error) {
        Log.d("WaterShed app", "Value Event Listener Error: " + error.getMessage());
      }
    });
    Log.d("WaterShed app", "Test water data list size " + waterDataList.size());
  }

  /**
   * Communicating with map to get the current location
   *
   * @param location The location that got from the map indicating the current location
   */
  @Subscribe
  public void getLocationDataFromMap(Location location) {
    if (allowToStart) {
      if (alreadyStart == 0) {
        Log.d("Watershed app", "get Location From Map");
        currentLocation.set(location);
        setupGeoFire();
        alreadyStart++;
      }
    }
  }

  /**
   * Set up GeoFire to query the current location and find a list of location nearby
   */
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
        EventBus.getDefault().post(waterResourceNearby);
        Log.d("WaterShed app", "All key data has been loaded and events have bee fired");
        geoQuery.removeAllListeners();
      }
    });
  }

  public Set<String> getListWaterResourceNearby() {
    return waterResourceNearby;
  }

  /**
   * Recycler View Adapter class that we implement for the list view
   */
  public static class ListLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int position;
    private List<WaterData> waterDataList;
    private Context mContext;

    /**
     * Public constructor for the adapter
     *
     * @param context the current context of the activity
     * @param waterDataList the list of water data that we get from GeoFire
     */
    public ListLocationAdapter(Context context, List<WaterData> waterDataList) {
      this.waterDataList = waterDataList;
      this.mContext = context;
    }

    @Override
    public ListLocationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_list_view, null);
      position = i;
      Log.d("Watershed app", "Press at the location " + i);
      ListLocationViewHolder viewHolder = new ListLocationViewHolder(view);
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          EventBus.getDefault().post(waterDataList.get(position));
          MapFragmentWatershed mapFragment = MapFragmentWatershed.newInstance();
          FragmentTransaction transaction = ((FragmentActivity) mContext)
              .getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.main_activity_worker_view_pager, mapFragment); // work but buggy
          transaction.addToBackStack("list");
          transaction.commit();
          callMapFragment(mapFragment);
        }
      });
      return viewHolder;
    }

    /**
     * Calling map fragment to show the direction when pressing to the child in the RecyclerView
     *
     * @param fragment the mapfragment that we created when we add to the stack when clicking on the
     * child
     */
    private void callMapFragment(MapFragmentWatershed fragment) {
      Log.d("Watershed app", "List View call map fragment");
      instanceMain.changeToMapFragment(fragment);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
      WaterData data = waterDataList.get(i);
      ((ListLocationViewHolder) customViewHolder).criticalLevel.
          setText("The Level of water is " + data.getcriticalLevel().
              get(data.getcriticalLevel().size() - 1));
      ((ListLocationViewHolder) customViewHolder).LocationName.setText(data.getlocationName());
    }

    @Override
    public int getItemCount() {
      if (waterDataList != null) {
        Log.d("Watershed app", "Recycler List has data");
        return waterDataList.size();
      } else {
        Log.d("WaterShed app", "Recycler list has no data");
        return 0;
      }
    }


    /**
     * The Recycler View's ViewHolder
     */
    class ListLocationViewHolder extends RecyclerView.ViewHolder {

      protected TextView LocationName;
      protected TextView criticalLevel;

      /**
       * The constructor of the view holder
       *
       * @param itemView the view that we get
       */
      public ListLocationViewHolder(View itemView) {
        super(itemView);
        this.LocationName = (TextView) itemView.findViewById(R.id.name_view_item_text);
        this.criticalLevel = (TextView) itemView.findViewById(R.id.water_level_item_text);
      }


      /**
       * The Location TextView in the child view
       *
       * @return the location TextView in the child view
       */
      public TextView getLocationTextView() {
        return LocationName;
      }

      /**
       * Get the Critical Level TextView in the child view
       *
       * @return the critical level TextView in the child view
       */
      public TextView getCriticalLevelTextView() {
        return criticalLevel;
      }

    }
  }

}
