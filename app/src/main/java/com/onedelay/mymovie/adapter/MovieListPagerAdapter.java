package com.onedelay.mymovie.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.onedelay.mymovie.fragment.PosterFragment;

import java.util.ArrayList;

public class MovieListPagerAdapter extends FragmentStatePagerAdapter{
    ArrayList<PosterFragment> items = new ArrayList<>();

    public MovieListPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public void addItem(PosterFragment fragment){
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

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return (position+1)+items.get(position).getTitle();
//    }
}
