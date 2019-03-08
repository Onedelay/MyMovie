package com.onedelay.mymovie.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.activities.AllReviewActivity
import com.onedelay.mymovie.activities.PhotoViewActivity
import com.onedelay.mymovie.activities.WriteReviewActivity
import com.onedelay.mymovie.adapters.GalleryAdapter
import com.onedelay.mymovie.adapters.GalleryItem
import com.onedelay.mymovie.data.api.RequestProvider
import com.onedelay.mymovie.data.local.entity.MovieEntity
import com.onedelay.mymovie.data.local.entity.ReviewEntity
import com.onedelay.mymovie.utils.TimeString
import com.onedelay.mymovie.viewmodels.MovieListViewModel
import com.onedelay.mymovie.viewmodels.ReviewListViewModel
import kotlinx.android.synthetic.main.comment_item_view.*
import kotlinx.android.synthetic.main.comment_item_view.view.*
import kotlinx.android.synthetic.main.fragment_detail.*
import java.util.*

class DetailFragment : Fragment(), GalleryAdapter.OnItemClickListener {

    private var reviewViewModel: ReviewListViewModel? = null
    private var viewModel: MovieListViewModel? = null

    private var adapter: GalleryAdapter? = null

    private var movieId: Int = 0
    private var title: String? = null
    private var grade: Int = 0
    private var rating: Float = 0.toFloat()

    private var listener: OnBackPress? = null

    interface OnBackPress {
        fun onBackPressListener()
        fun onRemoveListener()
    }

    interface RecommendCallback {
        fun updateData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false) as ViewGroup

        viewModel = ViewModelProviders.of(activity!!).get(MovieListViewModel::class.java)
        reviewViewModel = ViewModelProviders.of(activity!!).get(ReviewListViewModel::class.java)

        adapter = GalleryAdapter(this)

        arguments?.run {
            movieId = getInt(Constants.KEY_MOVIE_ID)
            title = getString(Constants.KEY_TITLE)
            grade = getInt(Constants.KEY_GRADE)
            rating = getFloat(Constants.KEY_RATING)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingViewModel()
        initViews()
    }

    private fun initViews() {
        recyclerViewGallery.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@DetailFragment.adapter
        }

