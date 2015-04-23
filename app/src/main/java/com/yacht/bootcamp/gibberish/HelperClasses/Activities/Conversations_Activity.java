package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //settings
        updateInterval = 5000;
        local = "Cymru";

        //list
        values = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.conversationListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToMessages(values.get(position).getRemote());
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
                adapter.addAll(upDateMessages());
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
        MessageDataSource mds = new MessageDataSource(this);
        try {
            mds.open();
            result = mds.getAllConversationsForUser(local);
            mds.close();
        } catch (SQLException e) {
            Log.d("Gib", e.getMessage());
            mds.close();
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void goToMessages(String remote){
        Intent intent = new Intent(this, Messages_Activity.class);
        intent.putExtra("remote", remote);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
