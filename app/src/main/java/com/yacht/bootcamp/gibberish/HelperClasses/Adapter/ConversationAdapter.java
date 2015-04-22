package com.yacht.bootcamp.gibberish.HelperClasses.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Message;
import com.yacht.bootcamp.gibberish.R;

import java.util.List;

/**
 * Created by gebruiker on 22/04/15.
 */
public class ConversationAdapter extends ArrayAdapter<Message>{

    private final Context context;
    private final List<Message> values;

    private class ViewHolder {
        public TextView tvRemote;
        public TextView tvDate;
        public TextView tvLastMessage;
    }


    public ConversationAdapter(Context context, List<Message> objects) {
        super(context, R.layout.conversation_list_item, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.conversation_list_item, null);
            ViewHolder holder = new ViewHolder();
            holder.tvDate = (TextView)v.findViewById(R.id.tvDate);
            holder.tvRemote = (TextView)v.findViewById(R.id.tvTile);
            holder.tvLastMessage = (TextView)v.findViewById(R.id.tvMessage);
            v.setTag(holder);
        }
        Message entry = values.get(position);
        if(entry != null){
            ViewHolder holder = (ViewHolder)v.getTag();
            holder.tvDate.setText(entry.getRemote()+ " : " + entry.getTimestampAsString());
            holder.tvRemote.setText(entry.getRemote().substring(0,2).toLowerCase());
            String sender;
            if(entry.isIncoming())
                sender = entry.getRemote();
            else
                sender = entry.getLocal();
            holder.tvLastMessage.setText(sender + " : " +entry.getMessage());
        }
        return v;
    }
}
