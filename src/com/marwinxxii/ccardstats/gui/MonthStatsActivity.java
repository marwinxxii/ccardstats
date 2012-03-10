package com.marwinxxii.ccardstats.gui;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.marwinxxii.ccardstats.R;
import com.marwinxxii.ccardstats.gui.GetStatsTask.FilterType;
import com.marwinxxii.ccardstats.helpers.DateHelper;
import com.marwinxxii.ccardstats.helpers.MoneyHelper;


public class MonthStatsActivity extends SimpleListActivity {
    
    private String card;
    private int year, month;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        card = i.getStringExtra("card");
        year = i.getIntExtra("year", DateHelper.year);
        month = i.getIntExtra("month", DateHelper.month);
        if (year == DateHelper.year && month == DateHelper.month) {
            setDialogParams(R.string.reading_stats_dialog_title,
                    getString(R.string.current_month_stats_dialog_message));
        } else {
            setDialogParams(R.string.reading_stats_dialog_title,
                    getString(R.string.month_stats_dialog_message));
        }
    }
    
    @Override
    public void setListTitle() {
        if (year == DateHelper.year && month == DateHelper.month) {
            super.setTitleText(getString(R.string.current_month_stats_title, card));
        } else {
            super.setTitleText(getString(R.string.month_stats_title, card, year,
                    DateHelper.getMonthName(month)));
        }
    }
    
    @Override
    public void getItems() {
        new GetStatsTask(this, card, FilterType.MONTH, year, month).execute();
    }
    
    @Override
    public void onTaskComplete(Map<Integer, double[]> values) {
        ArrayList<String[]> result = new ArrayList<String[]>(values.size());
        for (Integer day:values.keySet()) {
            double[] money = values.get(day);
            String[] temp = {
                    day.toString(),
                    MoneyHelper.formatMoney(money[0], true),
                    MoneyHelper.formatMoney(money[1], false),
            };
            result.add(temp);
        }
        cacheValues(result);
        super.onTaskComplete(values);
    }
    
    public static Intent getStartingIntent(Context context, String card, int year, int month) {
        Intent i = new Intent(context, MonthStatsActivity.class);
        i.putExtra("card", card);
        i.putExtra("year", year);
        i.putExtra("month", month);
        return i;
    }
}
