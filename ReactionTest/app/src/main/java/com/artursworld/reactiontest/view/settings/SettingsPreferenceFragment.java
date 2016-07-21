package com.artursworld.reactiontest.view.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.artursworld.reactiontest.R;

public class SettingsPreferenceFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference_screen);
    }
}
