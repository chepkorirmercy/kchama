package com.sharon.sample.mpesa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPayments extends AppCompatActivity {
    Button restartCycle;
    FirebaseDatabase database;
    DatabaseReference usersRef, awardedUsersRef;
    ProgressDialog loader;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_payments);

        restartCycle = findViewById(R.id.restartCycle);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("user");
        awardedUsersRef = database.getReference("users/awarded");
        loader = new ProgressDialog(this);


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foundUser = false; // checks if there was a member who had not been awarded.
                User user;
                for(DataSnapshot ds : snapshot.getChildren()){
                    user = ds.getValue(User.class);
                    /**
                     * Set member as not awarded and remove them too from the awarded members list
                     */
                    if(user.isAwarded()){
                        foundUser = true;
                        user.setAwarded(false);
                        usersRef.child(user.getId()).updateChildren(user.toMap());
                        // delete the user too in awarded list
                        awardedUsersRef.child(user.getId()).removeValue();
                    }
                }

                loader.dismiss();
                if(!foundUser){
                    Toast.makeText(UserPayments.this, "The cycle is ready to begin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(UserPayments.this, "Award cycle restarted successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        restartCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.setMessage("Restarting Cycle.... Please Wait...");
                loader.show();
                usersRef.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }
}