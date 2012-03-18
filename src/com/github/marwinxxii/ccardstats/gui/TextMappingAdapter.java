package com.github.marwinxxii.ccardstats.gui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextMappingAdapter extends ArrayAdapter<String[]> {

    private int[] ids;
    private int mLayoutId;

    public TextMappingAdapter(Context context, int layoutId, int[] ids,
            List<String[]> values) {
        super(context, layoutId, values);
        this.ids = ids;
        this.mLayoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View target = inflater.inflate(mLayoutId, parent, false);
        String[] values = getItem(position);
        for (int i = 0; i < ids.length; i++) {
            TextView textView = (TextView) target.findViewById(ids[i]);
            textView.setText(values[i]);
        }
        return target;
    }

}
