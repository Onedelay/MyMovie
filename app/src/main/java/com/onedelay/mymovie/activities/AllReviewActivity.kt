package com.onedelay.mymovie.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.adapters.ReviewAdapter
import com.onedelay.mymovie.data.api.RequestProvider
import com.onedelay.mymovie.utils.DividerItemDecorator
import com.onedelay.mymovie.viewmodels.ReviewListViewModel
import kotlinx.android.synthetic.main.activity_all_review.*

class AllReviewActivity : AppCompatActivity(), ReviewAdapter.OnItemClickListener {
    private var rating: Float = 0F

    private var viewModel: ReviewListViewModel? = null

    private val adapter = ReviewAdapter(this@AllReviewActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)
        initViewModel(ViewModelProviders.of(this).get(ReviewListViewModel::class.java))
        initView()
        rating = intent.getFloatExtra(Constants.KEY_RATING, 0.0f)
    }

    private fun initView() {
        supportActionBar?.apply {
            title = getString(R.string.appbar_review_list)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        btn_write.setOnClickListener {
            if (RequestProvider.isNetworkConnected(baseContext)) {
                val intent = Intent(this@AllReviewActivity, WriteReviewActivity::class.java).apply {
                    putExtra(Constants.KEY_MOVIE_ID, intent.getIntExtra(Constants.KEY_MOVIE_ID, 0))
                    putExtra(Constants.KEY_GRADE, intent.getIntExtra(Constants.KEY_GRADE, 12))
                    putExtra(Constants.KEY_TITLE, intent.getStringExtra(Constants.KEY_TITLE))
                }
                startActivityForResult(intent, Constants.WRITE_REQUEST)
            } else {
                Toast.makeText(this@AllReviewActivity, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
            }
        }

        review_list.apply {
            adapter = this@AllReviewActivity.adapter
            layoutManager = LinearLayoutManager(baseContext)
            setHasFixedSize(true)

            ContextCompat.getDrawable(baseContext, R.drawable.recyclerview_divider)?.let {
                DividerItemDecorator(it)
            }.also { addItemDecoration(it) }
        }

        movie_title.text = intent.getStringExtra(Constants.KEY_TITLE)

        val grade = intent.getIntExtra(Constants.KEY_GRADE, 12)
        when (grade) {
            12 -> level.setImageResource(R.drawable.ic_12)
            15 -> level.setImageResource(R.drawable.ic_15)
            19 -> level.setImageResource(R.drawable.ic_19)
            else -> level.setImageResource(R.drawable.ic_all)
        }

        rating_bar.rating = rating / 2
    }

    private fun initViewModel(viewModel: ReviewListViewModel) {
        this.viewModel = viewModel
        if (RequestProvider.isNetworkConnected(baseContext)) {
            viewModel.requestReviewList(intent.getIntExtra(Constants.KEY_MOVIE_ID, 0))
        }

        val movieId = intent.getIntExtra(Constants.KEY_MOVIE_ID, 0)
        viewModel.setData(intent.getIntExtra(Constants.KEY_MOVIE_ID, 0))
        viewModel.getReviewList(movieId).observe(this, Observer { reviews ->
            if (reviews != null) {
                score.text = String.format(getString(R.string.all_review_score), rating, reviews.size)
                adapter.setItems(reviews)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.WRITE_REQUEST && resultCode == Activity.RESULT_OK) {
            viewModel?.requestReviewList(intent.getIntExtra(Constants.KEY_MOVIE_ID, 0))
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this@AllReviewActivity, resources.getString(R.string.toast_reporting), Toast.LENGTH_SHORT).show()
    }

    override fun onRecommendClick(position: Int, value: Int) {
        if (RequestProvider.isNetworkConnected(baseContext)) {
            viewModel?.requestReviewRecommend(intent.getIntExtra(Constants.KEY_MOVIE_ID, 0), adapter?.getItem(position).id, "onedelay", adapter)
        } else {
            Toast.makeText(baseContext, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
        }
    }
}
