package com.example.haidangdam.watershed.controller.fragment_list;

/**
 * Created by haidangdam on 3/21/17.
 */

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.EditProfile;
import com.example.haidangdam.watershed.controller.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.User;

public class ProfileFragment extends Fragment {

  public static final String USER_DATA = "user";
  private TextView profileName;
  private TextView profileEmail;
  private TextView profileCredential;
  private Button profileUpdateButton;
  private FirebaseUser user;
  private DatabaseReference userDatabaseref;
  private User userData;
  private Button signOut;

  public static ProfileFragment newInstance() {
    ProfileFragment a = new ProfileFragment();
    return a;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.profile_fragment_layout, container, false);
    profileName = (TextView) rootView.findViewById(R.id.profile_name_text);
    profileEmail = (TextView) rootView.findViewById(R.id.profile_email_text);
    profileCredential = (TextView) rootView.findViewById(R.id.profile_credential_text);
    profileUpdateButton = (Button) rootView.findViewById(R.id.profile_edit_button);
    user = FirebaseAuth.getInstance().getCurrentUser();
    userDatabaseref = FirebaseDatabase.getInstance().getReference().child("userID")
        .child(user.getUid());
    userDatabaseref.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot ds) {
        Log.d("Watershed app", "Getting user from database in profile");
        userData = ds.getValue(User.class);
        changeTextView();
      }

      @Override
      public void onCancelled(DatabaseError error) {
        Log.d("WaterShed app", "Value event listener error: " + error.getMessage());
      }
    });
    profileUpdateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_DATA, userData);
        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
        intent.putExtras(bundle);
        startActivity(intent);
      }
    });
    signOut = (Button) rootView.findViewById(R.id.sign_out);
    signOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        FirebaseAuth.getInstance().signOut();
      }
    });
    return rootView;
  }

  /**
   * Change the text view of the profile with the user information
   */
  public void changeTextView() {
    userData.setName(user.getDisplayName());
    userData.setEmail(user.getEmail());
    profileName.setText(userData.getName());
    profileEmail.setText(userData.getEmail());
    profileCredential.setText(userData.getCredential());
  }
}
