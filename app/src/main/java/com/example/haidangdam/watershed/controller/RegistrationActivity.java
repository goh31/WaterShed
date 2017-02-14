package com.example.haidangdam.watershed.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidangdam.watershed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    Button registrationButton;
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

    private boolean validateField() {
        if (emailEditText.getText().toString().isEmpty() | passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(), "Have to fill up both field", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }


    private void createAccount() {
        progressDialog.setMessage("Please wait!");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),
                passwordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Authentication failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Sign up sucessful", "Sign up successful");
                        }
                        progressDialog.dismiss();
                    }

        });
    }
}
