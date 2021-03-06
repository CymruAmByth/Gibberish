package com.yacht.bootcamp.gibberish.HelperClasses.Adapter;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Model.Message;
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
            holder.tvRemote = (TextView)v.findViewById(R.id.tvTileLeft);
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
            String lastMessage = sender + " : " +entry.getMessage();
            int maxLength = 80;
            if(lastMessage.length()>maxLength){
                lastMessage = lastMessage.substring(0, maxLength)+"...";
            }
            holder.tvLastMessage.setText(lastMessage);
            if(entry.isRead()) {
                GradientDrawable gd = (GradientDrawable)holder.tvRemote.getBackground();
                gd.setColor(context.getResources().getColor(R.color.blue));
                holder.tvRemote.setBackground(gd);
            }
            else {
                GradientDrawable gd = (GradientDrawable)holder.tvRemote.getBackground();
                gd.setColor(context.getResources().getColor(R.color.red));
                holder.tvRemote.setBackground(gd);
            }
        }
        return v;
    }
}
