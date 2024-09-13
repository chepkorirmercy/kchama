package com.sharon.sample.mpesa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class WaitingListFragment extends Fragment {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<User> users;
    FirebaseDatabase database;
    DatabaseReference ref, usersRef, awardedUsersRef, cyclesRef;
    ImageView spinImage;
    Animation spinAnimation;
    ProgressDialog loader;
    AlertDialog.Builder builder;
    String spinDateTxt;

    // required default constructor
    public WaitingListFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        // inflate layout for this fragment
        return  inflater.inflate(R.layout.fragment_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // show progress dialog
        loader = new ProgressDialog(getContext());
        builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        
        loader.setMessage("Getting users in waiting list... Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

      // set data for the view
        users = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        spinImage = view.findViewById(R.id.spinImage);
        myAdapter = new MyAdapter(getContext(), users);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        usersRef = database.getReference("user");
        awardedUsersRef = database.getReference("users/awarded");
        cyclesRef = database.getReference( "cycles");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);

        // listen for data change in users database reference
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if all users have been awarded
                if(!snapshot.exists()){
                    loader.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "No Members registered in the system.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // clear existing users
                users.clear();
                User user;
                boolean allUsersAwarded = true; // will change once we found a member who is not awarded yet;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        user = ds.getValue(User.class);
                        // check if user had been awarded or not
                        if (!user.isAwarded()) {
                            allUsersAwarded = false;
                            users.add(user);
                        }
                    }

                myAdapter.notifyDataSetChanged();
                if(loader.isShowing()) loader.dismiss();

                if(allUsersAwarded){
                    if(getActivity() != null){
                        Toast.makeText(getActivity(), "All members have been awarded.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(loader.isShowing()) loader.dismiss();
            }
        });


        // set spin animation
        spinAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.spin);

        // listen when spinning finishes and select a winner
        spinAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                getWinner();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // randomly select a user
        spinImage.setOnClickListener(v -> {
            if(users.size() == 0){
                Toast.makeText(getActivity().getApplicationContext(), "All members have been awarded.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // check if user is allowed to spin
            checkIfCanSpin(canSpin -> {
                if(canSpin) {
                    spinImage.startAnimation(spinAnimation);
                } else {
                    Toast.makeText(getActivity(), "You cannot spin now: Spin time: "+spinDateTxt, Toast.LENGTH_LONG).show();
                }
            });
            /**
             * NOTE: Selecting a winner will be invoked once the spin animation finishes
             * in the onAnimationEnd() method
             */
        });
    }

    // check if award cycle has started
    private void checkIfCanSpin(CanSpinListener listener){
        loader.show();
        cyclesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinDateTxt = snapshot.getValue(String.class);
                SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm");
                try {
                    Date spinTime = formatter.parse(spinDateTxt);
                    Calendar calendar = Calendar.getInstance();

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(spinTime);
                    endTime.add(Calendar.MINUTE, 30);

                    // Get the current time
                    Date currentTime = calendar.getTime();

                    // Check if the current time is within the duration
                    if (currentTime.after(spinTime) && currentTime.before(endTime.getTime())) {
                        listener.canSpin(true);
                    } else {
                        listener.canSpin(false);
                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } finally {
                    loader.dismiss();
                }

                // covert t
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // failed to get spin date from database
            }
        });
    }

    public User getRandomUser(){
        Random random = new Random();
        if(users.size() == 0) return null;

        return users.get(random.nextInt(users.size()));
    }

    // get winner
    public void  getWinner(){
        // put winner selection logic here
        User user = getRandomUser();
        // when no users are left
        if(user == null) {
            Toast.makeText(getActivity().getApplicationContext(), "All members have been awarded.", Toast.LENGTH_SHORT).show();
            return;
        };
        // get the position of the user in users ArrayList
        int userPosition = users.indexOf(user);

        users.remove(user);
        myAdapter.notifyItemRemoved(userPosition);
        myAdapter.notifyItemRangeChanged(userPosition, users.size());

        Toast.makeText(getActivity().getApplicationContext(), "The winner was: "+user.getName(), Toast.LENGTH_SHORT).show();

        // update this user as awarded (in the /user ref)
        user.setAwarded(true);
        usersRef.child(user.getId()).setValue(user);

        // add the selected user to awarded list database (in the /users/awarded ref)
        awardedUsersRef.child(user.getId()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity().getApplicationContext(), user.getName()+" successfully added to Awarded Members list.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Unable to add "+user.getName()+" to Awarded Members list", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface CanSpinListener{
        void canSpin(boolean canSpin);
    }
}
