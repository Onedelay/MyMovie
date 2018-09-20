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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapters.MovieListPagerAdapter;
import com.onedelay.mymovie.api.RequestProvider;
import com.onedelay.mymovie.database.MovieEntity;
import com.onedelay.mymovie.fragments.DetailFragment;
import com.onedelay.mymovie.fragments.PosterFragment;
import com.onedelay.mymovie.viewmodels.MovieListViewModel;

import java.util.List;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback, DetailFragment.OnBackPress {
    private final static String TAG = "TEST#####";

    private MovieListViewModel viewModel;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private DetailFragment detailFragment;
    private MovieListPagerAdapter adapter;

    private Animation menuUp;
    private Animation menuDown;

    private View menuContainer;
    private LinearLayout optionMenuLayout;
    private TextView buttonId;

    private boolean isPopUp = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // 정렬 옵션 메뉴 애니메이션
        menuUp = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_up);
        menuDown = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_down);
        menuContainer = findViewById(R.id.menu_container);

        /* ViewModel 은 자체적으로 어떤 기능도 포함하고 있지 않기때문에, 일반적인 객체처럼 new 키워드로 생성하는 것은 아무런 의미가 없다.
         * 따라서 ViewModelProvider 를 통해 객체를 생성해야 한다. */
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        /* ViewModel 의 멤버 LiveData 를 observe 하도록 한다.
         * 데이터 변화가 감지되었을 때, UI 의 내용을 갱신하도록 onChanged 메소드를 오버라이드한다. */
        viewModel.getDataMerger().observe(this, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                setOptionTitle(viewModel.getOrderType()); // 화면 회전시에 원래대로 되돌아가므로 적용
                if (movieEntities != null) {
                    adapter.itemClear();
                    for (int i = 0; i < movieEntities.size(); i++) {
                        adapter.addItem(PosterFragment.newInstance(i + 1, movieEntities.get(i)));
                    }
                    adapter.notifyDataSetChanged();
                }
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

        viewPager.setAdapter(adapter);

        /* 인터넷이 연결되었을 경우 서버로부터 데이터를 다운로드하여 내부 DB 에 저장
         * 연결되어있지 않을 경우에는 DB 에 저장된 내용을 불러옴. (ViewModel 생성 시 DB 처리) */
        if (RequestProvider.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.toast_request_server), Toast.LENGTH_SHORT).show();
            viewModel.requestMovieList(Constants.ORDER_TYPE_RATING);
        }

        detailFragment = new DetailFragment();
    }

    /**
     * 옵션 메뉴의 타이틀을 바꿔주는 메서드
     * @param orderType 정렬할 타입
     */
    private void setOptionTitle(int orderType) {
        if (optionMenuLayout != null) {
            switch (orderType) {
                case Constants.ORDER_TYPE_RATING:
                    buttonId.setText(getResources().getString(R.string.order_reservation));
                    break;
                case Constants.ORDER_TYPE_CURATION:
                    buttonId.setText(getResources().getString(R.string.order_curation));
                    break;
                case Constants.ORDER_TYPE_SCHEDULED:
                    buttonId.setText(getResources().getString(R.string.order_scheduled));
            }
        }
    }

    private void startMenuAnimation() {
        if (!isPopUp) {
            menuContainer.setVisibility(View.VISIBLE);
            menuContainer.startAnimation(menuDown);
            isPopUp = true;
        } else {
            menuContainer.startAnimation(menuUp);
            menuContainer.setVisibility(View.GONE);
            isPopUp = false;
        }
    }

    private void orderOptionChange() {
        // 예매율순
        menuContainer.findViewById(R.id.opt_rating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updateMovieList(Constants.ORDER_TYPE_RATING);
                startMenuAnimation();
                viewPager.setCurrentItem(0);
            }
        });

        // 큐레이션
        menuContainer.findViewById(R.id.opt_curation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updateMovieList(Constants.ORDER_TYPE_CURATION);
                startMenuAnimation();
                viewPager.setCurrentItem(0);
            }
        });

        // 상영예정
        menuContainer.findViewById(R.id.opt_scheduled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updateMovieList(Constants.ORDER_TYPE_SCHEDULED);
                startMenuAnimation();
                viewPager.setCurrentItem(0);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_order, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.menu_order);
        optionMenuLayout = (LinearLayout) menuItem.getActionView();
        buttonId = optionMenuLayout.findViewById(R.id.buttonId);

        optionMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_order) {
            // 정렬 옵션 메뉴 터치 이벤트
            orderOptionChange();
            startMenuAnimation();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_list:
                /* 백스택에는 DetailFragment 가 있을 것.
                 * 후에 또 다른 프래그먼트들이 백스택에 추가될 경우를 대비하여
                 * 메인화면(영화목록)으로 갈 수 있도록 백스택에 남은 프래그먼트를 모두 팝하도록 했다. */
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getSupportFragmentManager().popBackStack();
                }
                toolbar.setTitle(getString(R.string.str_movie_list));
                viewPager.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_api:
                break;
            case R.id.nav_book:
                break;
            case R.id.nav_setting:
                break;
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
        optionMenuLayout.setVisibility(View.GONE);
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
        optionMenuLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRemoveListener() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }
}
