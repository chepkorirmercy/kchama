package com.sharon.sample.mpesa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sharon.mpesa.stkpush.Mode;
import com.sharon.mpesa.stkpush.api.response.STKPushResponse;
import com.sharon.mpesa.stkpush.interfaces.STKQueryListener;
import com.sharon.mpesa.stkpush.interfaces.TokenListener;
import com.sharon.mpesa.stkpush.model.Mpesa;
import com.sharon.mpesa.stkpush.model.STKPush;
import com.sharon.mpesa.stkpush.model.STKQuery;
import com.sharon.mpesa.stkpush.model.Token;

import java.io.UnsupportedEncodingException;

public class VerifyPayments extends AppCompatActivity implements TokenListener {
    Mpesa mpesa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_payments);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);
    }

    public void verify(View view) {
        try {
            mpesa.getToken(this);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onTokenSuccess(Token token) {
        // send stk query
        STKQuery stkQuery = new STKQuery();
        stkQuery.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
        stkQuery.setTimestamp(STKPush.getTimestamp());
        stkQuery.setPartyA(Config.BUSINESS_SHORT_CODE);
        stkQuery.setOriginatorConversationId("ws_CO_29012024140752307743703349");
        stkQuery.setTransactionId("ws_CO_29012024140752307743703349");

        mpesa.stkPushQuery(token, stkQuery, new STKQueryListener() {
            @Override
            public void onResponse(STKPushResponse stkPushResponse) {
                // query was successful
                Log.d("STKQuerySuccess", stkPushResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                // there was an error verifying payment
                Log.d("StkQueryError", throwable.getMessage());
            }
        });
    }

    @Override
    public void OnTokenError(Throwable throwable) {
        Log.d("TokenError", "Error getting token");
    }
}