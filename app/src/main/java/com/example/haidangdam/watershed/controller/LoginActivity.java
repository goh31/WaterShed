package com.example.haidangdam.watershed.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidangdam.watershed.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    Button LogInButton;
    EditText userNameLogin;
    EditText passwordLogin;
    Button registrationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LogInButton = (Button) findViewById(R.id.email_sign_in_button);
        userNameLogin = (EditText) findViewById(R.id.email);
        passwordLogin = (EditText) findViewById(R.id.password);
        registrationButton = (Button) findViewById(R.id.registration_sign_in_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistrationActivity();
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

    private void goToRegistrationActivity() {
        Intent registrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(registrationActivity);
    }

    /**
     * Action when typing the correct password and username
     */
    private void processCorrectPasswordAndUserName() {
        Intent nextPageIntent = new Intent(this, NextActivity.class);
        startActivity(nextPageIntent);
    }

    /**
     * Action when typing the wrong password or username
     */
    private void processWrongPasswordOrUserName(){
        Context context = getApplicationContext();
        CharSequence string = "Wrong password";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }
}

