package com.marwinxxii.ccardstats;

import android.content.Context;
import android.database.Cursor;

public class SmsNotificationReader extends SmsReader {

    private static String[] SERVICE_ADRESSES;
    private static NotificationService[] SERVICES;

    private static final String[] PROJECTION = { BODY };
    private static final String SELECTION = ADDRESS + " in (?)";
    private static final String SORT_ORDER = DATE + " ASC";

    private Cursor mCursor;

    static {
        SERVICES = new NotificationService[] { new SberbankService() };
        SERVICE_ADRESSES = new String[SERVICES.length];
        int i = 0;
        for (NotificationService ns : SERVICES) {
            SERVICE_ADRESSES[i++] = ns.getAddress();
        }
    }

    public static SmsNotificationReader getReader(Context context) {
        return new SmsNotificationReader(context, SERVICE_ADRESSES);
    }

    public SmsNotificationReader(Context context, String[] adresses) {
        super(context);
        mCursor = query(PROJECTION, SELECTION, adresses, SORT_ORDER);
    }

    public boolean hasNext() {
        return !mCursor.isLast();
    }

    public SmsNotification next() {
        while (mCursor.moveToNext()) {
            for (NotificationService ns : SERVICES) {
                try {
                    return ns.parse(mCursor.getString(0));
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
        mCursor.close();
        return null;
    }
}
