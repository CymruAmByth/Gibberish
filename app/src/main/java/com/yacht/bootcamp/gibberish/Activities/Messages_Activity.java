package com.yacht.bootcamp.gibberish.Activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Adapter.MessageAdapter;
import com.yacht.bootcamp.gibberish.HelperClasses.Model.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessageFetchTask;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessagePushTask;
import com.yacht.bootcamp.gibberish.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

public class Messages_Activity extends ActionBarActivity {

    private String local, remote;
    private int updateInterval, unreadMessages;
    private MessageAdapter adapter;
    private List<Message> messages;
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
            unreadMessages = extras.getInt("unreadMessages");
        }

        this.getSupportActionBar().setTitle("Gibberish with " +remote);


        //List
        ListView lv = (ListView) findViewById(R.id.lvMessages);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                messageClicked(messages.get(position));
                return true;
            }
        });
        try {
            mds.open();
            messages = mds.getAllMessagesBetweenLocalAndRemote(local, remote);
        } catch (SQLException e) {
            Log.d("Gib", e.getMessage());
        }
        mds.close();
        adapter = new MessageAdapter(this, messages);
        lv.setAdapter(adapter);

        //timer
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.addMessages(updateMessages());
                timerHandler.postDelayed(this, updateInterval);
            }
        };
        timerHandler.postDelayed(timerRunnable, updateInterval);
    }


    private List<Message> updateMessages(){
        List<Message> result = null;
        RemoteMessageFetchTask rmft = new RemoteMessageFetchTask(this);
        rmft.execute(local);
        int newMessages = 0;
        try {
            mds.open();
            result = mds.newMessagesInConversation(local, remote);
            newMessages = mds.unreadMessages(local);
            mds.close();
        } catch (SQLException e) {
            mds.close();
            Log.d("Gib", e.getMessage());
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(newMessages == 0)
            nm.cancel(0);
        else if(newMessages>unreadMessages){
            unreadMessages = newMessages;
            Intent intent = new Intent(this, Conversations_Activity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification not = new Notification.Builder(this)
                    .setContentTitle("Gibberish")
                    .setContentText("You have " + newMessages + " unread messages")
                    .setSmallIcon(R.mipmap.ic_gibberish_transparant)
                    .setContentIntent(pIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
            nm.notify(0, not);
        } else {
            unreadMessages = newMessages;
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
                            messages.remove(m);
                            adapter.deleteMessage(m);
                        } catch (SQLException e) {
                            Log.d("Gib", e.getMessage());
                        }
                        mds.close();
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnSendClicked(View view){
        EditText etMessage = (EditText)findViewById(R.id.etMessage);
        String message = etMessage.getText().toString().trim();
        if(message.length()>0){
            etMessage.setText("");
            Message m = new Message(0, false, local, remote, message, System.currentTimeMillis(),true);
            RemoteMessagePushTask rmpt = new RemoteMessagePushTask(this);
            rmpt.execute(m);
            adapter.addMessage(m);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

}
