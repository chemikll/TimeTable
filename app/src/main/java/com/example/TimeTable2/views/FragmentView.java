package com.example.TimeTable2.views;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.TimeTable2.AlarmMessage;
import com.example.TimeTable2.R;
import com.example.TimeTable2.adapters.ListAdapter;
import com.example.TimeTable2.model.HandleTimetableData;
import com.example.TimeTable2.model.TimetableModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentView {

    private Context context;
    private View view;
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private List<TimetableModel> result;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ProgressBar mProgress;
    private TextView emptyText;
    private HandleTimetableData handleTimetableData;

    private String Title, Text, timeFrom, timeTo, datePick;
    private EditText sName, sText;
    private TimePicker timePicker;
    private TextView btnDate;

    public FragmentView(Context context, View view){
        this.context = context;
        this.view = view;
    }

    public void initiateProcess(String day){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance("https://timetable-2e072-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference(user.getUid()).child(day);

        TextView userEmail = view.findViewById(R.id.tv_acc_email);
        userEmail.setText(user.getEmail());

        TextView userPhone = view.findViewById(R.id.tv_phone);
        userPhone.setText(user.getPhoneNumber());

        result = new ArrayList<>();
        mProgress = view.findViewById(R.id.mProgressBar);
        emptyText = view.findViewById(R.id.tv_emptyText);

        iniRecyclerView();
        iniFABs();

        handleTimetableData = new HandleTimetableData(context, mSwipeRefreshLayout, recyclerView, emptyText, mProgress, reference, result, adapter);
        handleTimetableData.updateList();
    }

    public HandleTimetableData getHandleTimetableData() {
        return handleTimetableData;
    }

    public void iniRecyclerView(){
        recyclerView = view.findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new ListAdapter(result);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleTimetableData.refresh();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void iniFABs(){
        SpeedDialView sdv = view.findViewById(R.id.mSpeedDialView);
        SpeedDialActionItem item1 = new SpeedDialActionItem.Builder(R.id.addData_fab, R.drawable.ic_edit)
                .setFabBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                .setLabel("Thêm")
                .setLabelClickable(true)
                .create();
        SpeedDialActionItem item2 = new SpeedDialActionItem.Builder(R.id.refresh_fab, R.drawable.ic_refresh)
                .setFabBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                .setLabel("Làm mới")
                .setLabelClickable(true)
                .create();
        SpeedDialActionItem item3 = new SpeedDialActionItem.Builder(R.id.deleteAll_fab, R.drawable.ic_delete_sweep)
                .setFabBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                .setLabel("Xóa tất cả")
                .setLabelClickable(true)
                .create();


        sdv.addActionItem(item1,0);
        sdv.addActionItem(item2,1);
        sdv.addActionItem(item3,2);


        sdv.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()){
                    case R.id.addData_fab:
                        initiateDialog1();
                        return false;
                    case R.id.refresh_fab:
                        handleTimetableData.refresh();
                        return false;
                    case R.id.deleteAll_fab:
                        iniDeleteAllDialog();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    private void initiateDialog1(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_title);
        sName = dialog.findViewById(R.id.edt_TieuDe);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title = sName.getText().toString();
                if (!TextUtils.isEmpty(Title)){
                    dialog.dismiss();
                    initiateDialog2();
                }
                else {
                    sName.setError("Vui lòng nhập tiêu đề");
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

    private void initiateDialog2(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_text);
        sText = dialog.findViewById(R.id.edt_Noidung);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Text = sText.getText().toString();
                if (!TextUtils.isEmpty(Text)){
                    dialog.dismiss();
                    initiateDialog3();
                }
                else {
                    sText.setError("Vui lòng nhập nội dung");
                }

            }
        });
        ImageButton btnPrevious = dialog.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                initiateDialog1();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void initiateDialog3(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_time_from);
        timePicker = (TimePicker) dialog.findViewById(R.id.tp_time_from);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                timeFrom = getTime(hour,minute);
                dialog.dismiss();
                Calendar calendar = Calendar.getInstance();
                calendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute(),
                            0);
                setAlarm(calendar);
                initiateDialog4();
            }
            private void setAlarm(Calendar c)
            {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(view.getContext(),AlarmMessage.class); //??

                PendingIntent pendingIntent =PendingIntent.getBroadcast(view.getContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_UPDATE_CURRENT

                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, 1);
                }

                alarmManager.setExact(AlarmManager.RTC,c.getTimeInMillis(),pendingIntent);

            }
        });
        ImageButton btnPrevious = dialog.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                initiateDialog2();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void initiateDialog4(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_time_to);
        timePicker = dialog.findViewById(R.id.tp_time_to);
        ImageButton btnNext = dialog.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ho = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                timeTo = getTime(ho,minute);
                dialog.dismiss();
                String time = new StringBuilder().append(timeFrom).append(" đến ").append(timeTo).toString();
                handleTimetableData.addTimetableData(Title,Text,time);
            }
        });
        ImageButton btnPrevious = dialog.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                initiateDialog3();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void iniDeleteAllDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Xóa tất cả?");
        alertDialog.setMessage("Bạn chắc chứ ?");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleTimetableData.deleteAll();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }



    private String getTime(int hr,int min) {
        String timeSet = "";
        if (hr > 12) {
            hr -= 12;
            timeSet = "PM";
        } else if (hr == 0) {
            hr += 12;
            timeSet = "AM";
        } else if (hr == 12){
            timeSet = "PM";
        }else{
            timeSet = "AM";
        }

        String minSet = "";
        if (min < 10){
            minSet = "0" + min ;
        }
        else{
            minSet = String.valueOf(min);
        }

        String aTime = new StringBuilder().append(hr).append(':').append(minSet ).append(" ").append(timeSet).toString();
        return aTime;
    }



}