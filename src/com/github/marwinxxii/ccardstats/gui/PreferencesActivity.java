package com.github.marwinxxii.ccardstats.gui;

import com.marwinxxii.ccardstats.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PreferencesActivity extends PreferenceActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    public static Intent getStartingIntent(Context context) {
        return new Intent(context, PreferencesActivity.class);
    }
}
