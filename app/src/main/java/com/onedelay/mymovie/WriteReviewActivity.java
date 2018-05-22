package com.onedelay.mymovie;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

public class WriteReviewActivity extends AppCompatActivity {
    RatingBar ratingBar;
    EditText contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        ab.setTitle("한줄평 작성");

        ratingBar = findViewById(R.id.rating_bar);
        contents = findViewById(R.id.review_content);

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
        String content = contents.getText().toString();

        if (getIntent().getIntExtra("type", 0) == 100) { // 메인액티비티에서 작성
            Intent intent = new Intent(this, AllReviewActivity.class);
            intent.putExtra("rating", rating);
            intent.putExtra("content", content);
            intent.putExtra("type", 100);
            startActivity(intent);
            finish();
        } else { // 모두보기에서 작성
            Intent returnIntent = new Intent();
            returnIntent.putExtra("rating", rating);
            returnIntent.putExtra("content", content);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }
}
