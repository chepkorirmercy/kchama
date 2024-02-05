package com.sharon.sample.mpesa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Dashboard extends AppCompatActivity {

    private CardView a;
    private CardView u;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        a=findViewById(R.id.as);
        u=findViewById(R.id.u);
        a.setOnClickListener(View->{
            Intent intent = new Intent(Dashboard.this, VerifyPayments.class);
            startActivity(intent);
            finish();
        });
        u.setOnClickListener(View->{
           Intent intent = new Intent(Dashboard.this, UserLogin.class);
           startActivity(intent);
           finish();
        });
    }
}