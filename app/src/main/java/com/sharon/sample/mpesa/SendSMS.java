package com.sharon.sample.mpesa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SendSMS extends AppCompatActivity {
    EditText etPhone,etMessage;
    Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        etPhone = findViewById(R.id.et_phone);
        etMessage = findViewById(R.id.et_message);

        btSend = findViewById(R.id.bt_send);
       btSend.setOnClickListener(v -> {
            //checking condition
            if(ContextCompat.checkSelfPermission(SendSMS.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                //WHEN permission is granted
                //create method
                sendMessage();
            }else{
                //when permission is not granted
                //request permission
                ActivityCompat.requestPermissions(SendSMS.this
                        ,new String[]{Manifest.permission.SEND_SMS}
                        ,100);
            }

        });

    }
    private void sendMessage() {
        //Get Values from edit Text
        String sPhone = etPhone.getText().toString().trim();
        String sMessage = etMessage.getText().toString().trim();
        //check condition

        if(!sPhone.equals("") && !sMessage.equals("")) {
            //check if both phone number and message are empty
            if (sPhone.isEmpty() && !sMessage.isEmpty()) {
                etPhone.setError("Enter Valid Phone Number");
                etPhone.requestFocus();
                return;
            }
            //when both edit text value not equal to blank
            SmsManager smsManager = SmsManager.getDefault();
            //send text message
            smsManager.sendTextMessage(sPhone, null, sMessage, null, null);
            //Display toast
            Toast.makeText(getApplicationContext()
                    , "SMS sent Successfully", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext()
                    , "Enter Value First", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//check condition
        if(requestCode == 100 &&  grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED){
            //when Permission is granted
            //call method
            sendMessage();
        }else{
            //when permission is denied
            //display toast
            Toast.makeText(getApplicationContext()
                    ,"Permission Denied!",Toast.LENGTH_SHORT).show();
        }

    }
}