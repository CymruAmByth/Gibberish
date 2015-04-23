package com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yacht.bootcamp.gibberish.HelperClasses.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Joey on 4/10/2015.
 */
public class MessageDataSource {

    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.MESSAGES_COLUMN_ID,
                                    MySQLiteHelper.MESSAGES_COLUMN_INCOMING,
                                    MySQLiteHelper.MESSAGES_COLUMN_LOCAL,
                                    MySQLiteHelper.MESSAGES_COLUMN_REMOTE,
                                    MySQLiteHelper.MESSAGES_COLUMN_MESSAGE,
                                    MySQLiteHelper.MESSAGES_COLUMN_TIMESTAMP,
                                    MySQLiteHelper.MESSAGES_COLUMN_READ};

    public MessageDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public Message saveMessage(Message m){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MESSAGES_COLUMN_INCOMING, m.isIncoming());
        values.put(MySQLiteHelper.MESSAGES_COLUMN_LOCAL, m.getLocal());
        values.put(MySQLiteHelper.MESSAGES_COLUMN_REMOTE, m.getRemote());
        values.put(MySQLiteHelper.MESSAGES_COLUMN_MESSAGE, m.getMessage());
        values.put(MySQLiteHelper.MESSAGES_COLUMN_TIMESTAMP, m.getTimestamp());
        values.put(MySQLiteHelper.MESSAGES_COLUMN_READ, m.isRead());
        long insertId = db.insert(MySQLiteHelper.TABLE_MESSAGES, null, values);
        Message newMessage = null;
        if(insertId != -1) {
            Cursor cursor = db.query(MySQLiteHelper.TABLE_MESSAGES, allColumns,
                    MySQLiteHelper.MESSAGES_COLUMN_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            newMessage = cursorToMessage(cursor);
            cursor.close();
        }
        return newMessage;
    }

    public void deleteMessage(Message message){
        long id = message.getId();
        db.delete(MySQLiteHelper.TABLE_MESSAGES, MySQLiteHelper.MESSAGES_COLUMN_ID + " = " + id, null);
    }

    public void deleteMessagesForUser(String local, String remote){
        String whereClause = MySQLiteHelper.MESSAGES_COLUMN_LOCAL + " = ? and " + MySQLiteHelper.MESSAGES_COLUMN_REMOTE + " = ?";
        db.delete(MySQLiteHelper.TABLE_MESSAGES, whereClause, new String[]{local, remote});
    }

    public void markMessageAsRead(Message message){
        long id = message.getId();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MESSAGES_COLUMN_READ, true);
        db.update(MySQLiteHelper.TABLE_MESSAGES, values,MySQLiteHelper.MESSAGES_COLUMN_ID + " = " + id, null);
    }


    public ArrayList<Message> getAllMessagesBetweenLocalAndRemote(String local, String remote){
        ArrayList<Message> messages = new ArrayList<>();
        String whereClause = MySQLiteHelper.MESSAGES_COLUMN_LOCAL + " = ? and " + MySQLiteHelper.MESSAGES_COLUMN_REMOTE + " = ?";
        Cursor cursor = db.query(MySQLiteHelper.TABLE_MESSAGES,
                allColumns, whereClause, new String[] {local, remote}, null, null, MySQLiteHelper.MESSAGES_COLUMN_TIMESTAMP);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            messages.add(cursorToMessage(cursor));
            cursor.moveToNext();
        }
        Collections.reverse(messages);
        return messages;
    }

    public ArrayList<Message> getAllConversationsForUser(String local){
        ArrayList<Message> messages = new ArrayList<>();
        String whereClause = "local = ?";
        Cursor cursor = db.query(MySQLiteHelper.TABLE_MESSAGES,
                new String[]{
                        MySQLiteHelper.MESSAGES_COLUMN_ID,
                        MySQLiteHelper.MESSAGES_COLUMN_INCOMING,
                        MySQLiteHelper.MESSAGES_COLUMN_LOCAL,
                        MySQLiteHelper.MESSAGES_COLUMN_REMOTE,
                        MySQLiteHelper.MESSAGES_COLUMN_MESSAGE,
                        "max("+MySQLiteHelper.MESSAGES_COLUMN_TIMESTAMP+")",
                        MySQLiteHelper.MESSAGES_COLUMN_READ},
                whereClause, new String[] {local}, MySQLiteHelper.MESSAGES_COLUMN_REMOTE, null, MySQLiteHelper.MESSAGES_COLUMN_TIMESTAMP);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            messages.add(cursorToMessage(cursor));
            cursor.moveToNext();
        }
        Collections.reverse(messages);
        return messages;

    }

    private Message cursorToMessage(Cursor cursor){
        return new Message(cursor.getInt(0), cursor.getInt(1)>0, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getInt(6)>0);
    }
}
