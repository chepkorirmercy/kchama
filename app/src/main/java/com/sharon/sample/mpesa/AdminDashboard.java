package com.sharon.sample.mpesa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminDashboard extends AppCompatActivity {


    private CardView Reg;
    private CardView wels;
    private CardView Send;
    private CardView userPayments;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Reg=findViewById(R.id.reg);
       Send=findViewById(R.id.Send);
        wels=findViewById(R.id.wels);
        userPayments = findViewById(R.id.userPayments);

        Reg.setOnClickListener(View->{
            Toast.makeText(AdminDashboard.this, "Registration", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashboard.this,ViewInfo.class);
            startActivity(intent);
        });
        Send.setOnClickListener(View->{
            Toast.makeText(AdminDashboard.this, "SMS", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashboard.this, SendSMS.class);
            startActivity(intent);
        });
        wels.setOnClickListener(View->{
            Toast.makeText(AdminDashboard.this, " Welfares", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashboard.this, Welfare.class);
            startActivity(intent);
        });

        userPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, UserPayments.class);
                startActivity(intent);
            }
        });
    }
}