package com.marwinxxii.ccardstats.gui;

import java.util.HashMap;
import java.util.HashSet;

import com.marwinxxii.ccardstats.SmsReader;
import com.marwinxxii.ccardstats.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SberbankActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Context context = getApplicationContext();
        SmsReader reader = new SmsReader(context);
        String[] projection = { SmsReader.BODY };
        Cursor cursor = reader.query(projection, "address = 900", null, null);
        int bodyIndex = cursor.getColumnIndex("body");
        double average = 0.0;
        int i = 0;
        LinearLayout lv = (LinearLayout) findViewById(R.id.hello);
        HashMap<Integer, HashSet<String>> hm = new HashMap<Integer, HashSet<String>>();
        while (cursor.moveToNext()) {
            String body = cursor.getString(bodyIndex);
            if (body.indexOf(';') == -1)
                continue;
            String[] temp = body.split(";");
            /*
             * for (int k=0;k<temp.length;k++) { if (!hm.containsKey(k))
             * hm.put(k, new HashSet<String>()); hm.get(k).add(temp[k]); }
             * average+=temp.length; i+=1;
             */
            if (temp.length != 7) {
                TextView tv = new TextView(context);
                tv.setText(body);
                lv.addView(tv);
            }
        }
        cursor.close();
        /*
         * TextView tv = new TextView(context);
         * tv.setText(String.valueOf(average/i)); for(Integer key:hm.keySet()) {
         * tv = new TextView(context); StringBuilder builder = new
         * StringBuilder(); builder.append(key.toString());
         * builder.append('\n'); for(String s:hm.get(key)) { builder.append(s);
         * builder.append('\n'); } tv.setText(builder.toString());
         * lv.addView(tv); }
         */
    }
}
