package com.sharon.sample.mpesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MeriGoReportsAdapter extends BaseAdapter {
    Context context;
    ArrayList<AwardedUser> awardedUsers;
    DatabaseReference usersRef;
    public MeriGoReportsAdapter(Context context, ArrayList<AwardedUser> awardedUsers){
        this.context = context;
        this.awardedUsers = awardedUsers;
        usersRef = FirebaseDatabase.getInstance().getReference("users/awarded");
    }

    @Override
    public int getCount() {
        return awardedUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.merigo_report_item, parent, false);

        // get views in the inflated layout
        TextView sNo, phoneNumber, username, winDate;
        sNo = view.findViewById(R.id.sNo);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        username = view.findViewById(R.id.username);
        winDate = view.findViewById(R.id.winDate);


        AwardedUser user = awardedUsers.get(position);
        sNo.setText(String.valueOf(position+1));
        phoneNumber.setText(user.getPhoneNumber());
        username.setText(user.getName());
        winDate.setText(user.getAwardedDate());


        return view;
    }
}