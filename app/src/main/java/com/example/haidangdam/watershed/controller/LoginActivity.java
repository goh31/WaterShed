package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidangdam.watershed.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    Button LogInButton;
    EditText userNameLogin;
    EditText passwordLogin;
    Button registrationButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private LoginButton logInFacebook;
    private CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        LogInButton = (Button) findViewById(R.id.email_sign_in_button);
        userNameLogin = (EditText) findViewById(R.id.email);
        passwordLogin = (EditText) findViewById(R.id.password);
        registrationButton = (Button) findViewById(R.id.registration_sign_in_button);
        logInFacebook = (LoginButton) findViewById(R.id.login_button);
        logInFacebook.setReadPermissions("email", "public_profile");
        progressDialog = new ProgressDialog(this);
        callbackManager = CallbackManager.Factory.create();
        if (getIntent().getExtras() != null) {
            putDataIntoField(getIntent().getExtras());
        }
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistrationActivity();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("MAIN ACTIVITY", "USER SIGN IN");
                } else {
                    Log.d("MAIN ACTIVITY", "USER SIGN OUT");
                }
            }
        };
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameLogin.getText().toString().isEmpty() | passwordLogin.getText().toString().isEmpty()) {
                    processEmptyTextField();
                } else {
                    authenticateUser();
                }
            }

        });
        logInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Android", "Facebook login: "+ loginResult);
                handleFacebookLoginToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Facebook", "User cancel");
            }
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Log in failed: " + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Action when the either user input is empty
     */
    private void processEmptyTextField() {
        Context context = getApplicationContext();
        CharSequence string = "You need to type password or user name login";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }


    /**
     *  Go to registration screen when hitting registration button
     */
    private void goToRegistrationActivity() {
        Intent registrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(registrationActivity);
    }


    /**
     * Put all the data passed from the registration to the appropriate field
     * @param data The data that pass from the registration
     */
    private void putDataIntoField(Bundle data) {
        Log.d("Device in put Data", "here");
        userNameLogin.setText(data.getString(RegistrationActivity.username));
        passwordLogin.setText(data.getString(RegistrationActivity.password));
    }


    /**
     * Authenticate the password and email from Firebase Database
     */
    private void authenticateUser() {
        progressDialog.setMessage("Please wait!!");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(userNameLogin.getText().toString(),
                passwordLogin.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> result) {
                        progressDialog.dismiss();
                        if (!result.isSuccessful()) {
                           Toast.makeText(getApplicationContext(), result.getException().toString(), Toast.LENGTH_LONG).show();
                       } else {
                            processCorrectPasswordAndUsername();
                       }
                    }
        });
    }


    /**
     * If it is the right password, move to the main activity
     */
    private void processCorrectPasswordAndUsername() {
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }


    /**
     * Sign in with facebook using Firebase
     * @param token The token confirmation from Facebook
     */
    private void handleFacebookLoginToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            processCorrectPasswordAndUsername();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed: " + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * After running authorization with Facebook, coming back to the main screen, this method will be invoked
     * Call Back Manager will get the data from log in activity an run the registration.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

