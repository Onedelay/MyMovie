package com.onedelay.mymovie.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.onedelay.mymovie.fragments.PosterFragment;

import java.util.ArrayList;

public class MovieListPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<PosterFragment> items = new ArrayList<>();

    public MovieListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(PosterFragment fragment) {
        items.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void itemClear(){
        items.clear();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
