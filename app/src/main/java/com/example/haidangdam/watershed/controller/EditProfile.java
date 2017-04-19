package com.example.haidangdam.watershed.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.fragment_list.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.User;


public class EditProfile extends AppCompatActivity {

  private TextView helloTextView;
  private EditText emailUpdateProfile;
  private EditText nameUpdateProfile;
  private Button button;
  private User user;
  private FirebaseUser userFirebase;
  private DatabaseReference refUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_profile);
    user = (User) getIntent().getExtras().getSerializable(ProfileFragment.USER_DATA);
    helloTextView = (TextView) findViewById(R.id.helloUser);
    nameUpdateProfile = (EditText) findViewById(R.id.name_edit_profile);
    emailUpdateProfile = (EditText) findViewById(R.id.email_edit_profile);
    button = (Button) findViewById(R.id.updateButton);
    if (user != null) {
      helloTextView.setText("Hello " + user.getCredential() + "!");
      nameUpdateProfile.setText(user.getName());
      emailUpdateProfile.setText(user.getEmail());
    }
    refUser = FirebaseDatabase.getInstance().getReference("userID");
    userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (nameUpdateProfile.getText().toString().isEmpty() || emailUpdateProfile.getText()
            .toString().isEmpty()) {

        } else {
          if (!emailUpdateProfile.getText().toString().equals(user.getEmail())) {
            checkEmail();
          } else {
            updateDatabase();
          }
        }
      }
    });
  }

  /**
   * Send the confirmation email for the user
   */
  private void checkEmail() {
    userFirebase.updateEmail(emailUpdateProfile.getText().toString()).addOnCompleteListener(
        new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              updateDatabase();
            } else {
              Log.d("Watershed app", task.getException().getMessage());
              Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                  Toast.LENGTH_LONG).show();
            }
          }
        }
    );
  }

  /**
   * Update the database with the user changes
   */
  private void updateDatabase() {
    user.setEmail(emailUpdateProfile.getText().toString());
    user.setName(nameUpdateProfile.getText().toString());
    refUser.child(userFirebase.getUid()).setValue(user);
    goBackToMain();
  }

  /**
   * Go back to the main activity after finish
   */
  private void goBackToMain() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }
}
