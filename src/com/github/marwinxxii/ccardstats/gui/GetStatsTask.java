package com.github.marwinxxii.ccardstats.gui;

import java.util.Map;

import com.github.marwinxxii.ccardstats.db.DBHelper;

import android.os.AsyncTask;

public class GetStatsTask extends AsyncTask<Void, Void, Map<Integer, double[]>> {
    
    public enum FilterType {
        MONTH, YEAR;
    }
    
    private SimpleListActivity context;
    private String card;
    private FilterType filter;
    private int year, month;
    
    public GetStatsTask(SimpleListActivity context, String card, FilterType filter,
            int year, int month) {
        this.context = context;
        this.card = card;
        this.filter = filter;
        this.year = year;
        this.month = month;
    }

    @Override
    protected Map<Integer, double[]> doInBackground(Void... params) {
        DBHelper helper = new DBHelper(context);
        Map<Integer, double[]> result;
        switch (filter) {
            case MONTH:
                result=helper.getMonthStats(card, year, month);
                result.put(-1, helper.getMonthTotalStats(card, year, month));
                break;
            default:
                result=helper.getYearStats(card, year);
                result.put(-1, helper.getYearTotalStats(card, year));
                break;
        }
        helper.close();
        return result;
    }
    
    @Override
    protected void onPostExecute(Map<Integer, double[]> result) {
        context.onTaskComplete(result);
    }

}
