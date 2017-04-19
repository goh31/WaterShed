package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.fragment_list.ListViewFragmentAdmin;
import com.example.haidangdam.watershed.controller.fragment_list.MapFragmentWatershed;
import com.example.haidangdam.watershed.controller.fragment_list.ProfileFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import model.User;

/**
 * Created by haidangdam on 2/18/17.
 */

public class MainActivity extends AppCompatActivity {

  public static final String ARRAY_LIST_KEY = "stringArray";
  static final int NUM_COUNTS = 3;
  private ViewPager viewPager;
  private TabLayout tabLayout;
  private MapFragmentWatershed mapFragment;
  private MyAdapter myAdapter;
  private Toolbar toolBar;
  private DatabaseReference userDatabaseref;
  private GoogleApiClient mGoogleApiClient;
  private FirebaseUser user;
  private User userData;
  private int a = 0;
  private String credential = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity_worker);
    viewPager = (ViewPager) findViewById(R.id.main_activity_worker_view_pager);
    viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), MainActivity.this));
    myAdapter = (MyAdapter) viewPager.getAdapter();
    mapFragment = myAdapter.getMapFragment();
    viewPager.setOffscreenPageLimit(2);
    tabLayout = (TabLayout) findViewById(R.id.main_activity_worker_tab_layout);
    tabLayout.addTab(tabLayout.newTab().setText("Map"));
    tabLayout.addTab(tabLayout.newTab().setText("Location"));
    tabLayout.addTab(tabLayout.newTab().setText("Profile"));
    tabLayout.setupWithViewPager(viewPager);
    toolBar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(toolBar);
    user = FirebaseAuth.getInstance().getCurrentUser();
    userDatabaseref = FirebaseDatabase.getInstance().getReference().child("userID")
        .child(user.getUid());
    userDatabaseref.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot ds) {
        Log.d("Watershed app", "abc");
        userData = ds.getValue(User.class);
        credential = userData.getCredential();
      }

      @Override
      public void onCancelled(DatabaseError error) {
        Log.d("Watershed app", "dbRef error: " + error.getMessage());
      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_bar, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_add: {
        Intent intent;
        Log.d("Watershed", "Touch the add button");
        if (user == null) {
          Log.d("User is 123", ""+ credential);
        }
        if (userData.getCredential().equals("User")) {
          intent = new Intent(MainActivity.this, AddReportUserActivity.class);
        } else {
          intent = new Intent(MainActivity.this, AddReportWorkerControl.class);
        }
        Bundle bundle = new Bundle();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(myAdapter.getListViewFragmentAdmin().getListWaterResourceNearby());
        if (arrayList.size() != 0) {
          Log.d("Watershed", "Array List size: " + arrayList.size());
        }
        bundle.putStringArrayList(ARRAY_LIST_KEY, arrayList);
        intent.putExtras(bundle);
        startActivity(intent);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);

    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == MapFragmentWatershed.REQUEST_CODE) {
      mapFragment.onActivityResult(requestCode, resultCode, intent);
    } else {
      super.onActivityResult(requestCode, resultCode, intent);
    }
  }

  /**
   * Set tab to change the map fragment from list view
   *
   * @param fragment the map fragment that we get from the list view
   */
  public void changeToMapFragment(MapFragmentWatershed fragment) {
    Log.d("Watershed app", "Change To Map Fragment");
    myAdapter.setMapFragment(fragment);
    viewPager.setCurrentItem(0, true);
  }

  /**
   * Return MapFragmentWatershed object in the activity in case any class needs to access
   *
   * @return MapFragmentWatershed object
   */
  public MapFragmentWatershed getMapFragment() {
    if (mapFragment == null) {
      Log.d("Watershed app", "Map Fragment is null");
    }
    return mapFragment;
  }

  /**
   * Create a Fragment Pager Adapter for the list of fragment under the tab
   */
  public static class MyAdapter extends FragmentPagerAdapter {

    private MapFragmentWatershed mapFragmentMyAdapter;
    private ListViewFragmentAdmin listViewFragment;
    private ProfileFragment profileFragment;
    private Context ctx;
    private String[] field = new String[]{"Map", "Location", "Profile"};

    /**
     * Constructor for Fragment Pager Adapter
     *
     * @param fm Fragment Manager from the current activity
     * @param ctx The context of the current activity
     */
    public MyAdapter(FragmentManager fm, Context ctx) {
      super(fm);
      this.ctx = ctx;
    }

    @Override
    public int getCount() {
      return NUM_COUNTS;
    }


    @Override
    public Fragment getItem(int position) {
      Log.d("Watershed app", "Get Item in Main Activity");
      switch (position) {
        case 0:
          if (mapFragmentMyAdapter == null) {
            Log.d("Watershed main activity", "Create new map fragment");
            mapFragmentMyAdapter = MapFragmentWatershed.newInstance();
          }
          return mapFragmentMyAdapter;
        case 1:
          if (listViewFragment == null) {
            listViewFragment = ListViewFragmentAdmin.newInstance();
          }
          return listViewFragment;
        case 2:
          if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance();
          }
          return profileFragment;
        default:
          return null;
      }
    }

    /**
     * Get the map fragment currently exist in the main activity
     *
     * @return MapFragment object
     */
    public MapFragmentWatershed getMapFragment() {
      return mapFragmentMyAdapter;
    }

    /**
     * Set the map Fragment to the new map fragment when we update the map with direction
     */
    public void setMapFragment(MapFragmentWatershed mapFragment) {
      this.mapFragmentMyAdapter = mapFragment;
      ((Activity) ctx).getFragmentManager().popBackStackImmediate();
    }

    /**
     * Get the ListViewFragmentAdmin that currently exist in the main activity
     *
     * @return ListViewFragmentAdmin object
     */
    public ListViewFragmentAdmin getListViewFragmentAdmin() {
      return listViewFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return field[position];
    }

  }
}


