package com.sharon.sample.mpesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharon.sample.mpesa.models.PaymentItem;

import java.util.List;

public class PaymentItemAdapter extends RecyclerView.Adapter<PaymentItemAdapter.PaymentItemViewHolder> {
    private Context context;
    private List<PaymentItem> paymentItemList;

    public PaymentItemAdapter(Context context, List<PaymentItem> paymentItemList) {
        this.context = context;
        this.paymentItemList = paymentItemList;

    }

    @NonNull
    @Override
    public PaymentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.payment_item,parent,false);
        return new PaymentItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentItemViewHolder holder, int position) {
        PaymentItem paymentItem = paymentItemList.get(position);
        holder.amount.setText(paymentItem.getAmount());
        holder.phoneNumber.setText(paymentItem.getPhoneNumber());
        holder.senderName.setText(paymentItem.getSenderName());
        holder.paymentStatus.setText(paymentItem.isPaid() ? "Paid": "Cancelled");

        if(paymentItem.isPaid()){
            holder.paymentStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return paymentItemList.size();
    }

    public static class PaymentItemViewHolder extends RecyclerView.ViewHolder{
        TextView senderName, phoneNumber, amount, paymentStatus;
        public PaymentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            amount = itemView.findViewById(R.id.amount);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
        }
    }
}
