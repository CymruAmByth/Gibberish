package com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yacht.bootcamp.gibberish.HelperClasses.Message;

import org.apache.http.client.methods.HttpPost;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Created by gebruiker on 22/04/15.
 */
public class RemoteMessagePushTask extends AsyncTask <Message, Integer, Void> {

    private Context context;

    public RemoteMessagePushTask(Context context){
        super();
        this.context = context;
    }

    @Override
    protected Void doInBackground(Message... params) {
        Message m = params[0];
        MessageDataSource mds = new MessageDataSource(context);
        try{
            mds.open();
            mds.saveMessage(m);
            mds.close();
        } catch (SQLException e) {
            mds.close();
            Log.d("Gib", e.getMessage());
        }
        String post = "sender=" + m.getLocal();
        post += "&receiver=" + m.getRemote();
        post += "&message=" +m.getMessage();
        post += "&timestamp=" +String.valueOf(m.getTimestamp());
        byte[] postData = post.getBytes(Charset.forName("UTF-8"));
        int postDataLength = postData.length;
        String server = "http://www.chattestserver.com/sendmessage.php";
        try {
            URL url = new URL(server);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty( "charset", "utf-8");
            con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            con.setUseCaches( false );
            con.getOutputStream().write(postData);
            con.getInputStream();
        } catch (MalformedURLException e) {
            Log.d("Gib", e.getMessage());
        } catch (IOException e) {
            Log.d("Gib", e.getMessage());
        }
        return null;
    }
}
