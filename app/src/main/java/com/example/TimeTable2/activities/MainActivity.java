package com.example.TimeTable2.activities;

import static com.example.TimeTable2.BrowserUtils.openUrlInChromeCustomTab;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;


import android.app.AlarmManager;
import android.app.Dialog;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.androidadvance.topsnackbar.TSnackbar;
import com.example.TimeTable2.AlarmMessage;
import com.example.TimeTable2.R;
import com.example.TimeTable2.adapters.ViewPagerAdapter;
import com.example.TimeTable2.fragments.Friday;
import com.example.TimeTable2.fragments.Monday;
import com.example.TimeTable2.fragments.Saturday;
import com.example.TimeTable2.fragments.Sunday;
import com.example.TimeTable2.fragments.Thursday;
import com.example.TimeTable2.fragments.Tuesday;
import com.example.TimeTable2.fragments.Wednesday;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ViewPager viewPager;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private RelativeLayout relativeLayout, relativeLayoutMain;
    private TextView loadingMsg;

    private TextInputEditText reEnterPassword, edtNewPassword, edtReNewPassword, edtOldPassword;
    private TextInputLayout til_Pass1, til_Pass2, til_Pass3;
    private String rePass, newPass, reNewPass, oldPass;


    private boolean switchSevenDays;


    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav);

        database = FirebaseDatabase.getInstance("https://timetable-2e072-default-rtdb.asia-southeast1.firebasedatabase.app");
        checkUserState();
        user = mAuth.getCurrentUser();


        relativeLayout = findViewById(R.id.rl_delete_view);
        relativeLayoutMain = findViewById(R.id.relativeLayoutMain);
        loadingMsg = findViewById(R.id.label);



        initAll();

    }

    private void initAll()
    {
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setupViewPager();
        setupSevenDaysPref();


        if(switchSevenDays) changeFragments(true);
    }

    private void setupViewPager(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        adapter.addFragment(new Monday(), getResources().getString(R.string.monday));
        adapter.addFragment(new Tuesday(), getResources().getString(R.string.tuesday));
        adapter.addFragment(new Wednesday(), getResources().getString(R.string.wednesday));
        adapter.addFragment(new Thursday(), getResources().getString(R.string.thursday));
        adapter.addFragment(new Friday(), getResources().getString(R.string.friday));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(day == 1 ? 6 : day-2, true);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void changeFragments(boolean isChecked)
    {
        if(isChecked) {
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            adapter.addFragment(new Saturday(), getResources().getString(R.string.saturday));
            adapter.addFragment(new Sunday(), getResources().getString(R.string.sunday));
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(day == 1 ? 6 : day-2, true);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            if(adapter.getFragmentList().size() > 5) {
                adapter.removeFragment(new Saturday(), 5);
                adapter.removeFragment(new Sunday(), 5);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupSevenDaysPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switchSevenDays = sharedPref.getBoolean(SettingsActivity.KEY_SEVEN_DAYS_SETTING, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
            return true;
        }

        else if (item.getItemId() == R.id.deleteAccount){
            iniDeleteAccDialog();
            return true;
        }
        else if (item.getItemId() == R.id.resetPassword){
            iniResetPassDialog();
            return true;
        }
        else if (item.getItemId() == R.id.cancelAlarm){
            cancelAlarm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmMessage.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmMessage.onStop();
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final NavigationView navigationView = findViewById(R.id.nav_view);
        switch (item.getItemId()) {
            case R.id.schoolwebsitemenu:
                String schoolWebsite = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_SCHOOL_WEBSITE_SETTING, null);
                if (!TextUtils.isEmpty(schoolWebsite)) {
                    openUrlInChromeCustomTab(getApplicationContext(), schoolWebsite);
                } else {
                    Snackbar.make(navigationView, "Hãy chọn địa chỉ website trường bạn", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            case R.id.deadline:
                Intent deadline = new Intent(MainActivity.this, DeadlineActivity.class);
                startActivity(deadline);
                return true;
            case R.id.refresh:
                recreate();
                return true;
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
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

    private void iniDeleteAccDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_reauth_password);
        reEnterPassword = dialog.findViewById(R.id.edt_reEnter_password);
        til_Pass1 = dialog.findViewById(R.id.til_password);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rePass = reEnterPassword.getText().toString();
                if (!TextUtils.isEmpty(rePass)){
                    deleteAccount(rePass);
                    dialog.dismiss();
                }
                else {
                    til_Pass1.setError("Xin hãy nhập mật khẩu của bạn");
                }
            }
        });
        ImageButton btnPrevious = dialog.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rePass = "";
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void deleteAccount(String password){
        loadingMsg.setText("Đang xóa...");
        relativeLayout.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                relativeLayout.setVisibility(View.GONE);
                                showSnackBar(task.getException().getMessage(), android.R.color.holo_red_dark);
                            }
                            else {
                                database.getReference(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        relativeLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    relativeLayout.setVisibility(View.GONE);
                    showSnackBar(task.getException().getMessage(), android.R.color.holo_red_dark);
                }
            }
        });

    }

    private void iniResetPassDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_update_password);
        edtOldPassword = dialog.findViewById(R.id.edt_old_password);
        edtNewPassword = dialog.findViewById(R.id.edt_new_password);
        edtReNewPassword = dialog.findViewById(R.id.edt_renew_password);
        til_Pass1 = dialog.findViewById(R.id.til_password1);
        til_Pass2 = dialog.findViewById(R.id.til_password2);
        til_Pass3 = dialog.findViewById(R.id.til_password3);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = edtOldPassword.getText().toString();
                newPass = edtNewPassword.getText().toString();
                reNewPass = edtReNewPassword.getText().toString();
                if (!TextUtils.isEmpty(newPass) && !TextUtils.isEmpty(reNewPass) && !TextUtils.isEmpty(oldPass)){
                    if (newPass.equals(reNewPass)){
                        dialog.dismiss();
                        resetPassword(reNewPass, oldPass);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (TextUtils.isEmpty(newPass)){
                        til_Pass2.setError(" ");
                    }
                    if (TextUtils.isEmpty(oldPass)){
                        til_Pass1.setError(" ");
                    }
                    if (TextUtils.isEmpty(reNewPass)){
                        til_Pass3.setError(" ");
                    }
                }
            }
        });
        ImageButton btnPrevious = dialog.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void resetPassword(final String passwordNew, String passwordOld){
        loadingMsg.setText("Đang cập nhật...");
        relativeLayout.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordOld);
        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mAuth.signOut();
                                relativeLayout.setVisibility(View.GONE);
                            }
                            else {
                                showSnackBar(task.getException().getMessage(), android.R.color.holo_red_dark);
                                relativeLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    relativeLayout.setVisibility(View.GONE);
                    showSnackBar(task.getException().getMessage(), android.R.color.holo_red_dark);
                }
            }
        });
    }

    private void showSnackBar(String msg, int color){
        TSnackbar snackbar = TSnackbar.make(relativeLayoutMain, msg, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
        TextView textView = snackView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(MainActivity.this, color));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);

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