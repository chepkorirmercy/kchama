package com.sharon.sample.mpesa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharon.mpesa.stkpush.Mode;
import com.sharon.mpesa.stkpush.interfaces.PaymentVerificationListener;
import com.sharon.mpesa.stkpush.interfaces.TokenListener;
import com.sharon.mpesa.stkpush.model.Mpesa;
import com.sharon.mpesa.stkpush.model.STKPush;
import com.sharon.mpesa.stkpush.model.STKQuery;
import com.sharon.mpesa.stkpush.model.Token;
import com.sharon.sample.mpesa.models.PaymentItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class VerifyPayments extends AppCompatActivity{
    Mpesa mpesa;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    PaymentItemAdapter adapter;
    List<PaymentItem> paymentItemList;
    DatabaseReference paymentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_payments);

        paymentItemList = new ArrayList<>();
        adapter = new PaymentItemAdapter(this, paymentItemList);
        paymentsRef = FirebaseDatabase.getInstance().getReference("payments");

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.paymentsRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);

        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("FirebaseDatabase", "Change in firebase database");
                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()){
                    paymentItemList.clear();
                    // store a list of unchecked payments to verify with M-Pesa
                    List<PaymentItem> unCheckedPayments = new ArrayList<>();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        PaymentItem paymentItem = snapshot.getValue(PaymentItem.class);
                        if(paymentItem.isChecked()){
                            paymentItemList.add(paymentItem);
                        } else {
                            unCheckedPayments.add(paymentItem);
                        }
                    }
                    // notify adapter of dataset change
                    adapter.notifyDataSetChanged();
                    // check payments
                    try {
                        if(!unCheckedPayments.isEmpty()){
                            checkPayments(unCheckedPayments);
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkPayments(List<PaymentItem> unCheckedPayments) throws UnsupportedEncodingException {
        final int[] count = {0};
        progressBar.setVisibility(View.VISIBLE);
        mpesa.getToken(new TokenListener() {
            @Override
            public void onTokenSuccess(Token token) {
                    for(PaymentItem paymentItem : unCheckedPayments){
                        STKQuery stkQuery = new STKQuery();
                        stkQuery.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
                        stkQuery.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
                        stkQuery.setTimestamp(STKPush.getTimestamp());
                        stkQuery.setCheckoutRequestId(paymentItem.getCheckoutRequestId());

                        mpesa.checkPaymentStatus(token, stkQuery, paymentItem.getId(), new PaymentVerificationListener() {
                            @Override
                            public void onPaymentVerified(String paymentId, boolean isPaid) {
                                // update whether chama member has paid or not
                                paymentsRef.child(paymentId).child("checked").setValue(true);
                                paymentsRef.child(paymentId).child("paid").setValue(isPaid);

                                count[0]++;
                                if(count[0] == unCheckedPayments.size()){
                                    // all items have been checked. Stop the progressbar
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("CheckPayments", "There was an error verifying payment status: "+e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
            }

            @Override
            public void OnTokenError(Throwable throwable) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}