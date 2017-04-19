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
import model.WaterData;
import java.util.Date;

/**
 * Created by haidangdam on 4/9/17.
 */

public class AddReportWorkerActivity extends AppCompatActivity {
  private Spinner locationSpinner;
  private Spinner waterConditionSpinner;
  private EditText virusPPMEditText;
  private EditText contaminantPPMEditText;
  private ProgressDialog pDialog;
  private ArrayList<String> arrSpinner;
  private ArrayList<String> waterConditionArr;
  private DatabaseReference databaseReference;
  private Button submitButton;
  private Button cancelButton;
  WaterData data;
  int add = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pDialog = new ProgressDialog(this);
    setContentView(R.layout.add_activity_layout_worker);
    if (getIntent().getExtras() != null) {
      arrSpinner = getIntent().getExtras().getStringArrayList(MainActivity.ARRAY_LIST_KEY);
      Log.d("Watershed app", "Enter spinner");
    }
    locationSpinner = (Spinner) findViewById(R.id.location_spinner_report);
    waterConditionSpinner = (Spinner) findViewById(R.id.water_condition_spinner_worker_report);
    virusPPMEditText = (EditText) findViewById(R.id.virus_ppm_worker_report);
    submitButton = (Button) findViewById(R.id.submit_report_button);
    cancelButton = (Button) findViewById(R.id.cancel_report_button);
    contaminantPPMEditText = (EditText) findViewById(R.id.contaminant_ppm_worker_report);
    ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
        arrSpinner);
    locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    locationSpinner.setAdapter(locationAdapter);
    waterConditionArr = new ArrayList<String>();
    waterConditionArr.add("Safe");
    waterConditionArr.add("Treatable");
    waterConditionArr.add("Unsafe");
    ArrayAdapter<String> waterConditionAdapter = new ArrayAdapter<>(this, android.R.layout.
        simple_spinner_dropdown_item, waterConditionArr);
    waterConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    waterConditionSpinner.setAdapter(waterConditionAdapter);
    databaseReference = FirebaseDatabase.getInstance().getReference().child("waterResources");
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (locationSpinner.getSelectedItem().toString().isEmpty() | waterConditionSpinner.
            getSelectedItem().toString().isEmpty() | virusPPMEditText.getText().toString().isEmpty()
            | contaminantPPMEditText.getText().toString().isEmpty()) {
          processEmptyField();
        } else {
          pDialog.setMessage("Loading");
          pDialog.show();
          setUpDatabase();
        }
      }
    });
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goBackToMainActivity();
      }
    });
  }

  /**
   *
   */
  private void processEmptyField() {
    Toast.makeText(this, "Cannot leave empty field", Toast.LENGTH_LONG).show();
  }

  /**
   *
   */
  private void setUpDatabase() {
    databaseReference.child(locationSpinner.getSelectedItem().toString()).addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot ds) {
            Log.d("Watershed app", "Set up database");
            if (add == 0) {
              data = ds.getValue(WaterData.class);
              add++;
              submitData();
            }
          }
          @Override
          public void onCancelled(DatabaseError de) {
            Log.d("Watershed app", "Error connecting firebase " + de.getMessage());
          }
        });
  }

  /**
   *
   */
  private void submitData() {
    ArrayList<Date> dateArray = data.getdatelist();
    ArrayList<Double> virusPPMArr = data.getvirusPPM();
    ArrayList<Double> contaminantPPMArr = data.getcontaminantPPM();
    ArrayList<String> reporterId = data.getreporterId();
    if (dateArray == null) {
      dateArray = new ArrayList<Date>();
    }
    dateArray.add(new Date());
    if (virusPPMArr == null) {
      virusPPMArr = new ArrayList<Double>();
    }
    if (contaminantPPMArr == null) {
      contaminantPPMArr = new ArrayList<Double>();
    }
    if (reporterId == null) {
      reporterId = new ArrayList<String>();
    }
    virusPPMArr.add(Double.parseDouble(virusPPMEditText.getText().toString()));
    contaminantPPMArr.add(Double.parseDouble(contaminantPPMEditText.getText().toString()));
    reporterId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
    databaseReference.child(locationSpinner.getSelectedItem().toString()).child("reporterId")
        .setValue(reporterId);
    databaseReference.child(locationSpinner.getSelectedItem().toString()).child("contaminantPPM")
        .setValue(contaminantPPMArr);
    databaseReference.child(locationSpinner.getSelectedItem().toString()).child("virusPPM")
        .setValue(virusPPMArr);
    databaseReference.child(locationSpinner.getSelectedItem().toString()).child("datelist")
        .setValue(dateArray);
    goBackToMainActivity();

  }


  /**
   *
   */
  private void goBackToMainActivity() {
    pDialog.dismiss();
    Log.d("Watershed app", "exit");
    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    finish();
  }
}
