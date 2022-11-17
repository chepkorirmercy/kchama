package com.sharon.sample.mpesa;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView Name, Time, Description;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        Time = itemView.findViewById(R.id.txtDate);
        Description = itemView.findViewById(R.id.txtDesc);
        Name = itemView.findViewById(R.id.txtName);
    }
}
