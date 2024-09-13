package com.sharon.sample.mpesa;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharon.sample.mpesa.models.PaymentItem;

public class PersonalReport extends Fragment {
    View view;
    TableLayout tableLayout;
    TextView merigoTotal, projectsTotal;
    DatabaseReference paymentsRef;
    ProgressDialog loader;
    int totalMerigo = 0;
    int totalProjects = 0;
    public PersonalReport() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableLayout = view.findViewById(R.id.tabLayout);
        merigoTotal = view.findViewById(R.id.merigoTotal);
        projectsTotal = view.findViewById(R.id.projectsTotal);
        paymentsRef = FirebaseDatabase.getInstance().getReference("payments");
        loader = new ProgressDialog(getActivity());

        loader.setMessage("Generating user report...");
        loader.show();
        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalMerigo =0 ;
                totalProjects = 0;

                for(DataSnapshot ds: snapshot.getChildren()) {
                    PaymentItem payment = ds.getValue(PaymentItem.class);
                    if (payment != null && payment.getPurpose() != null) {
                        if (payment.getPurpose().toLowerCase().contains("merigo") && payment.isPaid()) {
                            totalMerigo += Integer.parseInt(payment.getAmount());
                            continue;
                        }
                        if (payment.getPurpose().toLowerCase().contains("project") && payment.isPaid()) {
                            totalProjects += Integer.parseInt(payment.getAmount());
                        }
                    }
                }

                // update UI
                merigoTotal.setText(String.valueOf(totalMerigo));
                projectsTotal.setText(String.valueOf(totalProjects));

                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_personal_report, container, false);
        return view;
    }
}