package com.onedelay.mymovie.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.reflect.TypeToken
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R
import com.onedelay.mymovie.data.api.GsonRequest
import com.onedelay.mymovie.data.api.VolleyHelper
import com.onedelay.mymovie.data.api.data.ResponseInfo
import com.onedelay.mymovie.data.local.AppDatabase
import com.onedelay.mymovie.data.local.entity.MovieEntity
import com.onedelay.mymovie.fragments.DetailFragment

class MovieListViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application).movieDao()

    private val orderReservationRate = database.selectMoviesOrderByReservationRate()
    private val orderAudienceRate = database.selectMoviesOrderByAudienceRating()
    private val orderDate = database.selectMoviesOrderByDate()
    val dataMerger: MediatorLiveData<List<MovieEntity>> = MediatorLiveData()

    var orderType: Int = 0
        private set

    var curPosition = 0

    init {
        orderType = Constants.ORDER_TYPE_RATING

        // LiveData 뭉치 만들기
        dataMerger.run {
            addSource(orderReservationRate) {
                if (orderType == Constants.ORDER_TYPE_RATING) {
                    value = it
                }
            }

            addSource(orderAudienceRate) {
                if (orderType == Constants.ORDER_TYPE_CURATION) {
                    value = it
                }
            }

            addSource(orderDate) {
                if (orderType == Constants.ORDER_TYPE_SCHEDULED) {
                    value = it
                }
            }
        }
    }

    /**
     * 영화 목록 정렬에 사용되는 메서드
     *
     * @param orderType 정렬 타입(Constants 에 정의)
     */
    fun updateMovieList(orderType: Int) {
        this.orderType = orderType

        when (orderType) {
            Constants.ORDER_TYPE_RATING -> orderReservationRate.value?.let { dataMerger.value = it }
            Constants.ORDER_TYPE_CURATION -> orderAudienceRate.value?.let { dataMerger.value = it }
            Constants.ORDER_TYPE_SCHEDULED -> orderDate.value?.let { dataMerger.value = it }
        }
    }

    private fun updateMovieDetail(movie: MovieEntity) {
        Thread(Runnable { database.updateMovies(movie) }).start()
    }

    /**
     * 영화 상세보기 화면에서 사용하는 메서드
     *
     * @param id 영화 id
     * @return 영화 상세가 포함된 데이터 (MovieEntity)
     */
    fun getData(id: Int): LiveData<MovieEntity> {
        return database.selectMovieDetailLive(id)
    }

    /**
     * 서버로부터 영화 목록 데이터를 받아 DB 에 insert.
     * 해당 영화에 대한 상세 내용은 저장하지 않고, 상세보기 버튼을 눌렀을 경우에만 저장됨.
     */
    fun requestMovieList(orderType: Int) {
        val url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovieList?type=" + orderType
        val request =
                GsonRequest(
                        Request.Method.GET,
                        url,
                        object : TypeToken<ResponseInfo<List<MovieEntity>>>() {},
                        Response.Listener { response ->
                            /**
                             * @param response 성공적인 response 를 받았을 경우, DB 에 insert
                             * 새로운 데이터로 갱신하기 위해 Movies 테이블의 내용을 모두 지우고 새로운 데이터를 받도록 함.
                             */
                            if (response.code == 200 && response.result != null) {
                                // 데이터가 없을 경우 insert
                                if (database.selectDataMovies().isEmpty()) {
                                    database.insertMovies(*response.result.toTypedArray())
                                } else { // 있을 경우 update
                                    database.updateMovies(*response.result.toTypedArray())
                                }
                            } else {
                                Toast.makeText(getApplication(), response.message, Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener { error ->
                    Toast.makeText(getApplication(), getApplication<Application>().resources.getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show()
                    Log.e(TAG, error.message)
                })
        VolleyHelper.requestServer(request)
    }

    /**
     * 서버로부터 영화의 상세 데이터를 받아 DB 에 insert
     *
     * @param movieId 영화 id
     */
    fun requestMovieDetail(movieId: Int) {
        val url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovie?id=" + movieId
        val request =
                GsonRequest(
                        Request.Method.GET,
                        url,
                        object : TypeToken<ResponseInfo<List<MovieEntity>>>() {},
                        Response.Listener { response ->
                            val movie = response.result!![0]
                            // Room 의 Update 메서드를 이용하여 해당 영화에 대한 상세 데이터 갱신
                            updateMovieDetail(movie)
                        }, Response.ErrorListener { error ->
                    Toast.makeText(getApplication(), getApplication<Application>().resources.getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, error.message)
                })
        VolleyHelper.requestServer(request)
    }

    /**
     * 영화 좋아요 서버 요청 후 성공적인 응답(200)을 받았을 경우 DB 갱신
     *
     * @param movieId 영화 id
     * @param check   이미 누른 상태인지 확인
     */
    private fun updateMovieLike(movieId: Int, check: Boolean) {
        val movie = database.selectMovieDetail(movieId)
        if (check) {
            movie.like = movie.like + 1
        } else {
            movie.like = movie.like - 1
        }
        Thread(Runnable { database.updateMovies(movie) }).start()
    }

    /**
     * 영화 싫어요 서버 요청 후 성공적인 응답(200)을 받았을 경우 DB 갱신
     *
     * @param movieId 영화 id
     * @param check   이미 누른 상태인지 확인
     */
    private fun updateMovieDislike(movieId: Int, check: Boolean) {
        val movie = database.selectMovieDetail(movieId)
        if (check) {
            movie.dislike = movie.dislike + 1
        } else {
            movie.dislike = movie.dislike - 1
        }
        Thread(Runnable { database.updateMovies(movie) }).start()
    }

    /**
     * 영화 좋아요, 싫어요 서버 요청 메서드
     *
     * @param movieId  영화 ID
     * @param check    좋아요, 싫어요 체크 여부
     * @param string   좋아요, 싫어요 URL 결정을 위한 String
     * @param callback 서버 요청 후 LiveData 의 Observer 에 종속되지 않은 UI 를 갱신하기 위한 콜백 메서드
     */
    fun requestMovieRecommend(movieId: Int, check: Boolean, string: String, callback: DetailFragment.RecommendCallback) {
        var url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseLikeDisLike?id=" + movieId + "&" + string + "="
        url += if (check) "Y" else "N"

        val request =
                GsonRequest(
                        Request.Method.GET,
                        url,
                        object : TypeToken<ResponseInfo<String>>() {},
                        Response.Listener { response ->
                            if (response.code == 200) {
                                if (string == "likeyn") {
                                    updateMovieLike(movieId, check)
                                } else {
                                    updateMovieDislike(movieId, check)
                                }
                                callback.updateData()
                            }
                        }, Response.ErrorListener { Toast.makeText(getApplication(), getApplication<Application>().resources.getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show() })
        VolleyHelper.requestServer(request)
    }

    companion object {
        private const val TAG = "MOVIE_LIST_VIEW_MODEL"
    }
}
