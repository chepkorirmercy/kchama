package com.sharon.sample.mpesa;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private Context context;
    private  int totalTabs;

    public ViewPagerAdapter(Context context, int totalTabs, @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // waiting list
                WaitingListFragment waitingListFragment = new WaitingListFragment();
                return waitingListFragment;
            case 1:
                AwardedListFragment awardedListFragment = new AwardedListFragment();
                return awardedListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
