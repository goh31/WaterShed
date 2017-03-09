package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.haidangdam.watershed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.User;


/**
 * Created by haidangdam on 2/11/17.
 */

public class NextActivity extends Activity {

  public static final String TAG = "MainView";
  Button logOffButton;
  Button updateButton;
  Button nextToActivity;
  EditText nameTextField;
  EditText homeAddressField;
  EditText phoneNumberField;
  FirebaseUser firebaseUser;
  User user;
  String uId;
  String credential;
  ProgressDialog progressDialog;
  private DatabaseReference WaterDatabaseReference;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.next_activity_layout);
    nameTextField = (EditText) findViewById(R.id.nameTextField);
    homeAddressField = (EditText) findViewById(R.id.homeAddressField);
    phoneNumberField = (EditText) findViewById(R.id.phoneNumberField);
    logOffButton = (Button) findViewById(R.id.log_off_button);
    logOffButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goBackToLogin();
      }
    });
    WaterDatabaseReference = FirebaseDatabase.getInstance().getReference().
        child(RegistrationActivity.PATH_USER);
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    uId = firebaseUser.getUid();
    populateFieldView(uId);
    updateButton = (Button) findViewById(R.id.updateButton);
    progressDialog = new ProgressDialog(this);
    updateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressDialog.show();
        updateDatabase();
      }
    });
    nextToActivity = (Button) findViewById(R.id.button_info_to_main_activity);
    nextToActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToMainActivity();
      }
    });


  }

  /**
   * Create an intent pointing to the Login Activity and startActivity used to go back
   */
  private void goBackToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }

  /**
   *
   */
  private void goToMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  private void populateFieldView(String uId) {
    WaterDatabaseReference.child(uId).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        user = (User) snapshot.getValue(User.class);
        nameTextField.setText(user.getName());
        homeAddressField.setText(user.getHomeAddress());
        phoneNumberField.setText(user.getPhoneNumber());
        credential = user.getCredential();
      }

      @Override
      public void onCancelled(DatabaseError errr) {
        Log.d("Watershed", "error: " + errr.getMessage());
      }
    });
  }

  private void updateDatabase() {
    user.setName(nameTextField.getText().toString());
    user.setPhoneNumber(phoneNumberField.getText().toString());
    user.setHomeAddress(homeAddressField.getText().toString());
    WaterDatabaseReference.child(uId).setValue(user);
    progressDialog.dismiss();
  }
}

