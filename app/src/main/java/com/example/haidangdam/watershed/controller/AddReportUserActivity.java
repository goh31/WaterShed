package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.haidangdam.watershed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import model.WaterData;

/**
 * Created by haidangdam on 3/3/17.
 */

public class AddReportUserActivity extends AppCompatActivity {

  private Spinner locationAvailable;
  private DatabaseReference databaseReference;
  private Button submitButton;
  private Button cancelButton;
  private ArrayList<String> arrSpinner;
  int i = 0;
  private Spinner waterTypeSpinner;
  private Spinner waterConditionSpinner;
  private ProgressDialog progressDialog;
  private ArrayList<String> waterType;
  WaterData waterData;
  int time  = 0;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d("Watershed app", "Add report activity");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_activity_user_layout);
    if (getIntent().getExtras() != null) {
      Log.d("Watershed app", "get intent and get extras is not null");
      arrSpinner = getIntent().getExtras().getStringArrayList(MainActivity.ARRAY_LIST_KEY);
    }
    //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    locationAvailable = (Spinner) findViewById(R.id.location_spinner_report);
    setArrayAdapter();
    progressDialog = new ProgressDialog(this);
    submitButton = (Button) findViewById(R.id.submit_report_button);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressDialog.setMessage("Loading");
        progressDialog.show();
        submitWaterReport();
      }
    });
    cancelButton = (Button) findViewById(R.id.cancel_report_button);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goBackToMainActivity();
      }
    });
    waterType = new ArrayList<>();
    waterType.add("Bottled");
    waterType.add("Well");
    waterType.add("Stream");
    waterType.add("Lake");
    waterType.add("Spring");
    waterType.add("Other");
    waterTypeSpinner = (Spinner) findViewById(R.id.type_of_water_spinner);
    ArrayAdapter<String> waterTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, waterType);
    waterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    waterTypeSpinner.setAdapter(waterTypeAdapter);
    ArrayList<String> waterCondition = new ArrayList<String>();
    waterCondition.add("Waste");
    waterCondition.add("Treatable-Clear");
    waterCondition.add("Treatable-Muddy");
    waterCondition.add("Potable");
    ArrayAdapter<String> waterConditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, waterCondition);
    waterConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    waterConditionSpinner = (Spinner) findViewById(R.id.condition_of_water_spinner);
    waterConditionSpinner.setAdapter(waterConditionAdapter);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        submitWaterReport();
      }
    });
    databaseReference = FirebaseDatabase.getInstance().getReference().child("waterResources");
  }

  /**
   * Submit water report to firebase and then automatically going back to main activity
   */
  private void submitWaterReport() {
    if (waterConditionSpinner.getSelectedItem().toString().isEmpty() | waterTypeSpinner
        .getSelectedItem().toString().isEmpty()) {
      Toast.makeText(this, "Cannot have unselected spinner", Toast.LENGTH_LONG).show();
    } else {
      databaseReference.child(locationAvailable.getSelectedItem().toString()).
          addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (time == 0) {
                waterData = dataSnapshot.getValue(WaterData.class);
                Log.d("Watershed app", "Connect to firebase and get the array of critical value");
                time++;
                addWaterReportToFirebase();
              }
            }

            @Override
            public void onCancelled(DatabaseError err) {
              Log.d("Watershed app", "Error connecting firebase " + err.getMessage());
            }
          });

    }
  }

  /**
   * Populate spinner with location nearby
   */
  private void setArrayAdapter() {
    ArrayAdapter<String> arr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
        arrSpinner);
    arr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    locationAvailable.setAdapter(arr);
  }

  /**
   * Updating the data to firebase
   */
  private void addWaterReportToFirebase() {
    Log.d("Watershed", "Add Critical value");
    ArrayList<Date> dateArray = waterData.getdatelist();
    ArrayList<String> reporterId = waterData.getreporterId();
    reporterId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
    databaseReference.child(locationAvailable.getSelectedItem().toString()).child("reporterId")
        .setValue(reporterId);
    databaseReference.child(locationAvailable.getSelectedItem().toString()).child("datelist")
        .setValue(dateArray);
    databaseReference.child(locationAvailable.getSelectedItem().toString()).child("waterType").
        setValue(waterTypeSpinner.getSelectedItem().toString());
    databaseReference.child(locationAvailable.getSelectedItem().toString()).child("waterCondition").
        setValue(waterConditionSpinner.getSelectedItem().toString());
    goBackToMainActivity();
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
