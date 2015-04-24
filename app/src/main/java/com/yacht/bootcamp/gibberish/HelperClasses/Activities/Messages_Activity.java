package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private MessageDataSource mds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mds = new MessageDataSource(this);
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        updateInterval = prefs.getInt("activeUpdateInterval", 5000);
        local = prefs.getString("userName", "Cymru");

        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            remote = extras.getString("remote");
        }

        this.getSupportActionBar().setTitle("Gibberish with " +remote);


        //List
        ListView lv = (ListView) findViewById(R.id.messageListView);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                messageClicked(values.get(position));
                return true;
            }
        });

        values = updateMessages();
        adapter = new MessageAdapter(this, values);
        lv.setAdapter(adapter);

        //timer
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                values = updateMessages();
                adapter.addAll(values);
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
        try {
            mds.open();
            result = mds.getAllMessagesBetweenLocalAndRemote(local, remote);
            mds.close();
        } catch (SQLException e) {
            mds.close();
            Log.d("Gib", e.getMessage());
        }
        return result;
    }

    private void messageClicked(final Message m){
        TextView tvAlert = new TextView(this);
        tvAlert.setMovementMethod(LinkMovementMethod.getInstance());
        tvAlert.setPadding(10,10,10,10);
        tvAlert.setText("Are you sure you want to delete this message?");
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setView(tvAlert)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mds.open();
                            mds.deleteMessage(m);
                            mds.close();
                            values.remove(m);
                            adapter.notifyDataSetChanged();
                        } catch (SQLException e) {
                            mds.close();
                            Log.d("Gib", e.getMessage());
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnSendClicked(View view){
        EditText etMessage = (EditText)findViewById(R.id.etMessage);
        String message = etMessage.getText().toString();
        if(!message.equals("")){
            etMessage.setText("");
            Message m = new Message(0, false, local, remote, message, System.currentTimeMillis(),true);
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

}
