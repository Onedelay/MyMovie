package com.onedelay.mymovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onedelay.mymovie.utils.DividerItemDecorator;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.ReviewAdapter;
import com.onedelay.mymovie.ReviewData;
import com.onedelay.mymovie.ReviewItem;
import com.onedelay.mymovie.utils.TimeDescending;

import java.util.ArrayList;

public class AllReviewActivity extends AppCompatActivity {
    private ReviewAdapter adapter;

    private ArrayList<ReviewItem> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);

        reviewList = new ArrayList<>();

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("한줄평 목록");
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllReviewActivity.this, WriteReviewActivity.class);
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

        getData();

        reviewList.add(new ReviewItem(R.drawable.user1, "abc12**", System.currentTimeMillis()-2000000, 5, "웃긴 내용보다는 좀 더 진지한 영화.", "추천 1"));
        reviewList.add(new ReviewItem(R.drawable.user1, "bu_t**", System.currentTimeMillis()-3000000, 3, "연기가 부족한 느낌이 드는 배우도 있지만 전체적으로 재밌다.", "추천 0"));
        reviewList.add(new ReviewItem(R.drawable.user1, "em_r2**", System.currentTimeMillis()-4000000, 4, "말이 필요없어요.", "추천 0"));

        reviewList.sort(new TimeDescending());
        adapter.addItems(reviewList);

        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Toast.makeText(AllReviewActivity.this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(){
        Intent intent = getIntent();
        ArrayList<ReviewData> mainList = intent.getParcelableArrayListExtra("mainList");
        if (mainList != null && mainList.size() != 0) {
            for (ReviewData data : mainList) {
                reviewList.add(new ReviewItem(data.getImage(), data.getId(), data.getTime(), data.getRating(), data.getContent(), data.getRecommend()));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode != Activity.RESULT_CANCELED) {
            float rating = data.getFloatExtra("rating", 0.0f);
            String content = data.getStringExtra("content");
            reviewList.add(new ReviewItem(R.drawable.user1, "list", System.currentTimeMillis(), rating, content, "추천 0"));
            reviewList.sort(new TimeDescending());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
