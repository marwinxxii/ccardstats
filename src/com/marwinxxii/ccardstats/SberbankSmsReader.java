package com.marwinxxii.ccardstats;

import android.content.Context;
import android.database.Cursor;

public class SberbankSmsReader extends SmsReader {

    public static final String SERVICE_ADDRESS = "900";
    public static final String[] PROJECTION = { BODY };
    public static final String SELECTION = ADDRESS + "=?";
    public static final String[] ARGS = { SERVICE_ADDRESS };
    public static final String SORT_ORDER = DATE + " ASC";

    private Cursor mCursor;
    private int mColumnIndex;

    public SberbankSmsReader(Context context) {
        super(context);
        newQuery();
    }

    public void newQuery() {
        mCursor = query(PROJECTION, SELECTION, ARGS, SORT_ORDER);
        mColumnIndex = mCursor.getColumnIndex(BODY);
    }

    public SberbankNotification getNext() {
        if (mCursor == null) {
            throw new IllegalStateException("Cursor is already closed");
        }
        String body;
        do {
            if (!mCursor.moveToNext()) {
                mCursor.close();
                mCursor = null;
                return null;
            }
            body = mCursor.getString(mColumnIndex);
        } while (body.indexOf(';') == -1);
        return new SberbankNotification(body.split(";"));
    }

}
