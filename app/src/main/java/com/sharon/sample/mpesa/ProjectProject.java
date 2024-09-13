package com.sharon.sample.mpesa;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectProject extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private ArrayList<TaskItem> taskItemslist =new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_project);
        recyclerView=findViewById(R.id.userlists);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectProject.this));

        // Check if the current user is an admin or not
        // For simplicity, let's assume isAdmin is determined elsewhere in your code
        boolean isAdmin = checkIfUserIsAdmin();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskItemslist.clear();
                for (DataSnapshot projects : snapshot.child("projects").getChildren()){
                    final String getDescription = projects.child(database.getKey()).getValue(String.class);
                    //final String getName=projects.child(database.getKey()).getValue(String.class);

                    TaskItem taskItem =new TaskItem(getDescription, "test name","today");
                    //TaskItem taskItem=new TaskItem(getName);
                    taskItemslist.add(taskItem);
                }
                //boolean isAdminPage = false;
                recyclerView.setAdapter(new MyAdapter2(taskItemslist,
                        ProjectProject.this,
                        isAdmin));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean checkIfUserIsAdmin() {
        return true;
    }
}