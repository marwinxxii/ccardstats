package com.github.marwinxxii.ccardstats.notifications;

import java.util.HashMap;

import com.github.marwinxxii.ccardstats.db.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class NotificationReader extends SmsReader {

    private DBHelper helper;
    private static String[] addresses;

    public NotificationReader(Context context, DBHelper helper) {
        super(context);
        this.helper = helper;
        if (addresses == null) {
            addresses = new String[NotificationService.SERVICES.length];
            for (int i = 0; i < NotificationService.SERVICES.length; i++) {
                addresses[i] = NotificationService.SERVICES[i].getAddress();
            }
        }
    }

    public void readNotificationsToDB() {
        Cursor cursor = query(new String[] { BODY }, ADDRESS + " in (?)",
                addresses, DATE + " asc");
        HashMap<String, Double> cards = new HashMap<String, Double>();
        while (cursor.moveToNext()) {
            SmsNotification notif = SmsParser.parse(cursor.getString(0));
            if (notif == null)
                continue;
            helper.addNotification(notif);
            cards.put(notif.card, notif.balance);
            Log.d("nr", notif.diff + "/" + notif.balance);
        }
        for (String name : cards.keySet()) {
            helper.saveCard(name, name, cards.get(name));
        }
        cursor.close();
    }

}
