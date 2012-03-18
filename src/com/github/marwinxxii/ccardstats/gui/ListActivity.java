package com.github.marwinxxii.ccardstats.gui;

import com.marwinxxii.ccardstats.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class ListActivity extends Activity {
    
    protected ListView mItemsList;
    protected TextView mListTitle;
    protected TextView mNoItemsText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        mItemsList = (ListView)findViewById(R.id.list_activity_list);
        mListTitle = (TextView)findViewById(R.id.list_activity_title);
        mNoItemsText = (TextView)findViewById(R.id.list_activity_noitems);
    }
    
    public void setTitleText(CharSequence text) {
        mListTitle.setText(text);
        mListTitle.setVisibility(View.VISIBLE);
    }
    
    public void setTitleResId(int resId) {
        mListTitle.setText(resId);
        mListTitle.setVisibility(View.VISIBLE);
    }
    
    public void setNoItemsTextId(int resId) {
        mNoItemsText.setText(resId);
        mNoItemsText.setVisibility(View.VISIBLE);
    }
}
