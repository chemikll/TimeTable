package com.example.TimeTable2.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.TimeTable2.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
