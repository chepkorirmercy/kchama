package com.sharon.sample.mpesa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharon.mpesa.stkpush.Mode;
import com.sharon.mpesa.stkpush.api.response.STKPushResponse;
import com.sharon.mpesa.stkpush.interfaces.STKListener;
import com.sharon.mpesa.stkpush.interfaces.TokenListener;
import com.sharon.mpesa.stkpush.model.Mpesa;
import com.sharon.mpesa.stkpush.model.STKPush;
import com.sharon.mpesa.stkpush.model.Token;
import com.sharon.mpesa.stkpush.model.Transaction;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MpesaActivity extends AppCompatActivity implements TokenListener {

    public static final String TAG = MpesaActivity.class.getSimpleName();

    private EditText phoneET, amountET;
    private SweetAlertDialog sweetAlertDialog;
    private Mpesa mpesa;

    private String phone_number;
    private String amount;
    private FirebaseDatabase database;
    private DatabaseReference transactionStatusRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);


        phoneET = findViewById(R.id.phoneET);
        amountET = findViewById(R.id.amountET);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Connecting to Safaricom");
        sweetAlertDialog.setContentText("Please wait...");
        sweetAlertDialog.setCancelable(true);
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        transactionStatusRef = database.getReference("transaction_status");
    }

    public void startMpesa(View view) {

        phone_number = phoneET.getText().toString();
        amount = amountET.getText().toString();

        if (phone_number.isEmpty()) {
            Toast.makeText(MpesaActivity.this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.isEmpty()) {
            Toast.makeText(MpesaActivity.this, "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone_number.isEmpty() && !amount.isEmpty()) {
            try {
                sweetAlertDialog.show();
                mpesa.getToken(this);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UnsupportedEncodingException: " + e.getLocalizedMessage());
            }
        } else {
            Toast.makeText(MpesaActivity.this, "Please make sure that phone number and amount is not empty ", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTokenSuccess(Token token) {
        STKPush stkPush = new STKPush();
        stkPush.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
        stkPush.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
        stkPush.setTimestamp(STKPush.getTimestamp());
        stkPush.setTransactionType(Transaction.CUSTOMER_PAY_BILL_ONLINE);
        stkPush.setAmount(amount);
        stkPush.setPartyA(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setPartyB(Config.PARTYB);
        stkPush.setPhoneNumber(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setCallBackURL(Config.CALLBACKURL);
        stkPush.setAccountReference("KChama");
        stkPush.setTransactionDesc("some description");
        mpesa.startStkPush(token, stkPush, new STKListener() {
            @Override

            public void onResponse(STKPushResponse stkPushResponse) {
                Log.e(TAG, "onResponse: " + stkPushResponse.toJson(stkPushResponse));

                // Dynamically obtain the M-Pesa code
                String mpesaCode = getMpesaCode(stkPushResponse);

                // Log the M-Pesa code before storing it
                Log.d(TAG, "M-Pesa Code before storing: " + mpesaCode);

                String message = "Please enter your pin to complete transaction";


                // Log amount and phone_number for debugging
                Log.d(TAG, "Amount: " + amount);
                Log.d(TAG, "Phone Number: " + phone_number);


                // Store the transaction status in Firebase Database
                storeTransactionStatus("success", message, stkPushResponse, amount, phone_number, mpesaCode);


                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Transaction started");
                sweetAlertDialog.setContentText(message);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "stk onError: " + throwable.getMessage());

                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Error");
                sweetAlertDialog.setContentText(throwable.getMessage());
            }
        });
    }

    @Override
    public void OnTokenError(Throwable throwable) {
        Log.e(TAG, "stk onError: " + throwable.getMessage());

        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error");
        sweetAlertDialog.setContentText(throwable.getMessage());

    }

    // Method to dynamically obtain the M-Pesa code from the STKPushResponse
    private String getMpesaCode(STKPushResponse stkPushResponse) {
        // Check if the response contains the CheckoutRequestID or any other unique identifier
        if (stkPushResponse != null && stkPushResponse.getCheckoutRequestID() != null) {
            return stkPushResponse.getCheckoutRequestID();
        } else {
            // Handle the case when the unique identifier is not available
            return "unknownMpesaCode";
        }
    }


    // Method to store transaction status in Firebase Database
    private void storeTransactionStatus(String status, String message, STKPushResponse stkPushResponse,String amount,String phone_number,String sender_Name) {
        Log.d(TAG, "Inside storeTransactionStatus method");

        String user = getUserId(); // Dynamically obtain the user ID

        if (user == null || user.isEmpty()) {
            // Handle the case when the user ID is not available
            Log.e(TAG, "User ID not available");
            return;
        }
        String mpesaCode = getMpesaCode(stkPushResponse); // Dynamically obtain the M-Pesa code

        // Build a unique key for the transaction status
        String key = user;

        // Dynamically obtain the sender's name from the database based on the user ID
        getSenderNameFromDatabase(new SenderNameCallback() {
            @Override
            public void onSenderNameReceived(String senderName) {
                // Store the status, message, mpesacode, and sender name in Firebase Database
                DatabaseReference transactionRef = transactionStatusRef.child(key);
                transactionRef.child("status").setValue(status);
                transactionRef.child("message").setValue(message);
                transactionRef.child("mpesacode").setValue(mpesaCode);
                transactionRef.child("amount").setValue(amount);
                transactionRef.child("phone_number").setValue(phone_number);
                transactionRef.child("sender_name").setValue(sender_Name);

                // Store the sender's name in the database
                setSenderNameInDatabase(senderName);
            }

            @Override
            public void onError(String error) {
                // Handle the error, for example, show an error message to the user
                Log.e(TAG, "Error obtaining sender's name: " + error);
            }
        });

    }
    private interface SenderNameCallback {
        void onSenderNameReceived(String senderName);
        void onError(String error);
    }
    private void getSenderNameFromDatabase(SenderNameCallback callback) {
        DatabaseReference userRef = database.getReference("user").child(getUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String senderName = dataSnapshot.child("name").getValue(String.class);
                    Log.d(TAG, "Sender's Name obtained from database: " + senderName);
                    callback.onSenderNameReceived(senderName);


                } else {
                    callback.onError("User profile not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setSenderNameInDatabase(String senderName) {
        DatabaseReference userRef = database.getReference("user").child(getUserId());
        userRef.child("name").setValue(senderName);

    }

    // Method to dynamically obtain the user ID (replace this with your actual logic)
    private String getUserId()  {
        // Example: Retrieve user ID from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case when the user is not authenticated
            return null;
        }
    }



}
