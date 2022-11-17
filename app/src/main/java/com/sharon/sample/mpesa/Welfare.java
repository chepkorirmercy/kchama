package com.sharon.sample.mpesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Welfare extends AppCompatActivity {

    private Button btnsub;
    private TextInputEditText name;
    private TextInputEditText description;
    private DatabaseReference SpinnerRef;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);
        btnsub=findViewById(R.id.btnsub);
        name=findViewById(R.id.name);
       description=findViewById(R.id.desc);
        SpinnerRef=FirebaseDatabase.getInstance().getReference("project");
        btnsub.setOnClickListener(View->{
            String values=name.getText().toString();
            String Values=description.getText().toString();
            String Key=SpinnerRef.push().getKey();
            SpinnerRef.child("description").setValue(values);
            name.setText("");
            Toast.makeText(Welfare.this, "Saved", Toast.LENGTH_SHORT).show();
        });
    }
}