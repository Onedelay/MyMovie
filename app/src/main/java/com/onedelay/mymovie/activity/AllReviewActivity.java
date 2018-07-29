package com.onedelay.mymovie.activity;

import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.ReviewAdapter;
import com.onedelay.mymovie.api.AppHelper;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.api.data.ReviewInfo;
import com.onedelay.mymovie.utils.DividerItemDecorator;

import java.util.List;

public class AllReviewActivity extends AppCompatActivity {
    private ReviewAdapter adapter;

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
                Intent intent = new Intent(AllReviewActivity.this, WriteReviewActivity.class);
                intent.putExtra(Constants.KEY_MOVIE_ID, getIntent().getIntExtra("id", 0));
                intent.putExtra(Constants.KEY_GRADE, getIntent().getIntExtra("grade", 12));
                intent.putExtra(Constants.KEY_TITLE, getIntent().getStringExtra("title"));
                startActivityForResult(intent, 100);
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

        requestAllReview(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));

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

        float rating = getIntent().getFloatExtra(Constants.KEY_RATING, 0.0f);

        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(rating / 2);

        TextView score = findViewById(R.id.score);
        score.setText(String.format(getString(R.string.all_review_score), rating, 1111));

        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Toast.makeText(AllReviewActivity.this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestAllReview(int id) {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readCommentList?id=" + id;

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
                        Toast.makeText(AllReviewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    private void processReviewResponse(String response) {
        Gson gson = new Gson();
        ResponseInfo<List<ReviewInfo>> info = gson.fromJson(response, new TypeToken<ResponseInfo<List<ReviewInfo>>>(){}.getType());
        if (info.getCode() == 200) {
            adapter.addItems(info.getResult());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        requestAllReview(getIntent().getIntExtra(Constants.KEY_MOVIE_ID, 0));
    }
}
