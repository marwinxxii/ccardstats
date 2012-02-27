package com.marwinxxii.ccardstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        Object[] pdus = (Object[]) bundle.get("pdus");
        DatabaseHelper helper = new DatabaseHelper(context);
        for (int i=0; i<pdus.length; i++){
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[i]);
            SmsNotification notif = SmsNotificationReader.parse(sms);
            if (notif != null) {
                helper.updateCard(notif);
            }
        }
        helper.close();
    }

}
