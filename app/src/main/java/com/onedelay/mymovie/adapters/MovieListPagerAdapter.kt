package com.onedelay.mymovie.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.onedelay.mymovie.fragments.PosterFragment
import java.util.*

class MovieListPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val items = ArrayList<PosterFragment>()

    fun addItem(fragment: PosterFragment) {
        items.add(fragment)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    fun itemClear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}
