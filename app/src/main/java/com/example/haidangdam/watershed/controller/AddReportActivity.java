package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import com.example.haidangdam.watershed.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haidangdam on 3/3/17.
 */

public class AddReportActivity extends AppCompatActivity {
    EditText dayReport;
    SeekBar criticalLevelReport;
    Spinner locationAvailable;
    DatabaseReference databaseReference;
    Button submitButton;
    Button cancelButton;
    ArrayList<String> arrSpinner;
    int seekBarValue;
    int i = 0;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Watershed app", "Add report activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_layout);
        if (getIntent().getExtras() != null) {
            Log.d("Watershed app", "get intent and get extras is not null");
            arrSpinner = getIntent().getExtras().getStringArrayList("stringArray");
        }
        locationAvailable = (Spinner) findViewById(R.id.location_spinner_report);
        setArrayAdapter();
        progressDialog = new ProgressDialog(this);
        criticalLevelReport = (SeekBar) findViewById(R.id.critical_level_seek_bar);
        submitButton = (Button) findViewById(R.id.submit_report_button);
        cancelButton = (Button) findViewById(R.id.cancel_report_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMainActivity();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaterReport();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("waterResources");
        criticalLevelReport.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarValue = seekBar.getProgress();
            }

        });

    }

    /**
     * Submit water report to firebase and then automatically going back to main activity
     */
    private void submitWaterReport() {
        progressDialog.setMessage("Updating database");
        progressDialog.show();
        addWaterReportToFirebase();
        goBackToMainActivity();
    }

    /**
     * Populate spinner with location nearby
     */
    private void setArrayAdapter() {
        ArrayAdapter<String> arr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrSpinner);
        arr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationAvailable.setAdapter(arr);
    }

    /**
     * Updating the data to firebase
     */
    private void addWaterReportToFirebase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("criticalLevel", (double) seekBarValue / 100.0);
        databaseReference.child(locationAvailable.getSelectedItem().toString()).updateChildren(hashMap);
    }

    /**
     * Going back to main activity and delete the instance
     */
    private void goBackToMainActivity() {
        progressDialog.dismiss();
        startActivity(new Intent(this, MainActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

}
