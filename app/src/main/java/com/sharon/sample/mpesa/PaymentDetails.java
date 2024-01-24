package com.sharon.sample.mpesa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentDetails extends AppCompatActivity {
    private EditText code;
    private EditText amount;
    private EditText purpose;
    private Button submit;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        //initialize Firebase
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("payments");
        //initialize UI components
        code=findViewById(R.id.code);
        amount=findViewById(R.id.amount);
        purpose=findViewById(R.id.purpose);
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentcode = code.getText().toString().trim();
                String paymentamount = amount.getText().toString().trim();
                String paymentpurpose = purpose.getText().toString().trim();

                if (!paymentcode.isEmpty()&& !paymentamount.isEmpty()&& !paymentpurpose.isEmpty()) {
                    //add a new payment with code,amount and purpose to the list
                    Payment payment = new Payment(paymentcode, paymentamount, paymentpurpose);
                    // Clear the input fields
                    code.getText().clear();
                    amount.getText().clear();
                    purpose.getText().clear();
                    // Push the payment to the database
                    databaseReference.push().setValue(payment);
                    // Notify the user that the project has been added
                    Toast.makeText(PaymentDetails.this, "payment details submitted ", Toast.LENGTH_SHORT).show();
                }else {
                    // Notify the user if the project name is empty
                    Toast.makeText(PaymentDetails.this, "Please enter payment details", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}