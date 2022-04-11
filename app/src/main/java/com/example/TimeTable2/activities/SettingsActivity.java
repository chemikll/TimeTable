package com.example.TimeTable2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.TimeTable2.R;
import com.example.TimeTable2.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_SEVEN_DAYS_SETTING = "sevendays";
    public static final String KEY_SCHOOL_WEBSITE_SETTING = "schoolwebsite";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}