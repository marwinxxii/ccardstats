package com.marwinxxii.ccardstats.notifications;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsReader {

    public static final String ID = "_id";
    public static final String THREAD_ID = "thread_id";
    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String PROTOCOL = "protocol";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String REPLY_PATH_PRESENT = "reply_path_present";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";
    public static final String SERVICE_CENTER = "service_center";
    public static final String LOCKED = "locked";
    public static final String ERROR_CODE = "error_code";
    public static final String SEEN = "seen";

    protected static final Uri sUri = Uri.parse("content://sms");
    protected ContentResolver resolver;

    public SmsReader(Context context) {
        resolver = context.getContentResolver();
    }

    public Cursor query(String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return resolver.query(sUri, projection, selection, selectionArgs,
                sortOrder);
    }
}
