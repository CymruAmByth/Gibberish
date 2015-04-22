package com.yacht.bootcamp.gibberish.HelperClasses.Activities;

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
    private ListView lv;
    private MessageAdapter adapter;
    private ArrayList<Message> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            remote = extras.getString("remote");
        }

        local = "Cymru";

        RemoteMessageFetchTask rmft = new RemoteMessageFetchTask(this);
        rmft.execute(local);

        lv = (ListView) findViewById(R.id.messageListView);


        MessageDataSource mds = new MessageDataSource(this);
        values = new ArrayList<>();
        try {
            mds.open();
            values = mds.getAllMessagesBetweenLocalAndRemote(local, remote);
            mds.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mds.close();
        }

        adapter = new MessageAdapter(this, values);
        lv.setAdapter(adapter);
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
}
