package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.haidangdam.watershed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by haidangdam on 2/11/17.
 */

public class NextActivity extends Activity {
    Button logOffButton;
    Button updateButton;
    Button nextToActivity;
    EditText nameTextField;
    EditText homeAddressField;
    EditText phoneNumberField;
    EditText emailAddressField;
    EditText passwordField;
    public static final String TAG = "MainView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity_layout);
        nameTextField = (EditText) findViewById(R.id.nameTextField);
        homeAddressField = (EditText) findViewById(R.id.homeAddressField);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumberField);
        emailAddressField = (EditText) findViewById(R.id.emailAddressField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        logOffButton = (Button) findViewById(R.id.log_off_button);
        logOffButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goBackToLogin();
            }
        });

        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextField.getText().toString();
                String homeAddress = homeAddressField.getText().toString();
                String phoneNumber = phoneNumberField.getText().toString();
                String emailAddress = emailAddressField.getText().toString();
                String password = passwordField.getText().toString();
            }
        });


        AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
