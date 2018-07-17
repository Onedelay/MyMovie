package com.onedelay.mymovie.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.ReviewData;
import com.onedelay.mymovie.ReviewItem;
import com.onedelay.mymovie.activity.AllReviewActivity;
import com.onedelay.mymovie.activity.WriteReviewActivity;
import com.onedelay.mymovie.api.AppHelper;
import com.onedelay.mymovie.api.data.MovieInfo;
import com.onedelay.mymovie.api.data.MovieList;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.utils.TimeDescending;
import com.onedelay.mymovie.utils.TimeString;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class DetailFragment extends Fragment {
    private ViewGroup rootView;

    private ImageButton thumbUpBtn;
    private ImageButton thumbDownBtn;
    private TextView likeCountView;
    private TextView hateCountView;

    private int likeCount;
    private int hateCount;

    private ArrayList<ReviewItem> mainList;

    private ImageView imageView;
    private TextView textView;
    private ImageView viewGrade;
    private TextView textRelease;
    private TextView textGenre; // 러닝타임도 같이 있음
    private TextView textRank;
    private TextView textTicketRate;
    private RatingBar ratingBar;
    private TextView totalRate;
    private TextView totalAudience;
    private TextView synopsis;
    private TextView director;
    private TextView actor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail, container, false);

        if (AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getContext());

        imageView = rootView.findViewById(R.id.movie_image);
        textView = rootView.findViewById(R.id.movie_title);
        viewGrade = rootView.findViewById(R.id.view_level);
        textRelease = rootView.findViewById(R.id.text_release);
        textGenre = rootView.findViewById(R.id.text_genre);
        textRank = rootView.findViewById(R.id.text_rank);
        textTicketRate = rootView.findViewById(R.id.text_ticket_rate);
        ratingBar = rootView.findViewById(R.id.rating_bar);
        totalRate = rootView.findViewById(R.id.totalRate);
        totalAudience = rootView.findViewById(R.id.totalAudience);
        synopsis = rootView.findViewById(R.id.synopsis);
        director = rootView.findViewById(R.id.director);
        actor = rootView.findViewById(R.id.actor);
        likeCountView = rootView.findViewById(R.id.thumb_up_count_view);
        hateCountView = rootView.findViewById(R.id.thumb_down_count_view);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            requestMovieDetail(bundle.getInt("id"));
        } else {
            Toast.makeText(getContext(), "데이터 없음", Toast.LENGTH_SHORT).show();
        }

        mainList = new ArrayList<>();
        mainList.add(new ReviewItem(R.drawable.user1, "kym71**", System.currentTimeMillis() - 500000, 4, "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요. ", "추천 0"));
        mainList.add(new ReviewItem(R.drawable.user1, "su_m**", System.currentTimeMillis() - 1000000, 5, "완전 재밌고 흥미진진하네요! 다음에 또 보고싶습니다. 배우들의 연기력에도 감탄했어요. 친구들한테도 추천할래요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", "추천 0"));
        mainList.sort(new TimeDescending());

        thumbUpBtn = rootView.findViewById(R.id.btn_thumb_up);
        thumbUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeClick();
            }
        });

        thumbDownBtn = rootView.findViewById(R.id.btn_thumb_down);
        thumbDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hateClick();
            }
        });

        setContents(rootView.findViewById(R.id.item1), mainList.get(0));
        setContents(rootView.findViewById(R.id.item2), mainList.get(1));

        rootView.findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteReviewActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        rootView.findViewById(R.id.btn_all_see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(putMainList(mainList));
            }
        });

        rootView.findViewById(R.id.btn_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "페이스북 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        rootView.findViewById(R.id.btn_kakao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "카카오톡 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        rootView.findViewById(R.id.btn_ticketing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "예매하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void requestMovieDetail(int id) {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovie";
        url += "?" + "id=" + id;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

            MovieInfo movieInfo = movieList.getResult().get(0);

            Glide.with(this).load(movieInfo.getThumb()).into(imageView);
            textView.setText(movieInfo.getTitle());
            setIcon(movieInfo.getGrade());
            textRelease.setText(String.format(getString(R.string.detail_fragment_date), movieInfo.getDate()));
            textGenre.setText(String.format(getString(R.string.detail_fragment_genre_time), movieInfo.getGenre(), movieInfo.getDuration()));
            textRank.setText(String.format(getString(R.string.detail_fragment_rank), movieInfo.getReservation_grade()));
            textTicketRate.setText(String.format(getString(R.string.detail_fragment_rate), movieInfo.getReservation_rate()));
            ratingBar.setRating(movieInfo.getAudience_rating());
            totalRate.setText(String.format(getString(R.string.float_value), movieInfo.getAudience_rating()));
            totalAudience.setText(String.format(getString(R.string.detail_fragment_audience), movieInfo.getAudience()));
            synopsis.setText(movieInfo.getSynopsis());
            director.setText(movieInfo.getDirector());
            actor.setText(movieInfo.getActor());
            likeCountView.setText(String.format(getString(R.string.int_value), movieInfo.getLike()));
            hateCountView.setText(String.format(getString(R.string.int_value), movieInfo.getDislike()));

            likeCount = Integer.parseInt(likeCountView.getText().toString());
            hateCount = Integer.parseInt(hateCountView.getText().toString());
        }
    }

    public void setIcon(int grade) {
        switch (grade) {
            case 12:
                viewGrade.setImageResource(R.drawable.ic_12);
                break;
            case 15:
                viewGrade.setImageResource(R.drawable.ic_15);
                break;
            case 19:
                viewGrade.setImageResource(R.drawable.ic_19);
                break;
            default:
                viewGrade.setImageResource(R.drawable.ic_all);
        }
    }

    public void likeClick() {
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            likeCount++;
            thumbUpBtn.setSelected(true);
        } else if (thumbUpBtn.isSelected()) {
            likeCount--;
            thumbUpBtn.setSelected(false);
        } else {
            likeCount++;
            hateCount--;
            thumbUpBtn.setSelected(true);
            thumbDownBtn.setSelected(false);
        }

        likeCountView.setText(String.format(getString(R.string.int_value), likeCount));
        hateCountView.setText(String.format(getString(R.string.int_value), hateCount));
    }

    public void hateClick() {
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            hateCount++;
            thumbDownBtn.setSelected(true);
        } else if (thumbDownBtn.isSelected()) {
            hateCount--;
            thumbDownBtn.setSelected(false);
        } else {
            hateCount++;
            likeCount--;
            thumbDownBtn.setSelected(true);
            thumbUpBtn.setSelected(false);
        }

        likeCountView.setText(String.format(getString(R.string.int_value), likeCount));
        hateCountView.setText(String.format(getString(R.string.int_value), hateCount));
    }

    public Intent putMainList(ArrayList<ReviewItem> items) {
        Intent intent = new Intent(getActivity(), AllReviewActivity.class);
        ArrayList<ReviewData> mainParcelList = new ArrayList<>();

        for (ReviewItem item : items) {
            mainParcelList.add(new ReviewData(item.getImage(), item.getId(), item.getTime(), item.getRating(), item.getContent(), item.getRecommend()));
        }

        intent.putParcelableArrayListExtra("mainList", mainParcelList);
        return intent;
    }

    public void setContents(View contentView, ReviewItem data) {
        ImageView imageView = contentView.findViewById(R.id.user_image);
        imageView.setImageResource(data.getImage());

        TextView idView = contentView.findViewById(R.id.review_user_id);
        idView.setText(data.getId());

        TextView timeView = contentView.findViewById(R.id.review_user_time);
        timeView.setText(TimeString.formatTimeString(data.getTime()));

        RatingBar ratingBar = contentView.findViewById(R.id.review_rating_bar);
        ratingBar.setRating(data.getRating());

        TextView ContentView = contentView.findViewById(R.id.review_content);
        ContentView.setText(data.getContent());

        TextView recommendView = contentView.findViewById(R.id.recommend);
        recommendView.setText(data.getRecommend());

        TextView declareBtn = contentView.findViewById(R.id.review_btn_declare);
        declareBtn.setText("신고하기");
        declareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode != Activity.RESULT_CANCELED) {
            float rating = data.getFloatExtra("rating", 0.0f);
            String content = data.getStringExtra("content");

            mainList.add(new ReviewItem(R.drawable.user1, "main", System.currentTimeMillis(), rating, content, "추천 0"));
            mainList.sort(new TimeDescending());

            setContents(rootView.findViewById(R.id.item1), mainList.get(0));
            setContents(rootView.findViewById(R.id.item2), mainList.get(1));
        }
    }
}
