package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import android.support.annotation.Nullable;
import android.widget.EditText;

import com.example.haidangdam.watershed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by haidangdam on 2/11/17.
 */

public class NextActivity extends Activity {
    Button logOffButton;
    Button updateButton;
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

    }

    /**
     * Create an intent pointing to the Login Activity and startActivity used to go back
     */
    public void goBackToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
