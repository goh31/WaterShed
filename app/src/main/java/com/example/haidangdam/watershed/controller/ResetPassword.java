package com.example.haidangdam.watershed.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.haidangdam.watershed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by haidangdam on 3/21/17.
 */

public class ResetPassword extends AppCompatActivity {

  private EditText emailEditText;
  private Button resetPasswordButton;
  private FirebaseAuth auth;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reset_password);
    auth = FirebaseAuth.getInstance();
    emailEditText = (EditText) findViewById(R.id.edit_text_email_reset_password);
    resetPasswordButton = (Button) findViewById(R.id.reset_password_button);
    resetPasswordButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (emailEditText.getText().toString().isEmpty()) {
          emptyEmailActionProceed();
        } else {
          nonEmptyEmailActionProceed();
        }
      }
    });
  }

  /**
   * Inform the user that the email text is empty
   */
  private void emptyEmailActionProceed() {
    Toast.makeText(this, "Cannot have empty email", Toast.LENGTH_LONG).show();
  }

  /**
   * If the email text is not empty, try to send confirmation email. If failed, get a toast to
   * inform the user
   */
  private void nonEmptyEmailActionProceed() {
    auth.sendPasswordResetEmail(emailEditText.getText().toString())
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Toast.makeText(getApplicationContext(), "Email has been sent", Toast.LENGTH_LONG)
                  .show();
              goBackToLoginActivity();
            } else {
              Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                  Toast.LENGTH_LONG).show();
            }
          }
        });
  }

  /**
   * Go back to log in activity after finish
   */
  private void goBackToLoginActivity() {
    startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    finish();
  }
}
