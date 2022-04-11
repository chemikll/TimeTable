package com.example.TimeTable2.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TimeTable2.R;
import com.example.TimeTable2.model.TimetableModel;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>
{

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private List<TimetableModel> list;

    public ListAdapter(List<TimetableModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder listViewHolder, int i) {
        TimetableModel timetable = list.get(i);
        listViewHolder.title.setText(timetable.title);
        listViewHolder.text.setText(timetable.text);
        listViewHolder.time.setText(timetable.time);
        listViewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(listViewHolder.getAdapterPosition(), 0, 0, "Xóa thẻ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ListViewHolder extends RecyclerView.ViewHolder
    {
        TextView title,text,time,date;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            text = itemView.findViewById(R.id.tv_text);
            time = itemView.findViewById(R.id.tv_time);
        }
    }
}