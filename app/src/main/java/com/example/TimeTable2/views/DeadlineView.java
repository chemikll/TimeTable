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
import com.example.TimeTable2.adapters.ListDeadlineAdapter;
import com.example.TimeTable2.model.DeadlineModel;
import com.example.TimeTable2.model.HandleDeadlineData;
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

public class DeadlineView
{
        private Context context;
        private View view;
        private RecyclerView recyclerView;
        private ListDeadlineAdapter adapter;
        private List<DeadlineModel> result;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private FirebaseDatabase database;
        private DatabaseReference reference;

        private TextView emptyText;
        private HandleDeadlineData handleDeadlineData;

        private String Title, Text,datePick;
        private EditText sName, sText;

        private TextView btnDate;

        public DeadlineView(Context context, View view){
            this.context = context;
            this.view = view;
        }

        public void initiateProcess(String card){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            database = FirebaseDatabase.getInstance("https://timetable-2e072-default-rtdb.asia-southeast1.firebasedatabase.app");
            reference = database.getReference(user.getUid()).child(card);


            result = new ArrayList<>();
            emptyText = view.findViewById(R.id.tv_emptyText1);

            iniRecyclerView();
            iniFABs();

            handleDeadlineData = new HandleDeadlineData(context, mSwipeRefreshLayout, recyclerView, emptyText, reference, result, adapter);
            handleDeadlineData.updateList();
        }

        public HandleDeadlineData getHandleDeadlineData() {
            return handleDeadlineData;
        }

        public void iniRecyclerView(){
            recyclerView = view.findViewById(R.id.mRecyclerView1);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            adapter = new ListDeadlineAdapter(result);
            recyclerView.setAdapter(adapter);

            mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout1);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    handleDeadlineData.refresh();
                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        private void iniFABs(){
            SpeedDialView sdv = view.findViewById(R.id.mSpeedDialView1);
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
                            handleDeadlineData.refresh();
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
            dialog.setContentView(R.layout.dialog_deadline_title);
            sName = dialog.findViewById(R.id.edt_TieuDe1);
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
            dialog.setContentView(R.layout.dialog_deadline_text);
            sText = dialog.findViewById(R.id.edt_Noidung1);
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
            dialog.setContentView(R.layout.dialog_date);
            btnDate = dialog.findViewById(R.id.btnpickdate);
            ImageButton btnNext = dialog.findViewById(R.id.btn_next);

            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year,month,dayOfMonth);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            btnDate.setText(simpleDateFormat.format(calendar.getTime()));
                            datePick = btnDate.getText().toString();
                            setAlarm(calendar);
                        }
                    }, mYear, mMonth, mdayofMonth);
                    datePickerDialog.setTitle("Chọn ngày");
                    datePickerDialog.show();
                }
                private void setAlarm(Calendar c)
                {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(view.getContext(),AlarmMessage.class); //??

                    PendingIntent pendingIntent =PendingIntent.getBroadcast(view.getContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_UPDATE_CURRENT


                    alarmManager.setExact(AlarmManager.RTC,c.getTimeInMillis(),pendingIntent);

                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    handleDeadlineData.addDeadlineData(Title,Text,datePick);
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


        private void iniDeleteAllDialog(){
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Xóa tất cả?");
            alertDialog.setMessage("Bạn chắc chứ ?");
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handleDeadlineData.deleteAll();
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

    }
