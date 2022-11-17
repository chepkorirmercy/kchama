package com.sharon.sample.mpesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder>{

    private final ArrayList<TaskItem> items;
    private final Context context;

    public MyAdapter2(ArrayList<TaskItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items,null));
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.MyViewHolder holder, int position) {
        TaskItem taskItem= items.get(position);
        holder.description.setText(taskItem.getDescription());
       // holder.name.setText(taskItem.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private  final TextView description,name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            description=itemView.findViewById(R.id.desc);
            name=itemView.findViewById(R.id.names);

        }
    }
}

