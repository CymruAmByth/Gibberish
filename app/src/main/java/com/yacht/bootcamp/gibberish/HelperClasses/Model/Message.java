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
    private final DateFormat DAYFORMAT = new SimpleDateFormat("dd-MM-yyyy");
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
        DateFormat df = new SimpleDateFormat("HH:mm");
        Date date = new Date(timestamp);
        return getDay() + " " +df.format(date);
    }

    public String getDay(){
        if(isToday())
            return "Today";
        else if(isYesterday())
            return "Yesterday";
        else
            return DAYFORMAT.format(new Date(timestamp));

    }

    private Boolean isToday(){
        String strToday = DAYFORMAT.format(new Date());
        String strTest = DAYFORMAT.format(new Date(timestamp));
        return strToday.equals(strTest);
    }

    private Boolean isYesterday(){
        String strToday = DAYFORMAT.format(new Date());
        long tempTimeStamp = timestamp + (24*60*60*1000);
        String strTest = DAYFORMAT.format(new Date(tempTimeStamp));
        return strToday.equals(strTest);
    }

    @Override
    public int compareTo(Message another) {
        return (int) (this.timestamp - another.getTimestamp());
    }
}
