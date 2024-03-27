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
import com.sharon.sample.mpesa.models.PaymentItem;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MpesaActivity extends AppCompatActivity implements TokenListener {

    public static final String TAG = MpesaActivity.class.getSimpleName();

    private EditText phoneET, amountET;
    private Mpesa mpesa;
    SweetAlertDialog  sweetAlertDialog;
    private String phone_number;
    private String amount;
    private FirebaseDatabase database;
    private DatabaseReference paymentsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);

        phoneET = findViewById(R.id.phoneET);
        amountET = findViewById(R.id.amountET);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        paymentsRef = database.getReference("payments");
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

        try {
            sweetAlertDialog.setTitleText("Connecting to Safaricom")
                    .setContentText("Please wait...")
                    .show();

            mpesa.getToken(this);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException: " + e.getLocalizedMessage());
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

                // Store the transaction status in Firebase Database
                storeTransactionStatus(stkPushResponse, amount, phone_number);

                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Transaction started");
                sweetAlertDialog.setContentText("Please enter your pin to complete transaction");
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


    // Method to store transaction status in Firebase Database
    private void storeTransactionStatus(STKPushResponse stkPushResponse, String amount, String phoneNumber) {
        // Dynamically obtain the sender's name from the database based on the user ID
        getSenderNameFromDatabase(senderName -> {
            String paymentId = paymentsRef.push().getKey().toString();

            PaymentItem paymentItem = new PaymentItem();
            paymentItem.setId(paymentId);
            paymentItem.setAmount(amount);
            paymentItem.setPhoneNumber(phoneNumber);
            paymentItem.setSenderId(getUserId());
            paymentItem.setCheckoutRequestId(stkPushResponse.getCheckoutRequestID());
            paymentItem.setSenderName(senderName);

             paymentsRef.child(paymentId).setValue(paymentItem).addOnSuccessListener(unused ->{
                 Log.d("SaveTransactions", "Payment saved successfully");
             }).addOnFailureListener(e -> {
                 Log.d("SaveTransaction", "Unable to save payment details");
             });
        });

    }


    private interface SenderNameCallback {
        void onSenderNameReceived(String senderName);
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB error", "Database error: " + databaseError.getMessage());
            }
        });
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
