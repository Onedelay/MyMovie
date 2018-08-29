package com.onedelay.mymovie.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapters.ReviewAdapter;
import com.onedelay.mymovie.api.RequestProvider;
import com.onedelay.mymovie.database.ReviewEntity;
import com.onedelay.mymovie.utils.DividerItemDecorator;
import com.onedelay.mymovie.viewmodels.ReviewListViewModel;

import java.util.List;

public class AllReviewActivity extends AppCompatActivity {
    private TextView score;
    private ReviewAdapter adapter;

    private float rating;

    private ReviewListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.appbar_review_list));
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RequestProvider.isNetworkConnected(getBaseContext())) {
                    Intent intent = new Intent(AllReviewActivity.this, WriteReviewActivity.class);
                    intent.putExtra(Constants.KEY_MOVIE_ID, getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));
                    intent.putExtra(Constants.KEY_GRADE, getIntent().getIntExtra(Constants.KEY_GRADE, 12));
                    intent.putExtra(Constants.KEY_TITLE, getIntent().getStringExtra(Constants.KEY_TITLE));
                    startActivityForResult(intent, Constants.WRITE_REQUEST);
                } else {
                    Toast.makeText(AllReviewActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.review_list);

        adapter = new ReviewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        // 구분선
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewModel = ViewModelProviders.of(this).get(ReviewListViewModel.class);
        if (RequestProvider.isNetworkConnected(getBaseContext())) {
            viewModel.requestReviewList(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));
        }

        viewModel.setData(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));
        viewModel.getData().observe(this, new Observer<List<ReviewEntity>>() {
            @Override
            public void onChanged(@Nullable List<ReviewEntity> reviews) {
                if (reviews != null) {
                    score.setText(String.format(getString(R.string.all_review_score), rating, reviews.size()));
                    adapter.setItems(reviews);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        TextView textView = findViewById(R.id.movie_title);
        textView.setText(getIntent().getStringExtra(Constants.KEY_TITLE));

        ImageView imageView = findViewById(R.id.level);
        int grade = getIntent().getIntExtra(Constants.KEY_GRADE, 12);

        switch (grade) {
            case 12:
                imageView.setImageResource(R.drawable.ic_12);
                break;
            case 15:
                imageView.setImageResource(R.drawable.ic_15);
                break;
            case 19:
                imageView.setImageResource(R.drawable.ic_19);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_all);
        }

        rating = getIntent().getFloatExtra(Constants.KEY_RATING, 0.0f);

        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(rating / 2);

        score = findViewById(R.id.score);

        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(AllReviewActivity.this, getResources().getString(R.string.toast_reporting), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecommendClick(final int position, final int value) {
                if (RequestProvider.isNetworkConnected(getBaseContext())) {
                    viewModel.requestReviewRecommend(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0), adapter.getItem(position).getId(), "onedelay", adapter);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.WRITE_REQUEST && resultCode == Activity.RESULT_OK) {
            viewModel.requestReviewList(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));
        }
    }
}
