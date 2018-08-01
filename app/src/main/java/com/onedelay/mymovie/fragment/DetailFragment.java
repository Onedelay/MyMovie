package com.onedelay.mymovie.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.activity.AllReviewActivity;
import com.onedelay.mymovie.activity.WriteReviewActivity;
import com.onedelay.mymovie.api.RequestProvider;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.MovieInfo;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.api.data.ReviewInfo;
import com.onedelay.mymovie.utils.TimeString;

import org.w3c.dom.Text;

import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "serverTest";
    private ViewGroup rootView;

    private ImageButton thumbUpBtn;
    private ImageButton thumbDownBtn;
    private TextView likeCountView;
    private TextView hateCountView;

    private int likeCount;
    private int hateCount;

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

    private int id;
    private String title;
    private int grade;
    private float rating;

    private OnBackPress listener;

    public interface OnBackPress {
        void onBackPressListener();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail, container, false);

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
            id = bundle.getInt(Constants.KEY_MOVIE_ID);
            title = bundle.getString(Constants.KEY_TITLE);
            grade = bundle.getInt(Constants.KEY_GRADE);
            rating = bundle.getFloat(Constants.KEY_RATING);
            requestMovieDetail(id);
            requestLatestReview(id);
        } else {
            Toast.makeText(getContext(), "데이터 없음", Toast.LENGTH_SHORT).show();
        }

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
                dislikeClick();
            }
        });

        rootView.findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteReviewActivity.class);
                intent.putExtra(Constants.KEY_MOVIE_ID, id);
                intent.putExtra(Constants.KEY_TITLE, title);
                intent.putExtra(Constants.KEY_GRADE, grade);
                startActivityForResult(intent, Constants.WRITE_REQUEST);
            }
        });

        rootView.findViewById(R.id.btn_all_see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AllReviewActivity.class);
                intent.putExtra(Constants.KEY_MOVIE_ID, id);
                intent.putExtra(Constants.KEY_TITLE, title);
                intent.putExtra(Constants.KEY_GRADE, grade);
                intent.putExtra(Constants.KEY_RATING, rating);
                startActivity(intent);
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

    private void requestLatestReview(int id) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readCommentList?id=" + id + "&limit=2";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processReviewResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleyHelper.requestServer(request);
    }

    public void requestMovieDetail(int id) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readMovie";
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

        VolleyHelper.requestServer(request);
    }

    private void processReviewResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo<List<ReviewInfo>> info = gson.fromJson(response, new TypeToken<ResponseInfo<List<ReviewInfo>>>() {
        }.getType());
        if (info.getCode() == 200) {

            setContents(rootView.findViewById(R.id.item1), info.getResult().get(0));
            setContents(rootView.findViewById(R.id.item2), info.getResult().get(1));
        }
    }

    private void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo<List<MovieInfo>> info = gson.fromJson(response, new TypeToken<ResponseInfo<List<MovieInfo>>>() {
        }.getType());

        if (info.getCode() == 200) {
            MovieInfo movieInfo = info.getResult().get(0);

            Glide.with(this).load(movieInfo.getThumb()).into(imageView);
            textView.setText(movieInfo.getTitle());
            setIcon(movieInfo.getGrade());
            textRelease.setText(String.format(getString(R.string.detail_fragment_date), movieInfo.getDate().replace('-', '.')));
            textGenre.setText(String.format(getString(R.string.detail_fragment_genre_time), movieInfo.getGenre(), movieInfo.getDuration()));
            textRank.setText(String.format(getString(R.string.detail_fragment_rank), movieInfo.getReservation_grade()));
            textTicketRate.setText(String.format(getString(R.string.detail_fragment_rate), movieInfo.getReservation_rate()));
            ratingBar.setRating(movieInfo.getAudience_rating() / 2);
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

    private void processReviewRecommend(String url) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleyHelper.requestServer(request);
    }

    public void likeClick() {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseLikeDisLike?id=" + id + "&likeyn=";
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            url += "Y";
            likeCount++;
            thumbUpBtn.setSelected(true);
        } else if (thumbUpBtn.isSelected()) {
            url += "N";
            likeCount--;
            thumbUpBtn.setSelected(false);
        } else {
            likeCount++;
            hateCount--;
            thumbUpBtn.setSelected(true);
            thumbDownBtn.setSelected(false);
            processReviewRecommend(url.replace("likeyn","dislikeyn")+"N");
            url += "Y";
            try {
                Thread.sleep(500L); // 서버에 거의 동시에 보내면 적용이 안돼서 추가했습니다.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        processReviewRecommend(url);

        likeCountView.setText(String.format(getString(R.string.int_value), likeCount));
        hateCountView.setText(String.format(getString(R.string.int_value), hateCount));
    }

    public void dislikeClick() {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseLikeDisLike?id=" + id + "&dislikeyn=";
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            url += "Y";
            hateCount++;
            thumbDownBtn.setSelected(true);
        } else if (thumbDownBtn.isSelected()) {
            url += "N";
            hateCount--;
            thumbDownBtn.setSelected(false);
        } else {
            hateCount++;
            likeCount--;
            thumbDownBtn.setSelected(true);
            thumbUpBtn.setSelected(false);
            processReviewRecommend(url.replace("dislikeyn", "likeyn")+"N");
            url += "Y";
            try {
                Thread.sleep(500L); // 서버에 거의 동시에 보내면 적용이 안돼서 추가했습니다.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        processReviewRecommend(url);

        likeCountView.setText(String.format(getString(R.string.int_value), likeCount));
        hateCountView.setText(String.format(getString(R.string.int_value), hateCount));
    }

    public void setContents(View contentView, final ReviewInfo data) {
        ImageView imageView = contentView.findViewById(R.id.user_image);
        if (data.getWriter_image() != null) {
            Glide.with(this).load(data.getWriter_image()).into(imageView);
        }

        TextView idView = contentView.findViewById(R.id.review_user_id);
        idView.setText(data.getWriter());

        TextView timeView = contentView.findViewById(R.id.review_user_time);
        timeView.setText(TimeString.formatTimeString(data.getTimestamp()));

        RatingBar ratingBar = contentView.findViewById(R.id.review_rating_bar);
        ratingBar.setRating(data.getRating());

        TextView ContentView = contentView.findViewById(R.id.review_content);
        ContentView.setText(data.getContents());

        // 클래스 멤버변수로 선언하고 사용하면 추천이 이상하게 되었습니다.
        // 그래서 final 키워드를 붙였더니 제대로 동작하는데 이유가 뭘까요?
        final TextView recommend = contentView.findViewById(R.id.recommend);
        recommend.setText(String.format(getString(R.string.detail_review_recommend), data.getRecommend()));
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(recommend.getText().toString().substring(3));
                recommend.setText(String.format(getString(R.string.detail_review_recommend), v+1));
                RequestProvider.requestRecommend(String.valueOf(data.getId()), data.getWriter());
            }
        });

        final TextView declareBtn = contentView.findViewById(R.id.review_btn_declare);
        declareBtn.setText(getString(R.string.declare));
        declareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), data.getWriter()+"님을 신고합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.WRITE_REQUEST && resultCode == Activity.RESULT_OK) {
            requestLatestReview(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBackPress) {
            listener = (OnBackPress) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener.onBackPressListener();
        if (listener != null) {
            listener = null;
        }
    }
}
