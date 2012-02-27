package com.marwinxxii.ccardstats.gui;

import java.util.List;

import com.marwinxxii.ccardstats.CardInfo;
import com.marwinxxii.ccardstats.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardItemAdapter extends ArrayAdapter<CardInfo> {

    public CardItemAdapter(Context context, int textViewResourceId,
            List<CardInfo> objects) {
        super(context, textViewResourceId, objects);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardInfo ci = this.getItem(position);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View target = inflater.inflate(R.layout.card_item, parent, false);
        TextView textView=(TextView)target.findViewById(R.id.card_item_name);
        textView.setText(ci.card.getAlias());
        double[] values = new double[] { ci.card.getOutcome(), ci.card.getIncome(),
                ci.monthOutcome, ci.monthIncome, ci.todayOutcome, ci.todayIncome,
                ci.card.getAvailable() };
        
        char[] prefixes = new char[]{'-', '+', '-', '+', '-', '+', ' '};
        
        int[] ids = new int[] { R.id.card_item_total_out, R.id.card_item_total_in,
                R.id.card_item_month_out, R.id.card_item_month_in, R.id.card_item_today_out,
                R.id.card_item_today_in, R.id.card_item_available };
        
        for(int i=0;i<values.length;i++) {
            textView=(TextView)target.findViewById(ids[i]);
            char prefix = prefixes[i];
            if (values[i] <= 0.001) prefix=' ';
            textView.setText(String.format("%c%.2f", prefix, values[i]));
        }
        return target;
    }
    
}
