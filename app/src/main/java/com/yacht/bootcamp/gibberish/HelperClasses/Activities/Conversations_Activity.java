package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.app.ActionBar;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Adapter.ConversationAdapter;
import com.yacht.bootcamp.gibberish.HelperClasses.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessageFetchTask;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessagePushTask;
import com.yacht.bootcamp.gibberish.R;

import java.sql.SQLException;
import java.util.ArrayList;


public class Conversations_Activity extends ActionBarActivity {

    private ArrayList<Message> values;
    private ConversationAdapter adapter;
    private String local;
    private int updateInterval;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private SharedPreferences prefs;
    private MessageDataSource mds;
    private int unreadMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        unreadMessages = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        prefs = this.getSharedPreferences("Settings", MODE_PRIVATE);
        if(!prefs.contains("userName")){
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //settings
        updateInterval = prefs.getInt("activeUpdateInterval", 5000);
        local = prefs.getString("userName", "Cymru");
        this.getSupportActionBar().setTitle("Gibberish by "+local);

        //list
        values = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.conversationListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToMessages(values.get(position).getRemote());
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                conversationLongClicked(values.get(position));
                return true;
            }
        });
        values = upDateMessages();
        adapter = new ConversationAdapter(this, values);
        lv.setAdapter(adapter);

        //timer
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                values = upDateMessages();
                adapter.addAll(values);
                adapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, updateInterval);
            }
        };
        timerHandler.postDelayed(timerRunnable, updateInterval);
    }

    private ArrayList<Message> upDateMessages(){
        ArrayList<Message> result = null;
        RemoteMessageFetchTask rmft = new RemoteMessageFetchTask(this);
        rmft.execute(local);
        mds = new MessageDataSource(this);
        int newMessages = 0;
        try {
            mds.open();
            result = mds.getAllConversationsForUser(local);
            newMessages = mds.unreadMessages(local);
            mds.close();
        } catch (SQLException e) {
            Log.d("Gib", e.getMessage());
            mds.close();
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(newMessages == 0)
            nm.cancel(0);
        if(newMessages>unreadMessages){
            unreadMessages = newMessages;
            Intent intent = new Intent(this, Conversations_Activity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification not = new Notification.Builder(this)
                    .setContentTitle("Gibberish")
                    .setContentText("You have " + newMessages + " unread messages")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
            nm.notify(0, not);
        } else {
            unreadMessages = newMessages;
        }
        return result;
    }

    private void conversationLongClicked(final Message m){
        TextView tvAlert = new TextView(this);
        tvAlert.setMovementMethod(LinkMovementMethod.getInstance());
        tvAlert.setPadding(10,10,10,10);
        tvAlert.setText("Are you sure you want to delete this conversation?");
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
                            mds.deleteMessagesForUser(local, m.getRemote());
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

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }



    private void goToMessages(String remote){
        Intent intent = new Intent(this, Messages_Activity.class);
        intent.putExtra("remote", remote);
        intent.putExtra("unreadMessages", unreadMessages);
        startActivity(intent);
    }

    public void btnContactClicked(View view){
        EditText etContact = (EditText)findViewById(R.id.etContact);
        String remote = etContact.getText().toString();
        etContact.setText("");
        goToMessages(remote);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch(id){
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.miLogout:
                prefs.edit().remove("userName").apply();

                intent = new Intent(this, Login_Activity.class);
                startActivity(intent);
                return true;
            case R.id.miComposeNew:
                intent = new Intent(this, ComposeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
