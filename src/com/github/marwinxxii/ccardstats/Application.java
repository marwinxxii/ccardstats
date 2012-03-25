package com.github.marwinxxii.ccardstats;

import java.util.HashMap;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.marwinxxii.ccardstats.db.DBHelper;
import com.github.marwinxxii.ccardstats.helpers.DateHelper;
import com.github.marwinxxii.ccardstats.helpers.MoneyHelper;
import com.github.marwinxxii.ccardstats.R;


public class Application extends android.app.Application {
    
    @Override
    public void onCreate() {
        DateHelper.setMonthNames(getResources().getStringArray(R.array.month_names));
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        HashMap<String, Double> rates = new HashMap<String, Double>(2);
        rates.put("usd", Double.parseDouble(prefs.getString("exchange_rates_usd", "30.0")));
        rates.put("eur", Double.parseDouble(prefs.getString("exchange_rates_eur", "39.0")));
        MoneyHelper.setExchangeRates(rates);
        DBHelper.storeMonth = prefs.getBoolean("store_month", DBHelper.storeMonth);
    }
}
