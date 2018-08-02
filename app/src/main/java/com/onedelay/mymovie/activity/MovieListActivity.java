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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.MovieListPagerAdapter;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.MovieInfo;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.fragment.DetailFragment;
import com.onedelay.mymovie.fragment.PosterFragment;

import java.util.List;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback, DetailFragment.OnBackPress {
    private final static String VIEWPAGER_POSITION = "viewpagerPosition";
    private final static String TAG = "TEST#####";

    private DetailFragment detailFragment;

    private Toolbar toolbar;

    private ViewPager viewPager;

    private MovieListPagerAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.str_movie_list));
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

        adapter = new MovieListPagerAdapter(getSupportFragmentManager());

        /* 세로화면에서 가로화면으로 전환하였을 경우 onCreate()가 다시 호출된다.
         * 그래서 현재 viewPager 의 position 을 받아 현재 액티비티에 저장한다. (맨 아래 onSaveInstanceState 메소드 오버라이드)
         * 만약 저장된 position 이 있을 경우 (null 이 아닐 경우) viewPager 의 현재 위치를 그 값으로 변경한다.
         * 하지만 서버 요청이 되기 전에 이전 값을 불러오기때문에 (비동기로 동작하기 때문에) adapter 에 저장된 데이터가 없어서
         * 0번째 요소를 set 해주는 것 같아서 callback 메소드를 통해 순서대로 처리할 수 있도록 했다.
         * 근데 이 방법 뿐일까? */
        requestMovieList(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(adapter);

                if(savedInstanceState != null){
                    int position = savedInstanceState.getInt(VIEWPAGER_POSITION);
                    viewPager.setCurrentItem(position);
                    Log.v(TAG, "savedInstanceState 복원. 현재 position : "+viewPager.getCurrentItem());
                }
            }
        });

        detailFragment = new DetailFragment();
    }

    public PosterFragment setData(int index, int id, String imageUrl, String title, float rate, int grade, String date, float rating) {
        PosterFragment fragment = new PosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_INDEX, index);
        bundle.putInt(Constants.KEY_MOVIE_ID, id);
        bundle.putString(Constants.KEY_IMAGE_URL, imageUrl);
        bundle.putString(Constants.KEY_TITLE, title);
        bundle.putFloat(Constants.KEY_RATE, rate); // 예매율
        bundle.putInt(Constants.KEY_GRADE, grade);
        bundle.putString(Constants.KEY_DATE, date);
        bundle.putFloat(Constants.KEY_RATING, rating);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void requestMovieList(final Runnable runnable) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovieList";
        url += "?" + "type=1";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                        runnable.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleyHelper.requestServer(request);
    }

    private void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo<List<MovieInfo>> info = gson.fromJson(response,new TypeToken<ResponseInfo<List<MovieInfo>>>(){}.getType());
        if (info.getCode() == 200) {
            for (int i = 0; i < info.getResult().size(); i++) {
                MovieInfo movieInfo = info.getResult().get(i);
                adapter.addItem(setData(i + 1, movieInfo.getId(), movieInfo.getImage(), movieInfo.getTitle(), movieInfo.getReservation_rate(), movieInfo.getGrade(), movieInfo.getDate(), movieInfo.getAudience_rating()));
            }
            adapter.notifyDataSetChanged();
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
            /* 백스택에는 DetailFragment 가 있을 것.
             * 후에 또 다른 프래그먼트들이 백스택에 추가될 경우를 대비하여
             * 메인화면(영화목록)으로 갈 수 있도록 백스택에 남은 프래그먼트를 모두 팝하도록 했다. */
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            toolbar.setTitle(getString(R.string.str_movie_list));
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
        toolbar.setTitle(getString(R.string.appbar_movie_detail));
        viewPager.setVisibility(View.GONE);
    }

    @Override
    public void setData(int id, String title, int grade, float rating) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_MOVIE_ID, id);
        bundle.putString(Constants.KEY_TITLE, title);
        bundle.putInt(Constants.KEY_GRADE, grade);
        bundle.putFloat(Constants.KEY_RATING, rating);
        detailFragment.setArguments(bundle);
    }

    @Override
    public void onBackPressListener() {
        toolbar.setTitle(getString(R.string.str_movie_list));
        viewPager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "onSaveInstanceState 호출됨. 현재 position : "+viewPager.getCurrentItem());

        outState.putInt(VIEWPAGER_POSITION, viewPager.getCurrentItem());
    }
}
