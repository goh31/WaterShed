package com.example.haidangdam.watershed.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.fragment_list.MapFragment;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by haidangdam on 2/18/17.
 */

public class MainActivity extends AppCompatActivity {
    static final int NUM_COUNTS = 3;
    ViewPager viewPager;
    TabLayout tabLayout;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_worker);

        viewPager = (ViewPager) findViewById(R.id.main_activity_worker_view_pager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), MainActivity.this));
        tabLayout = (TabLayout) findViewById(R.id.main_activity_worker_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Location"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));

    }

    public static class MyAdapter extends FragmentPagerAdapter {

        private String[] field = new String[] {"Map", "Location", "Profile"};
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
                    return MapFragment.newInstance();
                case 1:
                    return MapFragment.newInstance();
                case 2:
                    return MapFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return field[position];
        }

    }
}


