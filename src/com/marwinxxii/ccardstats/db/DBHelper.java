package com.marwinxxii.ccardstats.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.marwinxxii.ccardstats.helpers.DateHelper;
import com.marwinxxii.ccardstats.notifications.SmsNotification;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ccardstats";
    private static final String CARDS_QUERY =
            "create table cards (name text primary key asc, alias text not null,"
                    + " balance real not null)";
    private static String STATS_QUERY =
            "create table stats (card text not null, date integer not null, "
                    + "ismonthly integer not null, "
                    + "income real not null, outcome real not null, "
                    + "primary key(card asc, date desc) "
                    + "foreign key (card) references cards(name))";

    private boolean mWasCreated = false;
    private SQLiteDatabase db;
    public static boolean storeMonth = false;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARDS_QUERY);
        db.execSQL(STATS_QUERY);
        mWasCreated = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean wasCreated() {
        return mWasCreated;
    }

    public void close() {
        super.close();
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public void init() {
        if (db == null) {
            try {
                db = getWritableDatabase();
            } catch (SQLiteException e) {
                db = getReadableDatabase();
            }
        }
    }

    public List<Card> getCards() {
        init();
        Cursor cursor = db.rawQuery("select * from cards", null);
        List<Card> cards = new ArrayList<Card>();
        while (cursor.moveToNext()) {
            cards.add(new Card(cursor.getString(0), cursor.getString(1), cursor.getDouble(2)));
        }
        cursor.close();
        return cards;
    }
    
    public Card getCard(String name) {
        init();
        Cursor cursor = db.rawQuery("select * from cards where name=?", new String[]{name});
        Card card = null;
        if (cursor.moveToNext())
            card= new Card(cursor.getString(0), cursor.getString(1), cursor.getDouble(2));
        cursor.close();
        return card;
    }

    public void saveCard(String name, String alias, double balance) {
        init();
        db.execSQL(String.format(Locale.US, "insert or replace into cards values('%s', '%s', %f)",
                name, alias, balance));
    }

    public double[] getAllStats(Card c, int year, int month, int day) {
        init();
        int monthDate = year * 10000 + month * 100;
        String[] args = { String.valueOf(monthDate),
                String.valueOf(monthDate + day) };
        String query = "select sum(income), sum(outcome) from stats where ismonthly=1 union all "
                + "select sum(income), sum(outcome) from stats where ismonthly=1 and date=? union all "
                + "select sum(income), sum(outcome) from stats where ismonthly=0 and date=?";
        Cursor cursor = db.rawQuery(query, args);

        double[] res = new double[6];
        int i = 0;
        while (cursor.moveToNext()) {
            res[i++] = cursor.getDouble(0);
            res[i++] = cursor.getDouble(1);
        }
        cursor.close();
        return res;
    }
    
    public Map<Integer, double[]> getYearStats(String card, int year) {
        init();
        int endYear = (year+1) *10000;
        year = year * 10000;
        String query = "select (date-%d)/100, income, outcome from stats" +
                " where card='%s' and ismonthly=1 and date>=%d and date<%d";
        Cursor cursor = db.rawQuery(String.format(query, year, card, year, endYear), null);
        TreeMap<Integer, double[]> result = new TreeMap<Integer, double[]>();
        while (cursor.moveToNext()) {
            result.put(cursor.getInt(0), new double[]{cursor.getDouble(1), cursor.getDouble(2)});
        }
        cursor.close();
        return result;
    }
    
    public Map<Integer, double[]> getMonthStats(String card, int year, int month) {
        init();
        int startDate = year*10000 + month*100, endDate;
        if (month < 12) {
            endDate = year*10000 + (month + 1)*100;
        } else {
            endDate=(year+1)*10000 + 1*100;
        }
        String query = "select (date-%d), income, outcome from stats where card='%s' and " +
        		"ismonthly=0 and date>=%d and date<%d";
        Cursor c = db.rawQuery(String.format(query, startDate, card, startDate, endDate), null);
        TreeMap<Integer, double[]> result = new TreeMap<Integer, double[]>();
        while(c.moveToNext()) {
            result.put(c.getInt(0), new double[]{c.getDouble(1), c.getDouble(2)});
        }
        c.close();
        return result;
    }
    
    public List<Integer> getYears(Card card) {
        init();
        Cursor c = db.rawQuery("select distinct date/10000 from stats where card=?",
                new String[]{card.getName()});
        ArrayList<Integer> result = new ArrayList<Integer>();
        while (c.moveToNext()) {
            result.add(c.getInt(0));
        }
        c.close();
        return result;
    }

    public void addNotification(SmsNotification notif) {
        int year = notif.year * 10000;
        int month = notif.month * 100;
        String[] args = { notif.card, String.valueOf(year + month + notif.day) };
        String income, outcome;
        if (notif.diff > 0) {
            income = String.format(Locale.US, "%f", notif.diff);
            outcome = "0";
        } else {
            income = "0";
            outcome = String.format(Locale.US, "%f", -notif.diff);
        }
        String selectQuery = "select * from stats where card=? and date=?";
        String insertQuery = "insert into stats values('%s', %s, %d, %s, %s)";
        String updateQuery = "update stats set income=income+%s, outcome=outcome+%s "
                + "where card='%s' and date=%s";
        db.beginTransaction();
        Cursor cursor;
        if (!storeMonth || (notif.year == DateHelper.year && notif.month == DateHelper.month)) {
            cursor = db.rawQuery(selectQuery, args);
            if (cursor.isAfterLast()) {
                db.execSQL(String.format(insertQuery, notif.card, args[1], 0, income, outcome));
            } else {
                db.execSQL(String.format(updateQuery, income, outcome, notif.card, args[1]));
            }
            cursor.close();
        }
        args[1] = String.valueOf(year + month);
        cursor = db.rawQuery(selectQuery, args);
        if (cursor.isAfterLast()) {
            db.execSQL(String.format(insertQuery, notif.card, args[1], 1, income, outcome));
        } else {
            db.execSQL(String.format(updateQuery, income, outcome, notif.card, args[1]));
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    
    public void deleteOldEntries(List<Card> cards) {
        int date = DateHelper.year * 10000 + DateHelper.month * 100;
        String query = "delete from stats where card='%s' and ismonthly=0 and date<%d";
        for(Card c:cards) {
            db.execSQL(String.format(query, c.getName(), date));
        }
    }
}
