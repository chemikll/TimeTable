package com.example.TimeTable2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;


import com.example.TimeTable2.R;

import com.example.TimeTable2.adapters.ViewPagerAdapter;
import com.example.TimeTable2.fragments.Deadline;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;



public class DeadlineActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase database;
    private FirebaseUser user;

    private ViewPager viewPager;

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline);

        database = FirebaseDatabase.getInstance("https://timetable-2e072-default-rtdb.asia-southeast1.firebasedatabase.app");
        checkUserState();
        user = mAuth.getCurrentUser();

        initAll();

    }

    private void initAll()
    {
        Toolbar mToolbar = findViewById(R.id.toolbar1);
        mToolbar.setTitle("Deadline");
        setSupportActionBar(mToolbar);
        getSupportActionBar();

        setupView();
    }

    private void setupView()
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPagerDeadline);
        adapter.addFragment(new Deadline(), getResources().getString(R.string.deadline));
        viewPager.setAdapter(adapter);

    }

    private void checkUserState(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}