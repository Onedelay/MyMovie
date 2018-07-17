package com.onedelay.mymovie.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.MovieListPagerAdapter;
import com.onedelay.mymovie.api.AppHelper;
import com.onedelay.mymovie.api.data.MovieInfo;
import com.onedelay.mymovie.api.data.MovieList;
import com.onedelay.mymovie.api.data.ResponseInfo;

public class ViewPagerFragment extends Fragment {
    private MovieListPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_viewpager, container, false);
        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        //viewPager.setPageMargin(dpToPx(5)); // 리뷰어님이 이거로 하라하셨는데 적용이 안됩니다!ㅠㅠ
        viewPager.setPadding(dpToPx(45), 0, dpToPx(45), 0);

        if (AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getContext());

        adapter = new MovieListPagerAdapter(getFragmentManager());
        requestMovieList();
        viewPager.setAdapter(adapter);

        return rootView;
    }

    public void requestMovieList() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovieList";
        url += "?" + "type=1";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "응답 받음", Toast.LENGTH_SHORT).show();
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);
        if (info.getCode() == 200) {
            MovieList movieList = gson.fromJson(response, MovieList.class);

            for (int i = 0; i < movieList.getResult().size(); i++) {
                MovieInfo movieInfo = movieList.getResult().get(i);
                adapter.addItem(setData(i+1, movieInfo.getId(), movieInfo.getImage(), movieInfo.getTitle(), movieInfo.getReservation_rate(), movieInfo.getGrade(), movieInfo.getDate()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    public PosterFragment setData(int index, int id, String imageUrl, String title, float rate, int grade, String date) {
        PosterFragment fragment = new PosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putInt("id", id);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("title", title);
        bundle.putFloat("rate", rate);
        bundle.putInt("grade",grade);
        bundle.putString("data", date);
        fragment.setArguments(bundle);
        return fragment;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
