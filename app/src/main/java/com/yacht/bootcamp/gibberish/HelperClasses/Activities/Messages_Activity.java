package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Adapter.ConversationAdapter;
import com.yacht.bootcamp.gibberish.HelperClasses.Adapter.MessageAdapter;
import com.yacht.bootcamp.gibberish.HelperClasses.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessageFetchTask;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessagePushTask;
import com.yacht.bootcamp.gibberish.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Messages_Activity extends ActionBarActivity {

    private String local, remote;
    private int updateInterval;
    private MessageAdapter adapter;
    private ArrayList<Message> values;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        updateInterval = prefs.getInt("activeUpdateInterval", 5000);
        local = prefs.getString("userName", "Cymru");

        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            remote = extras.getString("remote");
        }

        //List
        ListView lv = (ListView) findViewById(R.id.messageListView);
        values = updateMessages();
        adapter = new MessageAdapter(this, values);
        lv.setAdapter(adapter);

        //timer
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(updateMessages());
                adapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, updateInterval);
            }
        };
        timerHandler.postDelayed(timerRunnable, updateInterval);


    }


    private ArrayList<Message> updateMessages(){
        ArrayList<Message> result = null;
        RemoteMessageFetchTask rmft = new RemoteMessageFetchTask(this);
        rmft.execute(local);
        MessageDataSource mds = new MessageDataSource(this);
        try {
            mds.open();
            result = mds.getAllMessagesBetweenLocalAndRemote(local, remote);
            mds.close();
        } catch (SQLException e) {
            Log.d("Gib", e.getMessage());
            mds.close();
        }
        return result;
    }

    public void btnSendClicked(View view){
        EditText etMessage = (EditText)findViewById(R.id.etMessage);
        String message = etMessage.getText().toString();
        if(!message.equals("")){
            etMessage.setText("");
            Message m = new Message(0, false, local, remote, message, System.currentTimeMillis(),true);
            MessageDataSource mds = new MessageDataSource(this);
            RemoteMessagePushTask rmpt = new RemoteMessagePushTask(this);
            rmpt.execute(m);
            values.add(0, m);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
