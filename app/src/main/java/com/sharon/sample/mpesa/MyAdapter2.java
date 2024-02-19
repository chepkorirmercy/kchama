package com.sharon.sample.mpesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder>{

    private final ArrayList<TaskItem> items;
    private final Context context;
    private boolean isAdmin;

    public MyAdapter2(ArrayList<TaskItem> items, Context context,boolean isAdmin) {
        this.items = items;
        this.context = context;
        this.isAdmin = isAdmin;
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
        //set visibility of delete button based on admin status
        holder.btndelete.setVisibility(View.GONE);
        Toast.makeText(context, isAdmin? "Admin" : "User", Toast.LENGTH_SHORT).show();
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference dbRef= database.getReference("project");

                dbRef.child("").removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference dbRef) {
                        if (error != null) {
                            System.out.println("Data not deleted" + error.getMessage());
                        }else {
                            System.out.println("Data deleted successfully");
                        }

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private  final TextView description,name;
        private final Button btndelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            description=itemView.findViewById(R.id.desc);
            name=itemView.findViewById(R.id.names);
            btndelete=itemView.findViewById(R.id.btndelete);

        }
    }
}

