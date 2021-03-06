package com.yacht.bootcamp.gibberish.HelperClasses.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yacht.bootcamp.gibberish.HelperClasses.Model.Message;
import com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO.MessageDataSource;
import com.yacht.bootcamp.gibberish.R;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by gebruiker on 22/04/15.
 */
public class MessageAdapter extends BaseAdapter{

    private final Context context;
    private final List<Message> messages;

    private class ViewHolder {
        public TextView tvTileLeft;
        public TextView tvTileRight;
        public TextView tvDate;
        public TextView tvMessage;
    }

    public MessageAdapter(Context context, List<Message> messages) {
        super();
        this.context = context;
        this.messages = messages;
    }

    public void addMessage(Message msg){
        messages.add(0, msg);
        this.notifyDataSetChanged();
    }

    public void addMessages(List<Message> msgs){
        for(Message m :msgs){
            messages.add(0, m);
        }
        this.notifyDataSetChanged();
    }

    public  void deleteMessage(Message m){
        messages.remove(m);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
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
        Message entry = messages.get(position);
        if(entry != null){
            ViewHolder holder = (ViewHolder)v.getTag();
            if(entry.isIncoming()) {
                holder.tvTileRight.setVisibility(View.GONE);
                holder.tvTileLeft.setVisibility(View.VISIBLE);
                holder.tvTileLeft.setText(entry.getRemote().substring(0, 2).toLowerCase());
                holder.tvMessage.setGravity(Gravity.LEFT);
                holder.tvDate.setGravity(Gravity.LEFT);
                if(entry.isRead()){
                    GradientDrawable gd = (GradientDrawable)holder.tvTileLeft.getBackground();
                    gd.setColor(context.getResources().getColor(R.color.blue));
                    holder.tvTileLeft.setBackground(gd);
                }
                else{
                    GradientDrawable gd = (GradientDrawable)holder.tvTileLeft.getBackground();
                    gd.setColor(context.getResources().getColor(R.color.red));
                    holder.tvTileLeft.setBackground(gd);
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
                GradientDrawable gd = (GradientDrawable)holder.tvTileLeft.getBackground();
                gd.setColor(context.getResources().getColor(R.color.blue));
                holder.tvTileRight.setBackground(gd);
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