        btn_thumb_up.setOnClickListener {
            if (RequestProvider.isNetworkConnected(context!!)) {
                likeClick()
            } else {
                Toast.makeText(context, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
            }
        }

        btn_thumb_down.setOnClickListener {
            if (RequestProvider.isNetworkConnected(context!!)) {
                dislikeClick()
            } else {
                Toast.makeText(context, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
            }
        }

        btn_write.setOnClickListener {
            if (RequestProvider.isNetworkConnected(context!!)) {
                val intent = Intent(activity, WriteReviewActivity::class.java).apply {
                    putExtra(Constants.KEY_MOVIE_ID, movieId)
                    putExtra(Constants.KEY_TITLE, title)
                    putExtra(Constants.KEY_GRADE, grade)
                }
                startActivityForResult(intent, Constants.WRITE_REQUEST)
            } else {
                Toast.makeText(context, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
            }
        }

        btn_all_see.setOnClickListener {
            val intent = Intent(activity, AllReviewActivity::class.java).apply {
                putExtra(Constants.KEY_MOVIE_ID, movieId)
                putExtra(Constants.KEY_TITLE, title)
                putExtra(Constants.KEY_GRADE, grade)
                putExtra(Constants.KEY_RATING, rating)
            }
            startActivity(intent)
        }
    }

    private fun settingViewModel() {
        if (RequestProvider.isNetworkConnected(context!!)) {
            viewModel?.requestMovieDetail(movieId)
            reviewViewModel?.requestReviewList(movieId)
        } else {
            // 네트워크가 연결되어있지 않을 경우 갤러리 띄우지 않음
            container_gallery.visibility = View.GONE
        }

        reviewViewModel?.run {
            getReviewList(movieId).observe(this@DetailFragment, Observer { reviewEntities ->
                if (reviewEntities != null && reviewEntities.size > 1) {
                    setContents(item1, reviewEntities[0])
                    setContents(item2, reviewEntities[1])
                }
            })
        }

        viewModel?.getData(movieId)?.observe(this, Observer {
            it?.run {
                if (!RequestProvider.isNetworkConnected(context!!)) {
                    // 상세 데이터가 없을 경우 동작.
                    // 상세화면이 한번 뜨기때문에 번쩍거리는데 PosterFragment 에서 처리할 방법을 고안해야할 듯
                    Toast.makeText(context, resources.getString(R.string.toast_data_empty), Toast.LENGTH_SHORT).show()
                    listener?.onRemoveListener()
                } else {
                    Glide.with(activity).load(thumb).into(movie_image)
                    movie_title.text = title
                    setIcon(grade)
                    text_release.text = String.format(getString(R.string.detail_fragment_date), date.replace('-', '.'))
                    text_genre.text = String.format(getString(R.string.detail_fragment_genre_time), genre, duration)
                    text_rank.text = String.format(getString(R.string.detail_fragment_rank), reservation_grade)
                    text_ticket_rate.text = String.format(getString(R.string.detail_fragment_rate), reservation_rate)
                    rating_bar.rating = audience_rating / 2
                    totalRate.text = String.format(getString(R.string.float_value), audience_rating)
                    totalAudience.text = String.format(getString(R.string.detail_fragment_audience), audience)
                    tv_synopsis.text = synopsis
                    tv_director.text = director
                    tv_actor.text = actor
                    thumb_up_count_view.text = String.format(getString(R.string.int_value), like)
                    thumb_down_count_view.text = String.format(getString(R.string.int_value), dislike)
                    setGalleryList(this)
                }
            }
        })
    }

    private fun setGalleryList(movie: MovieEntity) {
        val photos = movie.photos?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?: emptyArray()
        val videos = movie.videos?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?: emptyArray()
        val items = ArrayList<GalleryItem>()

        for (s in photos) {
            items.add(GalleryItem(s, Constants.GALLERY_TYPE_PHOTO, s))
        }

        for (s in videos) {
            val id = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
            items.add(GalleryItem("https://img.youtube.com/vi/$id/0.jpg", Constants.GALLERY_TYPE_MOVIE, s))
        }
        adapter?.setItems(items)
    }

    private fun setIcon(grade: Int) {
        view_level.setImageResource(when (grade) {
            12 -> R.drawable.ic_12
            15 -> R.drawable.ic_15
            19 -> R.drawable.ic_19
            else -> R.drawable.ic_all
        })
    }

    private fun likeClick() {
        val str = "likeyn"

        if (btn_thumb_up != null && btn_thumb_down != null) {
            if (!btn_thumb_up.isSelected && !btn_thumb_down.isSelected) {
                viewModel?.requestMovieRecommend(movieId, true, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_up.isSelected = true
                    }
                })
            } else if (btn_thumb_up.isSelected) {
                viewModel?.requestMovieRecommend(movieId, false, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_up.isSelected = false
                    }
                })

            } else {
                viewModel?.requestMovieRecommend(movieId, true, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_up.isSelected = true // 좋아요
                    }
                })
                viewModel?.requestMovieRecommend(movieId, false, "dislikeyn", object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_down.isSelected = false // 싫어요 취소
                    }
                })
            }
        }
    }

    private fun dislikeClick() {
        val str = "dislikeyn"

        if (btn_thumb_up != null && btn_thumb_down != null) {
            if (!btn_thumb_up!!.isSelected && !btn_thumb_down!!.isSelected) {
                viewModel?.requestMovieRecommend(movieId, true, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_down.isSelected = true
                    }
                })
            } else if (btn_thumb_down.isSelected) {
                viewModel?.requestMovieRecommend(movieId, false, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_down.isSelected = false
                    }
                })
            } else {
                viewModel?.requestMovieRecommend(movieId, true, str, object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_down.isSelected = true // 좋아요
                    }
                })
                viewModel?.requestMovieRecommend(movieId, false, "likeyn", object : RecommendCallback {
                    override fun updateData() {
                        btn_thumb_up.isSelected = false // 좋아요 취소
                    }
                })
            }
        }
    }

    private fun setContents(view: View, data: ReviewEntity) {
        Glide.with(this).load(data.writer_image).into(view.user_image)

        view.apply {
            review_user_id.text = data.getHideWriter()
            review_user_time.text = TimeString.formatTimeString(data.timestamp * 1000)
            review_rating_bar.rating = data.rating
            review_content.text = data.contents

            recommend.text = String.format(getString(R.string.detail_review_recommend), data.recommend)
            recommend.setOnClickListener {
                if (RequestProvider.isNetworkConnected(context!!)) {
                    reviewViewModel?.requestReviewRecommend(-1, data.id, data.writer, null)
                } else {
                    Toast.makeText(context, resources.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        review_btn_declare.apply {
            text = getString(R.string.declare)
            setOnClickListener { Toast.makeText(activity, resources.getString(R.string.toast_reporting), Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onItemClick(position: Int) {
        val url = adapter?.getItem(position)?.url
        if (adapter?.getItem(position)?.type == Constants.GALLERY_TYPE_MOVIE) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } else {
            val intent = Intent(activity, PhotoViewActivity::class.java).apply {
                putExtra(Constants.KEY_IMAGE_URL, adapter?.getItem(position)?.thumbUrl)
            }
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.WRITE_REQUEST && resultCode == Activity.RESULT_OK) {
            reviewViewModel?.requestReviewList(movieId)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBackPress) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        // 액티비티에서 영화 상세보기 프래그먼트가 분리될 때 툴바의 이름을 바꾸고, viewpager 를 보여주기 위함.
        listener?.onBackPressListener()
        if (listener != null) {
            listener = null
        }
    }

    companion object {
        private val TAG = "DETAIL_FRAGMENT"
    }
}
