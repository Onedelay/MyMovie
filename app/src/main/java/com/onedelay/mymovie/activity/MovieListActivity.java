package com.onedelay.mymovie.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.MovieListPagerAdapter;
import com.onedelay.mymovie.api.AppHelper;
import com.onedelay.mymovie.api.data.MovieInfo;
import com.onedelay.mymovie.api.data.MovieList;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.fragment.DetailFragment;
import com.onedelay.mymovie.fragment.PosterFragment;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback, DetailFragment.OnBackPress {
    private DetailFragment detailFragment;

    private Toolbar toolbar;

    private ViewPager viewPager;

    private MovieListPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("영화 목록");
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPadding(dpToPx(45), 0, dpToPx(45), 0);

        if (AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getBaseContext());

        adapter = new MovieListPagerAdapter(getSupportFragmentManager());
        requestMovieList();
        viewPager.setAdapter(adapter);

        detailFragment = new DetailFragment();
    }

    public PosterFragment setData(int index, int id, String imageUrl, String title, float rate, int grade, String date, float rating) {
        PosterFragment fragment = new PosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putInt("id", id);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("title", title);
        bundle.putFloat("rate", rate);
        bundle.putInt("grade",grade);
        bundle.putString("date", date);
        bundle.putFloat("rating", rating);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void requestMovieList() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovieList";
        url += "?" + "type=1";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    private void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);
        if (info.getCode() == 200) {
            MovieList movieList = gson.fromJson(response, MovieList.class);

            for (int i = 0; i < movieList.getResult().size(); i++) {
                MovieInfo movieInfo = movieList.getResult().get(i);
                adapter.addItem(setData(i + 1, movieInfo.getId(), movieInfo.getImage(), movieInfo.getTitle(), movieInfo.getReservation_rate(), movieInfo.getGrade(), movieInfo.getDate(), movieInfo.getAudience_rating()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            toolbar.setTitle("영화 목록");
            viewPager.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_api) {

        } else if (id == R.id.nav_book) {

        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChangeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit();
        toolbar.setTitle("영화 상세");
        viewPager.setVisibility(View.GONE);
    }

    @Override
    public void setData(int id, String title, int grade, float rating) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putInt("grade", grade);
        bundle.putFloat("rating", rating);
        detailFragment.setArguments(bundle);
    }

    @Override
    public void onBackPressListener() {
        toolbar.setTitle("영화 목록");
        viewPager.setVisibility(View.VISIBLE);
    }
}
