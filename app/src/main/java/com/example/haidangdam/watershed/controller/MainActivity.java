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
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by haidangdam on 2/18/17.
 */

public class MainActivity extends AppCompatActivity {
    static final int NUM_COUNTS = 3;
    ViewPager viewPager;
    TabLayout tabLayout;
    private GoogleApiClient mGoogleApiClient;
    MapFragmentWatershed mapFragment;
    MyAdapter myAdapter;
    Toolbar toolBar;
    public static final String ARRAY_LIST_KEY = "stringArray";

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add: {
                Log.d("Watershed", "Touch the add button");
                Intent intent = new Intent(MainActivity.this, AddReportActivity.class);
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
            default: return super.onOptionsItemSelected(item);

        }
    }

    /**
     *
     */
    public static class MyAdapter extends FragmentPagerAdapter {
        MapFragmentWatershed mapFragmentMyAdapter;
        ListViewFragmentAdmin listViewFragment;
        private String[] field = new String[]{"Map", "Location", "Profile"};
        Context ctx;

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
                    return ListViewFragmentAdmin.newInstance();
                default:
                    return null;
            }
        }

        /**
         *
         * @return
         */
        public MapFragmentWatershed getMapFragment() {
            return mapFragmentMyAdapter;
        }

        public ListViewFragmentAdmin getListViewFragmentAdmin() {
            return listViewFragment;
        }

        /**
         *
         * @param mapFragment
         */
        public void setMapFragment(MapFragmentWatershed mapFragment) {
            this.mapFragmentMyAdapter = mapFragment;
            ((Activity) ctx).getFragmentManager().popBackStackImmediate();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return field[position];
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
     *
     * @param fragment
     */
    public void changeToMapFragment(MapFragmentWatershed fragment) {
        Log.d("Watershed app", "Change To Map Fragment");
        myAdapter.setMapFragment(fragment);
        viewPager.setCurrentItem(0, true);
    }

    public MapFragmentWatershed getMapFragment() {
        if (mapFragment == null) {
            Log.d("Watershed app", "Map Fragment is null");
        }
        return mapFragment;
    }
}


