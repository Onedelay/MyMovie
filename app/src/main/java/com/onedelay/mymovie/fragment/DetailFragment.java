package com.onedelay.mymovie.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.activity.AllReviewActivity;
import com.onedelay.mymovie.activity.WriteReviewActivity;
import com.onedelay.mymovie.api.RequestProvider;
import com.onedelay.mymovie.database.MovieEntity;
import com.onedelay.mymovie.database.ReviewEntity;
import com.onedelay.mymovie.utils.TimeString;
import com.onedelay.mymovie.viewmodel.MovieListViewModel;
import com.onedelay.mymovie.viewmodel.ReviewListViewModel;

import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "DETAIL_FRAGMENT";

    private ReviewListViewModel reviewViewModel;
    private MovieListViewModel viewModel;

    private ViewGroup rootView;
    private ImageButton thumbUpBtn;
    private ImageButton thumbDownBtn;
    private TextView likeCountView;
    private TextView hateCountView;
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

    private int likeCount;
    private int hateCount;

    private int id;
    private String title;
    private int grade;
    private float rating;

    private OnBackPress listener;

    public interface OnBackPress {
        void onBackPressListener();
    }

    public interface RecommendCallback {
        void UpdateData();
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

        viewModel = ViewModelProviders.of(getActivity()).get(MovieListViewModel.class);
        reviewViewModel = ViewModelProviders.of(getActivity()).get(ReviewListViewModel.class);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            id = bundle.getInt(Constants.KEY_MOVIE_ID);
            title = bundle.getString(Constants.KEY_TITLE);
            grade = bundle.getInt(Constants.KEY_GRADE);
            rating = bundle.getFloat(Constants.KEY_RATING);

            // 네트워크가 연결되어있으면 데이터 다운로드
            if (RequestProvider.isNetworkConnected(getContext())) {
                viewModel.requestMovieDetail(id);
                reviewViewModel.requestReviewList(id);
            }

            reviewViewModel.setData(id);
            reviewViewModel.getData().observe(this, new Observer<List<ReviewEntity>>() {
                @Override
                public void onChanged(@Nullable List<ReviewEntity> reviewEntities) {
                    if (reviewEntities != null && reviewEntities.size() > 1) {
                        setContents(rootView.findViewById(R.id.item1), reviewEntities.get(0));
                        setContents(rootView.findViewById(R.id.item2), reviewEntities.get(1));
                    }
                }
            });

            viewModel.getData(id).observe(this, new Observer<MovieEntity>() {
                @Override
                public void onChanged(@Nullable MovieEntity movie) {
                    // DB 로부터 읽어온 데이터를 UI 에 set
                    if (movie != null) {
                        Glide.with(getActivity()).load(movie.getThumb()).into(imageView);
                        textView.setText(movie.getTitle());
                        setIcon(movie.getGrade());
                        textRelease.setText(String.format(getString(R.string.detail_fragment_date), movie.getDate().replace('-', '.')));
                        textGenre.setText(String.format(getString(R.string.detail_fragment_genre_time), movie.getGenre(), movie.getDuration()));
                        textRank.setText(String.format(getString(R.string.detail_fragment_rank), movie.getReservation_grade()));
                        textTicketRate.setText(String.format(getString(R.string.detail_fragment_rate), movie.getReservation_rate()));
                        ratingBar.setRating(movie.getAudience_rating() / 2);
                        totalRate.setText(String.format(getString(R.string.float_value), movie.getAudience_rating()));
                        totalAudience.setText(String.format(getString(R.string.detail_fragment_audience), movie.getAudience()));
                        synopsis.setText(movie.getSynopsis());
                        director.setText(movie.getDirector());
                        actor.setText(movie.getActor());
                        likeCountView.setText(String.format(getString(R.string.int_value), movie.getLike()));
                        hateCountView.setText(String.format(getString(R.string.int_value), movie.getDislike()));
                    }

                    likeCount = Integer.parseInt(likeCountView.getText().toString());
                    hateCount = Integer.parseInt(hateCountView.getText().toString());
                }
            });

        } else {
            Toast.makeText(getContext(), "데이터 없음", Toast.LENGTH_SHORT).show();
        }

        thumbUpBtn = rootView.findViewById(R.id.btn_thumb_up);
        thumbUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RequestProvider.isNetworkConnected(getContext())) {
                    likeClick();
                } else {
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        thumbDownBtn = rootView.findViewById(R.id.btn_thumb_down);
        thumbDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RequestProvider.isNetworkConnected(getContext())) {
                    dislikeClick();
                } else {
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rootView.findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RequestProvider.isNetworkConnected(getContext())) {
                    Intent intent = new Intent(getActivity(), WriteReviewActivity.class);
                    intent.putExtra(Constants.KEY_MOVIE_ID, id);
                    intent.putExtra(Constants.KEY_TITLE, title);
                    intent.putExtra(Constants.KEY_GRADE, grade);
                    startActivityForResult(intent, Constants.WRITE_REQUEST);
                } else {
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
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
        String str = "likeyn";
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            viewModel.requestMovieRecommend(id, true, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbUpBtn.setSelected(true);
                }
            });
        } else if (thumbUpBtn.isSelected()) {
            viewModel.requestMovieRecommend(id, false, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbUpBtn.setSelected(false);
                }
            });

        } else {
            viewModel.requestMovieRecommend(id, true, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbUpBtn.setSelected(true); // 좋아요
                }
            });
            viewModel.requestMovieRecommend(id, false, "dislikeyn", new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbDownBtn.setSelected(false); // 싫어요 취소
                }
            });
        }
    }

    public void dislikeClick() {
        String str = "dislikeyn";
        if (!thumbUpBtn.isSelected() && !thumbDownBtn.isSelected()) {
            viewModel.requestMovieRecommend(id, true, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbDownBtn.setSelected(true);
                }
            });
        } else if (thumbDownBtn.isSelected()) {
            viewModel.requestMovieRecommend(id, false, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbDownBtn.setSelected(false);
                }
            });
        } else {
            viewModel.requestMovieRecommend(id, true, str, new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbDownBtn.setSelected(true); // 좋아요
                }
            });
            viewModel.requestMovieRecommend(id, false, "likeyn", new RecommendCallback() {
                @Override
                public void UpdateData() {
                    thumbUpBtn.setSelected(false); // 좋아요 취소
                }
            });
        }
    }

    public void setContents(View contentView, final ReviewEntity data) {
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
                if (RequestProvider.isNetworkConnected(getContext())) {
                    reviewViewModel.requestReviewRecommend(-1, data.getId(), data.getWriter(), null);
                } else {
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final TextView declareBtn = contentView.findViewById(R.id.review_btn_declare);
        declareBtn.setText(getString(R.string.declare));
        declareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), data.getWriter() + "님을 신고합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.WRITE_REQUEST && resultCode == Activity.RESULT_OK) {
            reviewViewModel.requestReviewList(id);
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

        // 액티비티에서 영화 상세보기 프래그먼트가 분리될 때 툴바의 이름을 바꾸고, viewpager 를 보여주기 위함.
        listener.onBackPressListener();
        if (listener != null) {
            listener = null;
        }
    }
}
