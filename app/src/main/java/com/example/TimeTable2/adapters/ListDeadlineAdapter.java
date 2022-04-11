package com.example.TimeTable2.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TimeTable2.R;
import com.example.TimeTable2.model.DeadlineModel;


import java.util.List;

public class ListDeadlineAdapter extends RecyclerView.Adapter<ListDeadlineAdapter.ListViewHolder1>
{
    private List<DeadlineModel> list;

    public ListDeadlineAdapter(List<DeadlineModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder1 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListViewHolder1(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_deadline_view, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder1 listViewHolder, int i) {
        DeadlineModel deadline = list.get(i);
        listViewHolder.title.setText(deadline.title);
        listViewHolder.text.setText(deadline.text);
        listViewHolder.date.setText(deadline.date);
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



    class ListViewHolder1 extends RecyclerView.ViewHolder
    {
        TextView title,text,date;
        public ListViewHolder1(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title1);
            text = itemView.findViewById(R.id.tv_text1);
            date = itemView.findViewById(R.id.tv_date1);
        }

    }
}
