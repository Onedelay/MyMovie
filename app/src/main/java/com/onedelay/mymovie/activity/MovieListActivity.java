package com.onedelay.mymovie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.MovieListPagerAdapter;
import com.onedelay.mymovie.fragment.DetailFragment;
import com.onedelay.mymovie.fragment.PosterFragment;
import com.onedelay.mymovie.fragment.ViewPagerFragment;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback {
    private ViewPagerFragment viewPagerFragment;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("영화 목록");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPagerFragment = new ViewPagerFragment();
        detailFragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, viewPagerFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ViewPagerFragment()).commit();
        } else if (id == R.id.nav_api) {

        } else if (id == R.id.nav_book) {

        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChangeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).commit();
    }

    @Override
    public void setData(int resId, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt("image", resId);
        bundle.putString("title", title);
        detailFragment.setArguments(bundle);
    }
}
