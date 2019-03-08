package com.onedelay.mymovie.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.adapters.MovieListPagerAdapter
import com.onedelay.mymovie.data.api.RequestProvider
import com.onedelay.mymovie.fragments.DetailFragment
import com.onedelay.mymovie.fragments.PosterFragment
import com.onedelay.mymovie.viewmodels.MovieListViewModel
import kotlinx.android.synthetic.main.activity_movie_list.*
import kotlinx.android.synthetic.main.activity_movie_list.view.*
import kotlinx.android.synthetic.main.fragment_poster.*
import kotlinx.android.synthetic.main.order_menu_container.view.*

class MovieListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PosterFragment.PosterFragmentCallback, DetailFragment.OnBackPress {
    private var viewModel: MovieListViewModel? = null

    private var detailFragment: DetailFragment? = null
    private var adapter: MovieListPagerAdapter? = null

    private var optionMenuLayout: View? = null
    private var menuUp: Animation? = null
    private var menuDown: Animation? = null

    private var isPopUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        setSupportActionBar(toolbar).apply {
            title = getString(R.string.str_movie_list)
        }

        // 정렬 옵션 메뉴 애니메이션
        menuUp = AnimationUtils.loadAnimation(baseContext, R.anim.translate_up)
        menuDown = AnimationUtils.loadAnimation(baseContext, R.anim.translate_down)

        /* ViewModel 은 자체적으로 어떤 기능도 포함하고 있지 않기때문에, 일반적인 객체처럼 new 키워드로 생성하는 것은 아무런 의미가 없다.
         * 따라서 ViewModelProvider 를 통해 객체를 생성해야 한다. */
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel::class.java)

        observeViewModel()
        initDrawer()
        initMovieListAdapter()

        /* 인터넷이 연결되었을 경우 서버로부터 데이터를 다운로드하여 내부 DB 에 저장
         * 연결되어있지 않을 경우에는 DB 에 저장된 내용을 불러옴. (ViewModel 생성 시 DB 처리) */
        if (RequestProvider.isNetworkConnected(this)) {
            Toast.makeText(this, resources.getString(R.string.toast_request_server), Toast.LENGTH_SHORT).show()
            viewModel?.requestMovieList(Constants.ORDER_TYPE_RATING)
        }

        detailFragment = DetailFragment()
    }

    private fun initMovieListAdapter() {
        adapter = MovieListPagerAdapter(supportFragmentManager)
        viewPager.apply {
            adapter = this@MovieListActivity.adapter
            offscreenPageLimit = 3
            setPadding(dpToPx(45), 0, dpToPx(45), 0)
        }
    }

    private fun initDrawer() {
        val drawer = drawer_layout
        ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
            isDrawerIndicatorEnabled = false
            setHomeAsUpIndicator(R.drawable.ic_hamburger_menu)
            toolbarNavigationClickListener = View.OnClickListener {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }
            syncState()
        }.also { drawer.addDrawerListener(it) }

        nav_view.setNavigationItemSelectedListener(this)
    }

    /**
     * ViewModel 의 멤버 LiveData 를 observe 하도록 한다.
     * 데이터 변화가 감지되었을 때, UI 의 내용을 갱신하도록 onChanged 메소드를 오버라이드한다.
     */
    private fun observeViewModel() {
        viewModel?.dataMerger?.observe(this, Observer { movieEntities ->
            setOptionTitle(viewModel?.orderType ?: 0) // 화면 회전시에 원래대로 되돌아가므로 적용

            movieEntities?.let { movies ->
                adapter?.itemClear()
                for (i in movies.indices) {
                    adapter?.addItem(PosterFragment.newInstance(i + 1, movies[i]))
                }
            }

            viewPager.currentItem = viewModel?.curPosition ?: 0
        })
    }

    /**
     * 옵션 메뉴의 타이틀을 바꿔주는 메서드
     * @param orderType 정렬할 타입
     */
    private fun setOptionTitle(orderType: Int) {
        if (menu_container != null) {
            when (orderType) {
                Constants.ORDER_TYPE_RATING -> optionMenuLayout?.buttonId?.text = resources.getString(R.string.order_reservation)
                Constants.ORDER_TYPE_CURATION -> optionMenuLayout?.buttonId?.text = resources.getString(R.string.order_curation)
                Constants.ORDER_TYPE_SCHEDULED -> optionMenuLayout?.buttonId?.text = resources.getString(R.string.order_scheduled)
            }
        }
    }

    private fun startMenuAnimation() {
        if (!isPopUp) {
            menu_container.apply {
                visibility = View.VISIBLE
                startAnimation(menuDown)
            }
            isPopUp = true
        } else {
            menu_container.apply {
                startAnimation(menuUp)
                visibility = View.GONE
            }
            isPopUp = false
        }
    }

    private fun orderOptionChange() {
        // 예매율순
        menu_container.opt_rating.setOnClickListener {
            viewModel?.updateMovieList(Constants.ORDER_TYPE_RATING)
            startMenuAnimation()
            viewPager.currentItem = 0
        }

        // 큐레이션
        menu_container.opt_curation.setOnClickListener {
            viewModel?.updateMovieList(Constants.ORDER_TYPE_CURATION)
            startMenuAnimation()
            viewPager.currentItem = 0
        }

        // 상영예정
        menu_container.opt_scheduled.setOnClickListener {
            viewModel?.updateMovieList(Constants.ORDER_TYPE_SCHEDULED)
            startMenuAnimation()
            viewPager.currentItem = 0
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_order, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_order).apply {
            actionView.setOnClickListener { onOptionsItemSelected(this) }
        }.also { optionMenuLayout = it.actionView as LinearLayout }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_order) {
            // 정렬 옵션 메뉴 터치 이벤트
            orderOptionChange()
            startMenuAnimation()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_list -> {
                /* 백스택에는 DetailFragment 가 있을 것.
                 * 후에 또 다른 프래그먼트들이 백스택에 추가될 경우를 대비하여
                 * 메인화면(영화목록)으로 갈 수 있도록 백스택에 남은 프래그먼트를 모두 팝하도록 했다. */
                for (i in 0 until supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStack()
                }
                toolbar.title = getString(R.string.str_movie_list)
                viewPager.visibility = View.VISIBLE
            }
            R.id.nav_api -> {
            }
            R.id.nav_book -> {
            }
            R.id.nav_setting -> {
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onChangeFragment(bundle: Bundle) {
        supportFragmentManager.beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit()
        toolbar.title = getString(R.string.appbar_movie_detail)
        viewPager.visibility = View.GONE
        optionMenuLayout?.visibility = View.GONE
        viewModel?.curPosition = viewPager.currentItem

        detailFragment?.arguments = bundle
    }

    override fun onBackPressListener() {
        toolbar.title = getString(R.string.str_movie_list)
        viewPager.visibility = View.VISIBLE
        optionMenuLayout?.visibility = View.VISIBLE
    }

    override fun onRemoveListener() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
        viewPager.currentItem = viewModel?.curPosition ?: 0
    }

    companion object {
        private const val TAG = "MovieListActivity"
    }
}
