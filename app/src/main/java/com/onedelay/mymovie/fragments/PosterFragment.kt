package com.onedelay.mymovie.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.data.local.entity.MovieEntity
import kotlinx.android.synthetic.main.fragment_poster.view.*

class PosterFragment : Fragment() {
    private var callback: PosterFragmentCallback? = null

    interface PosterFragmentCallback {
        fun onChangeFragment(bundle: Bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_poster, container, false) as ViewGroup

        arguments?.run {
            Glide.with(this@PosterFragment).load(getString(Constants.KEY_IMAGE_URL)).into(rootView.list_image)
            rootView.list_title.text = String.format(getString(R.string.list_fragment_title), getInt(Constants.KEY_INDEX), getString(Constants.KEY_TITLE))
            rootView.rate.text = String.format(getString(R.string.list_fragment_rate), getFloat(Constants.KEY_RATE))
            rootView.grade.text = String.format(getString(R.string.list_fragment_grade), getInt(Constants.KEY_GRADE))
        }

        rootView.detailButton.setOnClickListener {
            // 상세보기 버튼을 클릭하면 viewPager 를 숨기고 DetailFragment 를 띄우도록 콜백 메소드를 호출한다.
            arguments?.let { callback?.onChangeFragment(it) }
        }

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is PosterFragmentCallback) {
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        if (callback != null) callback = null
    }

    companion object {
        fun newInstance(index: Int, item: MovieEntity): PosterFragment {
            return PosterFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.KEY_INDEX, index)
                    putInt(Constants.KEY_MOVIE_ID, item.id)
                    putString(Constants.KEY_IMAGE_URL, item.image)
                    putString(Constants.KEY_TITLE, item.title)
                    putFloat(Constants.KEY_RATE, item.reservation_rate) // 예매율
                    putInt(Constants.KEY_GRADE, item.grade)
                    putString(Constants.KEY_DATE, item.date)
                    putFloat(Constants.KEY_RATING, item.audience_rating)
                }
            }
        }
    }
}
