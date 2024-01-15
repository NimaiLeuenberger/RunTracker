package com.runtracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.runtracker.R;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class RunningOverviewActivity extends AppCompatActivity {

    TableLayout tableLayout;
    String date;
    String steps;
    String kcal;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_overview);
        date = getIntent().getStringExtra("date");
        steps = getIntent().getStringExtra("steps");
        kcal = getIntent().getStringExtra("kcal");
        time = getIntent().getStringExtra("time");
        tableLayout = (TableLayout) findViewById(R.id.table_layout);

        loadData();

        if (date != null && steps != null && kcal != null && time != null) {
            saveData(date, steps, kcal, time);
            tableLayout.addView(createTableRow(date, steps, kcal, time));
        }
    }

    private TableRow createTableRow(String date, String steps, String kcal, String time) {
        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_layout, null); //new TableRow(this);
        tableRow.setPadding(3,0,3,1);
        TextView tv1 = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout, null);
        TextView tv2 = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout_2, null);
        TextView tv3 = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout_3, null);
        TextView tv4 = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout_4, null);
        tv1.setText(date);
        tv2.setText(steps);
        tv3.setText(kcal);
        tv4.setText(time);
        tableRow.addView(tv1);
        tableRow.addView(tv2);
        tableRow.addView(tv3);
        tableRow.addView(tv4);
        return tableRow;
    }

    private void saveData(String date, String steps, String kcal, String time) {
        SharedPreferences sharedPreferences = getSharedPreferences("RunTrackerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String key = String.valueOf(System.currentTimeMillis());
        editor.putString(key + "_date", date);
        editor.putString(key + "_steps", steps);
        editor.putString(key + "_kcal", kcal);
        editor.putString(key + "_time", time);

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("RunTrackerData", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().endsWith("_date")) {
                String keyBase = entry.getKey().replace("_date", "");
                String date = (String) entry.getValue();
                String steps = sharedPreferences.getString(keyBase + "_steps", "");
                String kcal = sharedPreferences.getString(keyBase + "_kcal", "");
                String time = sharedPreferences.getString(keyBase + "_time", "");

                TableRow tableRow = createTableRow(date, steps, kcal, time);
                tableLayout.addView(tableRow);
            }
        }
    }

}