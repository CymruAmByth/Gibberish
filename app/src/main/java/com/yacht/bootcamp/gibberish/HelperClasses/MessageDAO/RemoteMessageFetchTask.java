package com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yacht.bootcamp.gibberish.HelperClasses.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by gebruiker on 22/04/15.
 */
public class RemoteMessageFetchTask extends AsyncTask<String, Integer, Integer>{
    private Context context;

    public RemoteMessageFetchTask(Context context){
        super();
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        Integer result = 0;
        String data = "";
        String local = params[0];
        ArrayList<Message> newMessages = new ArrayList<>();
        try{
            URL server = new URL("http://www.chattestserver.com/retrievemessages.php?receiver="+local);
            HttpURLConnection con = (HttpURLConnection) server.openConnection();
            InputStreamReader input = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(input);
            String line;
            while ((line = reader.readLine())!= null)
            {
                data += line;
            }
        } catch (MalformedURLException e) {
            Log.d("Gib", e.getMessage());
        } catch (IOException e) {
            Log.d("Gib", e.getMessage());
        }

        if(data.length()>2) {
            MessageDataSource mds = new MessageDataSource(context);
            try {

                JSONObject obj = new JSONObject(data);
                JSONArray JMessageArray = obj.getJSONArray("content");
                mds.open();
                for (int i = 0; i < JMessageArray.length(); i++) {
                    JSONObject jMessage = JMessageArray.getJSONObject(i);
                    mds.saveMessage(new Message(0,
                            true,
                            local,
                            jMessage.getString("sender"),
                            jMessage.getString("message"),
                            jMessage.getLong("timestamp"),
                            false));
                }
                mds.close();
            } catch (JSONException e) {
                Log.d("Gib", e.getMessage());
            } catch (SQLException e) {
                mds.close();
                Log.d("Gib", e.getMessage());
            }
        }

        return result;
    }
}
