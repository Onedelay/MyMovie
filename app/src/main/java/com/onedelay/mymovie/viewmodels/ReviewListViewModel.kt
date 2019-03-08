package com.onedelay.mymovie.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.reflect.TypeToken
import com.onedelay.mymovie.adapters.ReviewAdapter
import com.onedelay.mymovie.data.api.GsonRequest
import com.onedelay.mymovie.data.api.VolleyHelper
import com.onedelay.mymovie.data.api.data.ResponseInfo
import com.onedelay.mymovie.data.local.AppDatabase
import com.onedelay.mymovie.data.local.entity.ReviewEntity
import java.util.*

class ReviewListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application).reviewDao()

    var data: LiveData<List<ReviewEntity>>? = null
        private set

    fun setData(movieId: Int) {
        data = database.selectReviewsLiveData(movieId)
    }

    fun getReviewList(movieId: Int) = database.selectReviewsLiveData(movieId)

    /**
     * 한줄평 리스트를 서버에 요청하는 메서드
     * @param movieId 영화 정보
     */
    fun requestReviewList(movieId: Int) {
        val url = "${VolleyHelper.host}:${VolleyHelper.port}/movie/readCommentList?id=$movieId&limit=all"
        val request = GsonRequest(
                Request.Method.GET,
                url,
                object : TypeToken<ResponseInfo<List<ReviewEntity>>>() {},
                Response.Listener { response ->
                    if (response.result != null) {
                        database.clearReviews(movieId)
                        database.insertReviews(*response.result.toTypedArray())
                    }
                }, Response.ErrorListener {
            //Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, error.getMessage());
        })
        VolleyHelper.requestServer(request)
    }

    /**
     * 한줄평 작성 (무한정 작성 가능)
     * @param movieId  영화 ID
     * @param writer   작성자명
     * @param rating   평점
     * @param contents 한줄평 내용
     */
    fun requestCreateComment(movieId: Int, writer: String, rating: Float, contents: String) {
        val url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/createComment"

        val request = object : GsonRequest<ResponseInfo<String>>(
                Request.Method.POST,
                url,
                object : TypeToken<ResponseInfo<String>>() {},
                Response.Listener { response ->
                    if (response.code == 200) {
                        //Toast.makeText(getApplication(), "한줄평이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        requestReviewList(movieId)
                    }
                }, Response.ErrorListener {
            //Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
        }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = movieId.toString()
                params["writer"] = writer
                params["rating"] = rating.toString()
                params["contents"] = contents

                return params
            }
        }
        VolleyHelper.requestServer(request)
    }

    /**
     * 한줄평 추천 (무한정 추천 가능)
     * @param movieId  영화 ID. 리사이클러뷰 어댑터를 사용하지 않는 경우 필요없음.
     * @param reviewId 한줄평 ID
     * @param writer   한줄평 추천인
     * @param adapter  갱신할 데이터를 가진 리사이클러뷰 어댑터.
     */
    fun requestReviewRecommend(movieId: Int, reviewId: Int, writer: String, adapter: ReviewAdapter?) {
        val url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseRecommend"

        val request = object : GsonRequest<ResponseInfo<String>>(
                Request.Method.POST,
                url,
                object : TypeToken<ResponseInfo<String>>() {},
                Response.Listener {
                    // 성공적으로 추천되었을 경우.
                    database.updateReviewRecommend(reviewId)
                    adapter?.updateItem(database.selectReviews(movieId))
                },
                Response.ErrorListener { }) {
            // Post 방식으로 서버에 요청하는 방법.
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["review_id"] = reviewId.toString()
                params["writer"] = writer

                return params
            }
        }
        VolleyHelper.requestServer(request)
    }

    companion object {
        private val TAG = "ReviewListViewModel"
    }
}
