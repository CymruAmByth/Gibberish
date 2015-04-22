package com.yacht.bootcamp.gibberish.HelperClasses.MessageDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Joey on 4/10/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_MESSAGES = "Messages";
    public static final String MESSAGES_COLUMN_ID = "_id";
    public static final String MESSAGES_COLUMN_INCOMING = "incoming";
    public static final String MESSAGES_COLUMN_LOCAL = "local";
    public static final String MESSAGES_COLUMN_REMOTE = "remote";
    public static final String MESSAGES_COLUMN_MESSAGE = "message";
    public static final String MESSAGES_COLUMN_TIMESTAMP = "timestamp";
    public static final String MESSAGES_COLUMN_READ = "read";


    public static final String DATABASE_NAME = "Gibberish.db";
    private static final int VERSION = 1;

    private static final String QUESTION_CREATE = "create table "
            + TABLE_MESSAGES + "("
            + MESSAGES_COLUMN_ID + " integer primary key autoincrement, "
            + MESSAGES_COLUMN_INCOMING + " boolean not null,"
            + MESSAGES_COLUMN_LOCAL + " text not null,"
            + MESSAGES_COLUMN_REMOTE + " text not null,"
            + MESSAGES_COLUMN_MESSAGE + " text not null,"
            + MESSAGES_COLUMN_TIMESTAMP + " bigint not null,"
            + MESSAGES_COLUMN_READ + " boolean not null);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUESTION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }
}
