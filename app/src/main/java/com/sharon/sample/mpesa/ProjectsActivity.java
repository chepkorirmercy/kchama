package com.sharon.sample.mpesa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dbRef;
    boolean isAdmin = false;

    FirebaseRecyclerOptions<TaskItem> options;
    FirebaseRecyclerAdapter<TaskItem,ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        Intent intent = getIntent();
        isAdmin = intent.getBooleanExtra("isAdmin", false);

        Toast.makeText(this, "Viewing projects as "+(isAdmin ? "Admin" : "User"), Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbRef = FirebaseDatabase.getInstance().getReference().child("project");


        options = new FirebaseRecyclerOptions.Builder<TaskItem>().setQuery(dbRef,TaskItem.class).build();

        adapter = new FirebaseRecyclerAdapter<TaskItem, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull TaskItem item) {
                holder.description.setText(item.getDescription());
                holder.name.setText(item.getName());
                holder.date.setText(item.getTime());
                //set visibility of delete button based on admin status
                holder.btndelete.setVisibility(isAdmin ? View.VISIBLE:View.GONE);
                holder.btndelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference dbRef= database.getReference("project");

                        dbRef.child(item.getId()).removeValue().addOnSuccessListener(success -> {
                            Toast.makeText(ProjectsActivity.this, "Successfully deleted the project", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(error -> {
                            Toast.makeText(ProjectsActivity.this, "Unable to delete the project item", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ProjectsActivity.this).inflate(R.layout.items,parent,false);

                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}