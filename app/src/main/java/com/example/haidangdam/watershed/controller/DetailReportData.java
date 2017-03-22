package com.example.haidangdam.watershed.controller;

import android.app.Activity;
import android.os.Bundle;
import com.example.haidangdam.watershed.R;
import com.example.haidangdam.watershed.controller.fragment_list.ListViewFragmentAdmin;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.WaterData;

/**
 * Created by haidangdam on 3/18/17.
 */

public class DetailReportData extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detail_report_layout);
    WaterData data = (WaterData) getIntent().getExtras()
        .getSerializable(ListViewFragmentAdmin.WATERDATAOBJECT);
    LineChart chart = (LineChart) findViewById(R.id.chart);
    List<Entry> entries = new ArrayList<Entry>();
    Date currentDate = new Date();
    for (int i = 0; i < data.getcriticalLevel().size(); i++) {
      entries.add(new Entry((int) (data.getcriticalLevel().get(i) * 100),
          ((int) data.getdatelist().get(i).getTime() - (int) currentDate.getTime()) /
              (1000 * 3600 * 24)));
    }
    LineDataSet dataSet = new LineDataSet(entries, "Critical Level");
    dataSet.setColor(R.color.colorPrimary);
    dataSet.setValueTextColor(R.color.red);
    LineData lineData = new LineData(dataSet);
    chart.setData(lineData);
    chart.invalidate();
  }

  @Override
  public void onPause() {
    super.onPause();
    finish();
  }

}
