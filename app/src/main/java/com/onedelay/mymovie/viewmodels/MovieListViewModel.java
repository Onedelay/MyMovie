package com.onedelay.mymovie.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.api.GsonRequest;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.MovieEntity;
import com.onedelay.mymovie.fragments.DetailFragment;

import java.util.List;

public class MovieListViewModel extends AndroidViewModel {
    private static final String TAG = "MOVIE_LIST_VIEW_MODEL";
    /**
     * DB 로부터 읽어온 데이터 저장.
     * 여기에 observer 를 달아서 데이터를 관찰하며, 내용이 변경되었을 때 화면 갱신을 하도록 함
     * 영화 목록 정렬 옵션에 따라 각각 다른 LiveData 로 관찰할 것.
     */
    private LiveData<List<MovieEntity>> mData1;
    private LiveData<List<MovieEntity>> mData2;
    private LiveData<List<MovieEntity>> mData3;

    /**
     * 3개의 LiveData 를 하나로 병합하여 관리
     */
    private MediatorLiveData<List<MovieEntity>> mDataMerger;

    /**
     * 보여줄 LiveData 를 설정하기 위한 변수
     */
    private int orderType;

    public MovieListViewModel(@NonNull Application application) {
        super(application);

        mDataMerger = new MediatorLiveData<>();
        mDataMerger.setValue(null);

        orderType = Constants.ORDER_TYPE_RATING;

        // Room DB 로부터 영화 목록 데이터를 읽어옴.
        mData1 = AppDatabase.getInstance(application.getApplicationContext()).movieDao().selectMoviesOrderByReservationRate();
        mData2 = AppDatabase.getInstance(application.getApplicationContext()).movieDao().selectMoviesOrderByAudienceRating();
        mData3 = AppDatabase.getInstance(application.getApplicationContext()).movieDao().selectMoviesOrderByDate();

        // LiveData 뭉치 만들기
        mDataMerger.addSource(mData1, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                if (orderType == Constants.ORDER_TYPE_RATING) {
                    mDataMerger.setValue(movieEntities);
                }
            }
        });

        mDataMerger.addSource(mData2, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                if (orderType == Constants.ORDER_TYPE_CURATION) {
                    mDataMerger.setValue(movieEntities);
                }
            }
        });

        mDataMerger.addSource(mData3, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                if (orderType == Constants.ORDER_TYPE_SCHEDULED) {
                    mDataMerger.setValue(movieEntities);
                }
            }
        });
    }

    /**
     * 영화 목록 화면(메인화면)에서 사용하는 메서드
     *
     * @return 영화 목록 LiveData (정렬 옵션에 따라 set 되는 LiveData 가 다름)
     */
    public MediatorLiveData<List<MovieEntity>> getDataMerger() {
        return mDataMerger;
    }

    /**
     * 영화 목록 정렬에 사용되는 메서드
     *
     * @param orderType 정렬 타입(Constants 에 정의)
     */
    public void updateMovieList(int orderType) {
        this.orderType = orderType;

        switch (orderType) {
            case Constants.ORDER_TYPE_RATING:
                mDataMerger.setValue(mData1.getValue());
                break;
            case Constants.ORDER_TYPE_CURATION:
                mDataMerger.setValue(mData2.getValue());
                break;
            case Constants.ORDER_TYPE_SCHEDULED:
                mDataMerger.setValue(mData3.getValue());
                break;
        }
    }

    private void updateMovieDetail(final MovieEntity movie) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().updateMovies(movie);
            }
        }).start();
    }

    /**
     * 영화 상세보기 화면에서 사용하는 메서드
     *
     * @param id 영화 id
     * @return 영화 상세가 포함된 데이터 (MovieEntity)
     */
    public LiveData<MovieEntity> getData(int id) {
        return AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().selectMovieDetailLive(id);
    }

    /**
     * 서버로부터 영화 목록 데이터를 받아 DB 에 insert.
     * 해당 영화에 대한 상세 내용은 저장하지 않고, 상세보기 버튼을 눌렀을 경우에만 저장됨.
     */
    public void requestMovieList(int orderType) {
        String url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovieList?type=" + orderType;
        GsonRequest<ResponseInfo<List<MovieEntity>>> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<ResponseInfo<List<MovieEntity>>>() {
        }, new Response.Listener<ResponseInfo<List<MovieEntity>>>() {
            /**
             * @param response 성공적인 response 를 받았을 경우, DB 에 insert
             *                 새로운 데이터로 갱신하기 위해 Movies 테이블의 내용을 모두 지우고 새로운 데이터를 받도록 함. */
            @Override
            public void onResponse(ResponseInfo<List<MovieEntity>> response) {
                if (response.getCode() == 200) {
                    final MovieEntity[] array = new MovieEntity[response.getResult().size()];
                    for (int i = 0; i < response.getResult().size(); i++) {
                        array[i] = response.getResult().get(i);
                    }
                    // 데이터가 없을 경우 insert
                    if (AppDatabase.getInstance(getApplication()).movieDao().selectDataMovies().size() < 0) {
                        AppDatabase.getInstance(getApplication()).movieDao().insertMovies(array);
                    } else { // 있을 경우 update
                        AppDatabase.getInstance(getApplication()).movieDao().updateMovies(array);
                    }
                } else {
                    Toast.makeText(getApplication(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), getApplication().getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                Log.d(TAG, error.getMessage());
            }
        });
        VolleyHelper.requestServer(request);
    }

    /**
     * 서버로부터 영화의 상세 데이터를 받아 DB 에 insert
     *
     * @param movieId 영화 id
     */
    public void requestMovieDetail(int movieId) {
        String url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovie?id=" + movieId;
        GsonRequest<ResponseInfo<List<MovieEntity>>> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<ResponseInfo<List<MovieEntity>>>() {
        }, new Response.Listener<ResponseInfo<List<MovieEntity>>>() {
            @Override
            public void onResponse(ResponseInfo<List<MovieEntity>> response) {
                MovieEntity movie = response.getResult().get(0);
                // Room 의 Update 메서드를 이용하여 해당 영화에 대한 상세 데이터 갱신
                updateMovieDetail(movie);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), getApplication().getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
                Log.d(TAG, error.getMessage());
            }
        });
        VolleyHelper.requestServer(request);
    }

    /**
     * 영화 좋아요 서버 요청 후 성공적인 응답(200)을 받았을 경우 DB 갱신
     *
     * @param movieId 영화 id
     * @param check   이미 누른 상태인지 확인
     */
    private void updateMovieLike(int movieId, boolean check) {
        final MovieEntity movie = AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().selectMovieDetail(movieId);
        if (check) {
            movie.setLike(movie.getLike() + 1);
        } else {
            movie.setLike(movie.getLike() - 1);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().updateMovies(movie);
            }
        }).start();
    }

    /**
     * 영화 싫어요 서버 요청 후 성공적인 응답(200)을 받았을 경우 DB 갱신
     *
     * @param movieId 영화 id
     * @param check   이미 누른 상태인지 확인
     */
    private void updateMovieDislike(int movieId, boolean check) {
        final MovieEntity movie = AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().selectMovieDetail(movieId);
        if (check) {
            movie.setDislike(movie.getDislike() + 1);
        } else {
            movie.setDislike(movie.getDislike() - 1);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().updateMovies(movie);
            }
        }).start();
    }

    /**
     * 영화 좋아요, 싫어요 서버 요청 메서드
     *
     * @param movieId  영화 ID
     * @param check    좋아요, 싫어요 체크 여부
     * @param string   좋아요, 싫어요 URL 결정을 위한 String
     * @param callback 서버 요청 후 LiveData 의 Observer 에 종속되지 않은 UI 를 갱신하기 위한 콜백 메서드
     */
    public void requestMovieRecommend(final int movieId, final boolean check, final String string, final DetailFragment.RecommendCallback callback) {
        String url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseLikeDisLike?id=" + movieId + "&" + string + "=";
        url += check ? "Y" : "N";

        GsonRequest<ResponseInfo<String>> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<ResponseInfo<String>>() {
        }, new Response.Listener<ResponseInfo<String>>() {
            @Override
            public void onResponse(ResponseInfo<String> response) {
                if (response.getCode() == 200) {
                    if (string.equals("likeyn")) {
                        updateMovieLike(movieId, check);
                    } else {
                        updateMovieDislike(movieId, check);
                    }
                    callback.UpdateData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), getApplication().getResources().getString(R.string.toast_server_error), Toast.LENGTH_SHORT).show();
            }
        });
        VolleyHelper.requestServer(request);
    }
}
