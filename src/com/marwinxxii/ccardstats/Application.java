package com.marwinxxii.ccardstats;

import com.marwinxxii.ccardstats.helpers.DateHelper;


public class Application extends android.app.Application {
    
    @Override
    public void onCreate() {
        DateHelper.setMonthNames(getResources().getStringArray(R.array.month_names));
    }
}
