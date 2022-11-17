package com.sharon.sample.mpesa;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AwardedListFragment extends Fragment {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<User> users;
    FirebaseDatabase database;
    DatabaseReference awardedUsesRef;
    ProgressDialog loader;

    // default constructor required
    public AwardedListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.awarded_fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set layout for your loaded view
        recyclerView = view.findViewById(R.id.recyclerView);
        loader = new ProgressDialog(getContext());
        database = FirebaseDatabase.getInstance();
        awardedUsesRef = database.getReference("users/awarded");


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        users = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), users);
        recyclerView.setAdapter(myAdapter);
        loader.setMessage("Getting Awarded users... Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        // listen for new awarded users
        awardedUsesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if there are no any awarded users
                if(!snapshot.exists()){
                    loader.dismiss();
                    if(getActivity() != null){
                        Toast.makeText(getActivity(), "No awarded user currently", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                users.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    User newUser = ds.getValue(User.class);
                    users.add(newUser);
                }
                myAdapter.notifyDataSetChanged();
                if(loader.isShowing()) loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Error in database "+error.getMessage(), Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

    }
}

