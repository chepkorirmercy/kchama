package com.sharon.sample.mpesa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

public class UserDashboard extends AppCompatActivity {

    private CardView pay, prog, meriGo;
    private CardView list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        pay = findViewById(R.id.pay);
        list = findViewById(R.id.list);
        prog = findViewById(R.id.prog);
        meriGo = findViewById(R.id.M);

        pay.setOnClickListener(View -> {
            Intent intent = new Intent(UserDashboard.this, MpesaActivity.class);
            startActivity(intent);
        });
        prog.setOnClickListener(View -> {
            Intent intent = new Intent(UserDashboard.this, ProjectProject.class);
            startActivity(intent);
        });
        list.setOnClickListener(View -> {
            Intent intent = new Intent(UserDashboard.this, ViewInfo.class);
            startActivity(intent);
        });

        meriGo.setOnClickListener(view -> {
            Intent intent = new Intent(UserDashboard.this, MeriGo.class);
            startActivity(intent);
        });
    }
}