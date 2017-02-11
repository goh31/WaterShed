package com.example.haidangdam.watershed.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.haidangdam.watershed.R;

public class RegistrationActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        emailEditText = (EditText) findViewById(R.id.email_edit_text_registration);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text_registration);

    }
}
