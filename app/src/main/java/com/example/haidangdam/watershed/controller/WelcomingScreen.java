package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.haidangdam.watershed.R;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import model.User;

/**
 * Created by haidangdam on 4/18/17.
 */

public class WelcomingScreen extends AppCompatActivity {
  FirebaseAuth firebase;
  ProgressDialog dialog;
  private static final int FACEBOOK_REQUEST_CODE = 1709;
  DatabaseReference refUser;
  public static final String USER_CREDENTIAL = "Credential";
  User user;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FacebookSdk.sdkInitialize(getApplicationContext(), FACEBOOK_REQUEST_CODE);
    setContentView(R.layout.welcoming_screen);
    dialog = new ProgressDialog(this);
    firebase = FirebaseAuth.getInstance();
    dialog.setMessage("Loading");
    dialog.show();
    if (firebase.getCurrentUser() != null) {
      Intent intent = new Intent(this, MainActivity.class);
      dialog.dismiss();
      startActivity(intent);
    } else {
      Intent intent = new Intent(this, LoginActivity.class);
      dialog.dismiss();
      startActivity(intent);
    }
  }

}
