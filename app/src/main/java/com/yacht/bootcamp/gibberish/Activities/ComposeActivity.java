package com.yacht.bootcamp.gibberish.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.yacht.bootcamp.gibberish.HelperClasses.Model.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessagePushTask;
import com.yacht.bootcamp.gibberish.R;

public class ComposeActivity extends ActionBarActivity {

    String local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        local = prefs.getString("userName", "Cymru");
    }

    public void sendButtonClicked(View view){
        String contact = ((EditText)findViewById(R.id.etComposeContact)).getText().toString();
        String message = ((EditText)findViewById(R.id.etComposeMessage)).getText().toString();
        Message m = new Message(0, false, local, contact, message, System.currentTimeMillis(), true);
        RemoteMessagePushTask rmpt = new RemoteMessagePushTask(this);
        rmpt.execute(m);
        finish();
    }
}
