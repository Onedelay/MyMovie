package com.onedelay.mymovie.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
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
     */
    private LiveData<List<MovieEntity>> mData;

    public MovieListViewModel(@NonNull Application application) {
        super(application);

        // Room DB 로부터 영화 목록 데이터를 읽어옴.
        mData = AppDatabase.getInstance(application.getApplicationContext()).movieDao().selectMovies();
    }

    public LiveData<List<MovieEntity>> getData() {
        return mData;
    }

    private void updateMovieDetail(final MovieEntity movie) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().updateMovies(movie);
            }
        }).start();
    }

    public LiveData<MovieEntity> getData(int id) {
        return AppDatabase.getInstance(getApplication().getApplicationContext()).movieDao().selectMovieDetailLive(id);
    }

    /**
     * 서버로부터 영화 목록 데이터를 받아 DB 에 insert.
     * 해당 영화에 대한 상세 내용은 저장하지 않고, 상세보기 버튼을 눌렀을 경우에만 저장됨.
     */
    public void requestMovieList() {
        String url = VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovieList?type=1";
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
                    List<MovieEntity> movies = AppDatabase.getInstance(getApplication()).movieDao().selectDataMovies();
                    if (movies.size() == 0) { // 아무 데이터가 없을 경우 새로 DB 에 insert
                        AppDatabase.getInstance(getApplication()).movieDao().insertMovies(array);
                    } else {
                        /* 현재 한번에 영화 목록 데이터를 받는데, 만약 5개가 아닌 1개가 더 추가되어온다면 작동이 안될 수도 있다.
                         * 따라서 데이터를 따로 id 체크 후 없을 경우 insert, 있을 경우 update 를 하도록 하면 되지 않을까싶다. */
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
     * @param movieId 영화 id
     * @param check 이미 누른 상태인지 확인
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
     * @param movieId 영화 id
     * @param check 이미 누른 상태인지 확인
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
