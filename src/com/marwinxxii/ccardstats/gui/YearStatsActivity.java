package com.marwinxxii.ccardstats.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.marwinxxii.ccardstats.R;
import com.marwinxxii.ccardstats.db.Card;
import com.marwinxxii.ccardstats.db.DBHelper;
import com.marwinxxii.ccardstats.gui.GetStatsTask.FilterType;
import com.marwinxxii.ccardstats.helpers.DateHelper;
import com.marwinxxii.ccardstats.helpers.MoneyHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class YearStatsActivity extends SimpleListActivity implements OnItemClickListener{
    
    private String card;
    private int year;
    private static List<Integer> months;
    private Toast toast;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogParams(R.string.reading_stats_dialog_title,
                getString(R.string.year_stats_dialog_message));
        Intent i = getIntent();
        card = i.getStringExtra("card");
        year = i.getIntExtra("year", DateHelper.year);
        mItemsList.setOnItemClickListener(this);
        toast = Toast.makeText(this, R.string.year_stats_nomonth_toast, Toast.LENGTH_SHORT);
    }
    
    @Override
    public void setListTitle() {
        super.setTitleText(getString(R.string.year_stats_title, card, year));
    }
    
    @Override
    public void getItems() {
        new GetStatsTask(this, card, FilterType.YEAR, year, 0).execute();
    }
    
    @Override
    public void onTaskComplete(Map<Integer, double[]> values) {
        ArrayList<String[]> result = new ArrayList<String[]>(values.size());
        months = new ArrayList<Integer>(values.size());
        for (Integer month:values.keySet()) {
            double[] money = values.get(month);
            String[] temp = {
                    DateHelper.getMonthName(month),
                    MoneyHelper.formatMoney(money[0], true),
                    MoneyHelper.formatMoney(money[1], false),
            };
            result.add(temp);
            months.add(month);
        }
        cacheValues(result);
        super.onTaskComplete(values);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!DBHelper.storeMonth) {
            int month = months.get(position);
            startActivity(MonthStatsActivity.getStartingIntent(this, card, year, month));
        } else {
            toast.show();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        toast.cancel();
    }
    
    public static Intent getStartingIntent(Context context, String card, int year) {
        Intent i = new Intent(context, YearStatsActivity.class);
        i.putExtra("card", card);
        i.putExtra("year", year);
        return i;
    }
}
