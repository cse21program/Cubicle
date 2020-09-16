package com.example.cubicle.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.cubicle.Fragment.ActivityOfClubesFragment;
import com.example.cubicle.Fragment.MembershipFragment;
import com.example.cubicle.Fragment.NewsFeedFragment;
import com.example.cubicle.Fragment.ProfileFragment;

public class TabAdapter extends FragmentPagerAdapter {

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                NewsFeedFragment newsFeedFragment=new NewsFeedFragment();
                return newsFeedFragment;
            case 1:
                ProfileFragment profileFragment=new ProfileFragment();
                return profileFragment;
            case 2:
                MembershipFragment membershipFragment=new MembershipFragment();
                return membershipFragment;
            case 3:
                ActivityOfClubesFragment activityOfClubesFragment=new ActivityOfClubesFragment();
                return activityOfClubesFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "News feed";
            case 1:
                return "Profile";
            case 2:
                return "Membership";
            case 3:
                return "Activity of Club";
            default:
                return null;

        }

    }
}
