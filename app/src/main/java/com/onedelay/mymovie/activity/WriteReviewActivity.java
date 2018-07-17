package com.onedelay.mymovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.onedelay.mymovie.R;

public class WriteReviewActivity extends AppCompatActivity {
    RatingBar ratingBar;
    EditText contentsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("한줄평 작성");
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
        float rating = ratingBar.getRating();
        String content = contentsEditText.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("rating", rating);
        returnIntent.putExtra("content", content);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
