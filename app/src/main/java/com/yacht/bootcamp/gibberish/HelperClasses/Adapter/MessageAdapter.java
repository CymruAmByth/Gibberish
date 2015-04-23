package com.yacht.bootcamp.gibberish.HelperClasses.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.R;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by gebruiker on 22/04/15.
 */
public class MessageAdapter extends ArrayAdapter<Message>{

    private final Context context;
    private final List<Message> values;

    private class ViewHolder {
        public TextView tvTileLeft;
        public TextView tvTileRight;
        public TextView tvDate;
        public TextView tvMessage;
    }


    public MessageAdapter(Context context, List<Message> objects) {
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
            holder.tvTileLeft = (TextView)v.findViewById(R.id.tvTileLeft);
            holder.tvTileRight = (TextView)v.findViewById(R.id.tvTileRight);
            holder.tvMessage = (TextView)v.findViewById(R.id.tvMessage);
            v.setTag(holder);
        }
        Message entry = values.get(position);
        if(entry != null){
            ViewHolder holder = (ViewHolder)v.getTag();
            if(entry.isIncoming()) {
                holder.tvTileRight.setVisibility(View.GONE);
                holder.tvTileLeft.setVisibility(View.VISIBLE);
                holder.tvTileLeft.setText(entry.getRemote().substring(0, 2).toLowerCase());
                holder.tvMessage.setGravity(Gravity.LEFT);
                holder.tvDate.setGravity(Gravity.LEFT);
                if(entry.isRead()){
                    holder.tvTileLeft.setBackgroundColor(context.getResources().getColor(R.color.blue));
                }
                else{
                    holder.tvTileLeft.setBackgroundColor(context.getResources().getColor(R.color.red));
                    MessageDataSource mds = new MessageDataSource(context);
                    try {
                        mds.open();
                        mds.markMessageAsRead(entry);
                        mds.close();
                    } catch (SQLException e) {
                        mds.close();
                        Log.d("Gib", e.getMessage());
                    }
                }
            }
            else{
                holder.tvTileRight.setVisibility(View.VISIBLE);
                holder.tvTileLeft.setVisibility(View.GONE);
                holder.tvTileRight.setText(entry.getLocal().substring(0, 2).toLowerCase());
                holder.tvMessage.setGravity(Gravity.RIGHT);
                holder.tvDate.setGravity(Gravity.RIGHT);
            }
            holder.tvDate.setText(entry.getTimestampAsString());
            holder.tvMessage.setText(entry.getMessage());
        }
        return v;
    }
}
