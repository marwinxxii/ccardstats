package com.marwinxxii.ccardstats;

import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.telephony.SmsMessage;

public class SmsNotificationReader extends SmsReader {

    private static String[] SERVICE_ADRESSES_ARR;
    private static HashSet<String> SERVICE_ADRESSES;
    private static NotificationService[] SERVICES;

    private static final String[] PROJECTION = { BODY };
    private static final String SELECTION = ADDRESS + " in (?)";
    private static final String SORT_ORDER = DATE + " ASC";

    private Cursor mCursor;

    static {
        SERVICES = new NotificationService[] { new SberbankService() };
        
        SERVICE_ADRESSES = new HashSet<String>();
        for (NotificationService ns : SERVICES) {
            SERVICE_ADRESSES.add(ns.getAddress());
        }
        SERVICE_ADRESSES_ARR = new String[SERVICE_ADRESSES.size()];
        SERVICE_ADRESSES_ARR=SERVICE_ADRESSES.toArray(SERVICE_ADRESSES_ARR);
    }

    public static SmsNotificationReader getReader(Context context) {
        return new SmsNotificationReader(context, SERVICE_ADRESSES_ARR);
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
    
    public static SmsNotification parse(SmsMessage message) {
        String address = message.getOriginatingAddress();
        if (address != null && !SERVICE_ADRESSES.contains(address)) return null;
        String body = message.getDisplayMessageBody();
        for (NotificationService ns : SERVICES) {
            try {
                return ns.parse(body);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        return null;
    }
}
