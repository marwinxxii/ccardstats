package com.marwinxxii.ccardstats;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = null;//"com.marwinxxii.card_entries";
    private static final String CARDS_TABLE = "cards";
    private static final String CARD_CHANGES_QUERY = "create table card_changes"
            + " (card text, diff real, date integer)";
    private static final String CARDS_QUERY = "create table cards " +
    		"(name text, alias text, available real, income real, outcome real)";
    private static final int VERSION = 1;

    private boolean mWasCreated = false;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARDS_QUERY);
        db.execSQL(CARD_CHANGES_QUERY);
        mWasCreated = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldV, int newV) {}

    public boolean wasCreated() {
        return mWasCreated;
    }
    
    public void initForWrite() {
        if (db != null)
            throw new IllegalStateException("db is already opened");
        db = getWritableDatabase();
    }
    
    public void initForRead() {
        if (db != null)
            throw new IllegalStateException("db is already opened");
        db = getReadableDatabase();
    }
    
    public void close() {
        db.close();
        db = null;
    }
    
    public long insertNotification(SmsNotification sms) {
        return 0;
    }
    
    public Card addCard(String name, double available) {
        return new Card(name, name, available, 0,0);
    }
    
    public ArrayList<Card> getCards() {
        if (db == null) db = getReadableDatabase();
        Cursor cursor = db.query(CARDS_TABLE, null, null, null, null, null, null);
        ArrayList<Card> result = new ArrayList<Card>();
        
        while (cursor.moveToNext()) {
            result.add(new Card(cursor.getString(0), cursor.getString(1),
                    cursor.getDouble(2), cursor.getDouble(3),
                    cursor.getDouble(4)));
        }
        cursor.close();
        return result;
    }

}
