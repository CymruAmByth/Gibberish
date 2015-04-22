package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.yacht.bootcamp.gibberish.HelperClasses.Adapter.ConversationAdapter;
import com.yacht.bootcamp.gibberish.HelperClasses.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessageFetchTask;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.RemoteMessagePushTask;
import com.yacht.bootcamp.gibberish.R;

import java.sql.SQLException;
import java.util.ArrayList;


public class Conversations extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        RemoteMessageFetchTask rmft = new RemoteMessageFetchTask(this);
        rmft.execute("Cymru");

        final ListView lv = (ListView) findViewById(R.id.conversationListView);


        MessageDataSource mds = new MessageDataSource(this);
        ArrayList<Message> values = new ArrayList<>();
        try {
            mds.open();
            values = mds.getAllConversationsForUser("Cymru");
            mds.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mds.close();
        }

        final ConversationAdapter adapter = new ConversationAdapter(this, values);
        lv.setAdapter(adapter);


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
