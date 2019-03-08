package com.onedelay.mymovie.activities

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.viewmodels.ReviewListViewModel

class WriteReviewActivity : AppCompatActivity() {
    private var ratingBar: RatingBar? = null
    private var contentsEditText: EditText? = null

    private var id: Int = 0

    private var viewModel: ReviewListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        viewModel = ViewModelProviders.of(this).get(ReviewListViewModel::class.java!!)

        id = intent.getIntExtra(Constants.KEY_MOVIE_ID, 0)

        // 앱바 제목 텍스트 변경
        val ab = supportActionBar
        ab?.setTitle(getString(R.string.appbar_review_write))

        val textView = findViewById<TextView>(R.id.movie_title)
        textView.text = intent.getStringExtra(Constants.KEY_TITLE)

        val imageView = findViewById<ImageView>(R.id.level)

        val grade = intent.getIntExtra(Constants.KEY_GRADE, 12)

        when (grade) {
            12 -> imageView.setImageResource(R.drawable.ic_12)
            15 -> imageView.setImageResource(R.drawable.ic_15)
            19 -> imageView.setImageResource(R.drawable.ic_19)
            else -> imageView.setImageResource(R.drawable.ic_all)
        }

        ratingBar = findViewById(R.id.rating_bar)
        contentsEditText = findViewById(R.id.review_content)

        findViewById<View>(R.id.btn_save).setOnClickListener { returnToReviewList() }

        findViewById<View>(R.id.btn_cancel).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun returnToReviewList() {
        viewModel!!.requestCreateComment(id, "ffff", ratingBar!!.rating, contentsEditText!!.text.toString())
        Log.d(TAG, System.currentTimeMillis().toString() + "")
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private val TAG = "WRITE_REVIEW_ACTIVITY"
    }
}
