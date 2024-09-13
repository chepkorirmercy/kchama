package com.sharon.sample.mpesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {

    private TextView loginPageQuestion;
    private Button loginButton;
    private EditText loginEmail;
    SharedPreferences pref;
    private EditText loginPassword;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        //loginPageQuestion = findViewById(R.id.backButton);
        pref = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        loader = new ProgressDialog(this);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        /**
         * Check if there are login details saved in shared preferences and use them instead
         */
        if(!(pref.getString("email", "").isEmpty() && pref.getString("password","").isEmpty())){
            Intent intent = new Intent(UserLogin.this, UserDashboard.class);
            startActivity(intent);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = loginEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email is required");
                }
                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password is required");

                } else {
                    loader.setMessage("Log in in Progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                /*
                                 * Save user login details in shared preferences so that they do not need to login every time
                                 */
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("email",email);
                                editor.putString("password",password);
                                editor.commit();
                                 Intent intent = new Intent(UserLogin.this, UserDashboard.class);
                                  startActivity(intent);
                                 finish();
                            } else {
                                Toast.makeText(UserLogin.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });

                }
            }
        });
    }
}