package com.example.TimeTable2.model;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.TimeTable2.adapters.ListDeadlineAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HandleDeadlineData
{
    private Context context;
    private DatabaseReference reference;
    private List<DeadlineModel> result;
    private ListDeadlineAdapter adapter;
    private TextView emptyText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;


    private int getItemIndex(DeadlineModel user){
        int index = -1;
        for (int i=0; i<result.size(); i++){
            if (result.get(i).key == user.key){
                index = i;
                break;
            }
        }
        return index;
    }

    private void checkIfEmpty(){
        if (result.isEmpty()){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);

        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    public HandleDeadlineData(Context context, SwipeRefreshLayout mSwipeRefreshLayout, RecyclerView recyclerView, TextView emptyText, DatabaseReference reference, List<DeadlineModel> result, ListDeadlineAdapter adapter){
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.emptyText = emptyText;
        this.reference = reference;
        this.result = result;
        this.adapter = adapter;
    }

    public void addDeadlineData(String title,String text,String date){

        String key = reference.push().getKey();
        DeadlineModel deadlineModel = new DeadlineModel(title,text,key,date);
        reference.child(key).setValue(deadlineModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    refresh();
                }
                else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void deleteDeadlineData(int position){

        reference.child(result.get(position).key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
                else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }



    public void updateList(){

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(DeadlineModel.class));
                adapter.notifyDataSetChanged();
                checkIfEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DeadlineModel model = dataSnapshot.getValue(DeadlineModel.class);
                int index = getItemIndex(model);
                result.set(index, model);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DeadlineModel model = dataSnapshot.getValue(DeadlineModel.class);
                int index = getItemIndex(model);
                if (index > -1){
                    result.remove(index);
                    adapter.notifyItemRemoved(index);
                }
                checkIfEmpty();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void refresh(){
        result.clear();
        if (result.isEmpty()){
            updateList();
        }
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void deleteAll(){

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    result.clear();

                    checkIfEmpty();
                }
                else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
