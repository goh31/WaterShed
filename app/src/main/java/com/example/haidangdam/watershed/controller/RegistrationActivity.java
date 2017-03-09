package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.haidangdam.watershed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import model.User;

public class RegistrationActivity extends AppCompatActivity {

  public static final String password = "PASSWORD";
  public static final String username = "USERNAME";
  public static final String PATH_USER = "userID";
  EditText emailEditText;
  EditText passwordEditText;
  Button registrationButton;
  DatabaseReference databaseUser;
  Spinner spinnerRegistration;
  EditText phoneNumberRegistration;
  EditText nameRegistration;
  EditText addressRegistration;
  private FirebaseAuth mAuth;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);
    emailEditText = (EditText) findViewById(R.id.email_edit_text_registration);
    passwordEditText = (EditText) findViewById(R.id.password_edit_text_registration);
    mAuth = FirebaseAuth.getInstance();
    registrationButton = (Button) findViewById(R.id.registration_button);
    databaseUser = FirebaseDatabase.getInstance().getReference().child(PATH_USER);
    spinnerRegistration = (Spinner) findViewById(R.id.spinnerRegistration);
    phoneNumberRegistration = (EditText) findViewById(R.id.phone_number_edit_text_registration);
    addressRegistration = (EditText) findViewById(R.id.home_address_edit_text_registration);
    nameRegistration = (EditText) findViewById(R.id.name_edit_text_registration);
    ArrayList<String> userType = new ArrayList<String>();
    userType.add("User");
    userType.add("Worker");
    userType.add("Manager");
    userType.add("Admin");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, userType);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerRegistration.setAdapter(dataAdapter);
    progressDialog = new ProgressDialog(this);
    registrationButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (validateField()) {
          createAccount();
        }
      }
    });
  }

  /**
   * @return validate the field if any is left empty
   */
  private boolean validateField() {
    if (emailEditText.getText().toString().isEmpty() | passwordEditText.getText().toString()
        .isEmpty() |
        spinnerRegistration.getSelectedItem().toString() == null) {
      Toast.makeText(getBaseContext(), "Have to fill up 3 field", Toast.LENGTH_LONG).show();
      return false;
    } else {
      return true;
    }
  }

  /**
   * Create the account from firebase when the user hit the button
   */
  private void createAccount() {
    progressDialog.setMessage("Please wait!");
    progressDialog.show();
    Log.d("1", "something");
    mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),
        passwordEditText.getText().toString())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            progressDialog.dismiss();
            if (!task.isSuccessful()) {
              Toast.makeText(getApplicationContext(),
                  "Authentication failed: " + task.getException().toString(), Toast.LENGTH_LONG)
                  .show();
            } else {
              Log.d("Sign up sucessful", "Sign up successful");
              FirebaseUser user = task.getResult().getUser();
              addToDatabase(user.getUid());
            }
          }

        });
  }

  /**
   * Go back to the log in page when finish
   */
  private void goBackToLogIn() {
    Intent goBackToLogIn = new Intent(this, LoginActivity.class);
    Bundle dataBackToLogIn = new Bundle();
    dataBackToLogIn.putString(username, emailEditText.getText().toString());
    dataBackToLogIn.putString(password, passwordEditText.getText().toString());
    goBackToLogIn.putExtras(dataBackToLogIn);
    startActivity(goBackToLogIn);
    finish();
  }

  /**
   * Add to the Firebase database the user with appropriate usertype
   */
  private void addToDatabase(String userID) {
    User newUser = new User(emailEditText.getText().toString(),
        spinnerRegistration.getSelectedItem().toString());
    newUser.setHomeAddress(addressRegistration.getText().toString());
    newUser.setPhoneNumber(phoneNumberRegistration.getText().toString());
    newUser.setName(nameRegistration.getText().toString());
    Log.d("Finish", "Finish");
    databaseUser.child(userID).setValue(newUser);
    goBackToLogIn();
  }
}
