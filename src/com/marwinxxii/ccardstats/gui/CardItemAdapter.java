package com.marwinxxii.ccardstats.gui;

import java.util.List;

import com.marwinxxii.ccardstats.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardItemAdapter extends ArrayAdapter<String[]> {

    private int[] ids;

    public CardItemAdapter(Context context, int textViewResourceId, int[] ids,
            List<String[]> values) {
        super(context, textViewResourceId, values);
        this.ids = ids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View target = inflater.inflate(R.layout.card_item, parent, false);
        String[] values = getItem(position);
        for (int i = 0; i < values.length; i++) {
            TextView textView = (TextView) target.findViewById(ids[i]);
            textView.setText(values[i]);
        }
        return target;
    }

}
