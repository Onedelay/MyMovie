package com.onedelay.mymovie.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.viewmodel.ReviewListViewModel;

public class WriteReviewActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText contentsEditText;

    private int id;

    private ReviewListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        viewModel = ViewModelProviders.of(this).get(ReviewListViewModel.class);

        id = getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.appbar_review_write));
        }

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

        ratingBar = findViewById(R.id.rating_bar);
        contentsEditText = findViewById(R.id.review_content);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToReviewList();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    private void returnToReviewList() {
        viewModel.requestCreateComment(id, "ffff", ratingBar.getRating(), contentsEditText.getText().toString());
        setResult(Activity.RESULT_OK);
        finish();
    }
}
