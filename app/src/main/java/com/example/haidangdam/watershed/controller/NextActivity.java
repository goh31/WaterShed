package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.haidangdam.watershed.R;

/**
 * Created by haidangdam on 2/11/17.
 */

public class NextActivity extends Activity {
    Button logOffButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity_layout);
        logOffButton = (Button) findViewById(R.id.log_off_button);
        logOffButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goBackToLogin();
            }
        });
    }

    /**
     * Create an intent pointing to the Login Activity and startActivity used to go back
     */
    public void goBackToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
