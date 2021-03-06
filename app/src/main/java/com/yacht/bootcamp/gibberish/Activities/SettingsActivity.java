package com.yacht.bootcamp.gibberish.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.R;

public class SettingsActivity extends ActionBarActivity {
    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;
    TextView tvAUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myPrefs = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = myPrefs.edit();

        int activeUpdateInterval = myPrefs.getInt("activeUpdateInterval", 5000);

        tvAUI = (TextView)findViewById(R.id.etAutomaticUpdateInterval);
        tvAUI.setText(String.valueOf(activeUpdateInterval/1000));
    }

    @Override
    protected void onPause() {
        int newInterval = Integer.valueOf(tvAUI.getText().toString())*1000;
        if(newInterval<2000)
            newInterval = 2000;
        editor.putInt("activeUpdateInterval", newInterval);
        editor.commit();
        super.onPause();
    }
}
