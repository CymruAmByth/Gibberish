package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.yacht.bootcamp.gibberish.R;

public class Login_Activity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();
    }


    public void loginButtonClicked(View view){
        String name = ((EditText)findViewById(R.id.etLogin)).getText().toString();
        if(name.length()>0){
            SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", name);
            editor.commit();
            finish();
        }
    }
}
