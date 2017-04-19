package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.haidangdam.watershed.R;
import java.util.ArrayList;

/**
 * Created by haidangdam on 4/9/17.
 */

public class AddReportWorkerControl extends Activity {
  private Button userReport;
  private Button workerReport;
  private Bundle bundle;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_report_worker_control);
    ArrayList<String> content = getIntent().getStringArrayListExtra(MainActivity.ARRAY_LIST_KEY);
    userReport = (Button) findViewById(R.id.user_report_button);
    workerReport = (Button) findViewById(R.id.worker_report_button);
    bundle = new Bundle();
    bundle.putStringArrayList(MainActivity.ARRAY_LIST_KEY, content);
    userReport.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToUserReport();
      }
    });
    workerReport.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToWorkerReport();
      }
    });
  }

  /**
   *
   */
  private void goToUserReport() {
    Intent intent = new Intent(this, AddReportUserActivity.class);
    intent.putExtras(bundle);
    startActivity(intent);
  }

  /**
   *
   */
  private void goToWorkerReport() {
    Intent intent = new Intent(this, AddReportWorkerActivity.class);
    intent.putExtras(bundle);
    startActivity(intent);
  }
}
