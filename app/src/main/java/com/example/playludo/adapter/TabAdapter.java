package com.example.playludo.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.playludo.fragments.BidFragment;
import com.example.playludo.utils.Utils;

public class TabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return new BidFragment(Utils.getBidPrice(position));
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
