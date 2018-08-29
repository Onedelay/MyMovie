package com.onedelay.mymovie.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapters.MovieListPagerAdapter;
import com.onedelay.mymovie.api.RequestProvider;
import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.MovieEntity;
import com.onedelay.mymovie.fragments.DetailFragment;
import com.onedelay.mymovie.fragments.PosterFragment;
import com.onedelay.mymovie.viewmodels.MovieListViewModel;

import java.util.List;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback, DetailFragment.OnBackPress {
    private final static String TAG = "TEST#####";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private DetailFragment detailFragment;
    private MovieListPagerAdapter adapter;

    private AppDatabase database;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        database = AppDatabase.getInstance(getBaseContext());

        /* ViewModel 은 자체적으로 어떤 기능도 포함하고 있지 않기때문에, 일반적인 객체처럼 new 키워드로 생성하는 것은 아무런 의미가 없다.
         * 따라서 ViewModelProvider 를 통해 객체를 생성해야 한다. */
        final MovieListViewModel viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        /* ViewModel 의 멤버 LiveData 를 observe 하도록 한다.
         * 데이터 변화가 감지되었을 때, UI 의 내용을 갱신하도록 onChanged 메소드를 오버라이드한다. */
        viewModel.getData().observe(this, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                /* Movies 테이블이 갱신될때마다(LiveData 가 변경될때마다) 호출되어 viewPager 의 position 을 잃게 되므로
                 * 현재 position 을 저장해두고 복원하여 position 을 유지하도록 했다. */
                int position = viewPager.getCurrentItem();
                adapter.itemClear();
                for (int i = 0; i < (movieEntities != null ? movieEntities.size() : 0); i++) {
                    adapter.addItem(PosterFragment.newInstance(i + 1, movieEntities.get(i)));
                }
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(position);
            }
        });

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

        /* 인터넷이 연결되었을 경우 서버로부터 데이터를 다운로드하여 내부 DB 에 저장
         * 연결되어있지 않을 경우에는 DB 에 저장된 내용을 불러옴. (ViewModel 생성 시 DB 처리) */
        if (RequestProvider.isNetworkConnected(this)) {
            Toast.makeText(this, "서버로부터 데이터를 요청합니다.", Toast.LENGTH_SHORT).show();
            viewModel.requestMovieList();
        }

        detailFragment = new DetailFragment();
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
}
