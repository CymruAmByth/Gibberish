package com.yacht.bootcamp.gibberish.HelperClasses.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gebruiker on 22/04/15.
 */
public class Message implements  Comparable<Message>{
    private final int id;
    private final boolean incoming;
    private final String local, remote, message;
    private final long timestamp;
    private boolean read;

    public Message(int id, boolean incoming, String local, String remote, String message, long timestamp, boolean read) {
        this.id = id;
        this.incoming = incoming;
        this.local = local;
        this.remote = remote;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public String getLocal() {
        return local;
    }

    public String getRemote() {
        return remote;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public String getTimestampAsString(){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(timestamp);
        return df.format(date);
    }

    @Override
    public int compareTo(Message another) {
        return (int) (this.timestamp - another.getTimestamp());
    }
}
