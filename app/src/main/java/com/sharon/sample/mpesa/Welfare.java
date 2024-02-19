package com.sharon.sample.mpesa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Welfare extends AppCompatActivity {

    private Button btnsub,btnView;
    private TextInputEditText name;
    private TextInputEditText description;
    private DatabaseReference spinnerRef;
    private  int hour, minute;
    private  String reportDate;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);
        btnsub=findViewById(R.id.btnsub);
        btnView=findViewById(R.id.btnView);
        name=findViewById(R.id.name);
        description=findViewById(R.id.desc);
        calendar = Calendar.getInstance();
        final  int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        spinnerRef =FirebaseDatabase.getInstance().getReference("project");

        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectId = spinnerRef.push().getKey();
                String nameText = name.getText().toString();
                String descText = description.getText().toString();

                if (nameText.isEmpty()||descText.isEmpty()){
                    Toast.makeText(Welfare.this, "Please fill in all spaces", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    reportDate = simpleDateFormat.format(calendar.getTime());

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id", projectId);
                    hashMap.put("name",nameText);
                    hashMap.put("Time",reportDate);
                    hashMap.put("description",descText);

                    spinnerRef.child(projectId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Welfare.this, "Details saved successful", Toast.LENGTH_SHORT).show();
                            name.setText("");
                            description.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Welfare.this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Welfare.this,ProjectsActivity.class);
                intent.putExtra("isAdmin", true);
                startActivity(intent);

            }
        });

    }
}