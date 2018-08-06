package com.onedelay.mymovie.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.onedelay.mymovie.fragment.PosterFragment;

import java.util.ArrayList;

public class MovieListPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<PosterFragment> items = new ArrayList<>();

    public MovieListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(PosterFragment fragment) {
        items.add(fragment);
    }

    public void addItems(ArrayList<PosterFragment> fragments){
        items.addAll(fragments);
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

}
