package com.sharon.sample.mpesa;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MeriGoControl extends AppCompatActivity {
    Button restartCycle;
    FirebaseDatabase database;
    DatabaseReference usersRef, awardedUsersRef, cycleRef;
    ProgressDialog loader;
    ValueEventListener valueEventListener;
    DatePicker datePicker;
    Button btnSelectTime;
    Button setSpinDate;
    TimePickerDialog picker;
    TextView tvSelectedTime;
    String spinDate; // next spin date and time
    int year, month, date, hour, minute;
    boolean isTimeSelected =  false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merigo_control);

        restartCycle = findViewById(R.id.restartCycle);
        datePicker = findViewById(R.id.datePicker);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        setSpinDate = findViewById(R.id.setSpinDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("user");
        awardedUsersRef = database.getReference("users/awarded");
        cycleRef = database.getReference("/cycles");
        loader = new ProgressDialog(this);

        // Get the current time
        final Calendar currentTime = Calendar.getInstance();
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);

        // open time picker on select time
        btnSelectTime.setOnClickListener(v -> {
            picker.show();
        });

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
                    Toast.makeText(MeriGoControl.this, "The cycle is ready to begin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MeriGoControl.this, "Award cycle restarted successfully.", Toast.LENGTH_SHORT).show();
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

        picker = new TimePickerDialog(
                this,
                (view, hourOfDay, sMinute) -> {
                    // Handle the time set by the user
                    isTimeSelected = true;
                    hour = hourOfDay;
                    minute = sMinute;

                    btnSelectTime.setText("Time Selected!");
                    tvSelectedTime.setText("Selected time: "+hourOfDay+": "+minute);

                },
                hour,
                minute,
                true // 24-hour format
        );

        /**
         * Set next spin date
         */
        setSpinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if has selected time
                if(!isTimeSelected){
                    Toast.makeText(MeriGoControl.this, "Please select time first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // date displayed time
//                picker.updateTime(hour, minute);
                Log.d("Time", hour+":"+minute);


                // set date, month and year
                date = datePicker.getDayOfMonth();
                month = datePicker.getMonth()+1;
                year  = datePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, month, date, hour, minute);
                // format date
                spinDate = date+"/"+month+"/"+year+" "+hour+":"+minute;

                // set spin date in firebase
                cycleRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cycleRef.setValue(spinDate);
                        Toast.makeText(MeriGoControl.this, "Spin date set successfully. ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // TODO handle
                    }
                });
            }
        });
    }
}