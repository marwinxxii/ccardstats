package com.marwinxxii.ccardstats.gui;

import java.util.List;

import com.marwinxxii.ccardstats.Card;
import com.marwinxxii.sberbank.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardItemAdapter extends ArrayAdapter<Card> {

    public CardItemAdapter(Context context, int textViewResourceId,
            List<Card> objects) {
        super(context, textViewResourceId, objects);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Card c = this.getItem(position);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout target = (RelativeLayout)inflater.inflate(
                R.layout.card_item, parent, false);
        TextView textView=(TextView)target.findViewById(R.id.card_item_name);
        textView.setText(c.getAlias());
        textView = (TextView)target.findViewById(R.id.card_item_available);
        textView.setText(String.format("%.2f", c.getAvailable()));
        textView = (TextView)target.findViewById(R.id.card_item_income);
        String temp=context.getString(R.string.card_item_income, c.getIncome());
        textView.setText(temp);
        textView = (TextView)target.findViewById(R.id.card_item_outcome);
        temp=context.getString(R.string.card_item_outcome, c.getOutcome());
        textView.setText(temp);
        return target;
    }
    
}
