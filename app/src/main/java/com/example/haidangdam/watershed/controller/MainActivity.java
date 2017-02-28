package com.example.haidangdam.watershed.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.fragment_list.ListViewFragmentAdmin;
import com.example.haidangdam.watershed.controller.fragment_list.MapFragmentWatershed;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by haidangdam on 2/18/17.
 */

public class MainActivity extends FragmentActivity {
    static final int NUM_COUNTS = 3;
    ViewPager viewPager;
    TabLayout tabLayout;
    private GoogleApiClient mGoogleApiClient;
    MapFragmentWatershed mapFragment;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_worker);

        viewPager = (ViewPager) findViewById(R.id.main_activity_worker_view_pager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), MainActivity.this));
        myAdapter = (MyAdapter) viewPager.getAdapter();
        mapFragment = myAdapter.getMapFragment();
        tabLayout = (TabLayout) findViewById(R.id.main_activity_worker_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Location"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setupWithViewPager(viewPager);

    }

    public static class MyAdapter extends FragmentPagerAdapter {
        MapFragmentWatershed mapFragmentMyAdapter;
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
            switch (position) {
                case 0:
                    return MapFragmentWatershed.newInstance();
                case 1:
                    return ListViewFragmentAdmin.newInstance();
                case 2:
                    return MapFragmentWatershed.newInstance();
                default:
                    return null;
            }
        }

        public MapFragmentWatershed getMapFragment() {
            return mapFragmentMyAdapter;
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


}


