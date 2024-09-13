package com.sharon.sample.mpesa;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    public  final TextView description,name,date;
    public final ImageButton btndelete;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        description=itemView.findViewById(R.id.txtDesc);
        name=itemView.findViewById(R.id.txtName);
        date=itemView.findViewById(R.id.txtDate);
        btndelete=itemView.findViewById(R.id.btndelete);
    }
}

