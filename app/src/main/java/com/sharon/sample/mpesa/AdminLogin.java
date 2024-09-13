package com.sharon.sample.mpesa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {

    private Button loginButton;
    private EditText loginEmail;
    private EditText loginPassword;
    private ProgressDialog loader;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        loader = new ProgressDialog(this);

        loginButton.setOnClickListener(View->{
            final String email = loginEmail.getText().toString().trim();
            final String password = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email is required");
            }
            if (TextUtils.isEmpty(password)) {
                loginPassword.setError("Password is required");
            }else {
                loader.setMessage("Log in in Progress");
                loader.setCanceledOnTouchOutside(false);
                loader.show();
                if(loginEmail.getText().toString().equals("admin@gmail.com") && loginPassword.getText().toString().equals("admin")){
                    Toast.makeText(AdminLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminLogin.this,AdminDashboard.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(AdminLogin.this, "Login Not Successful", Toast.LENGTH_SHORT).show();
                }
                loader.dismiss();
            }
        });
    }
}