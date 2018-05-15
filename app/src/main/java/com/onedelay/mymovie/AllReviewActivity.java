package com.onedelay.mymovie;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class AllReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        ab.setTitle("한줄평 목록");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllReviewActivity.this, WriteReviewActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        recyclerView = findViewById(R.id.review_list);

        final ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // 구분선
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.recyclerview_divider, getTheme()));
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.addItem(new ReviewItem(R.drawable.user1, "kym71**", "10분전", 4, "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요.", "추천 0"));
        adapter.addItem(new ReviewItem(R.drawable.user1, "su_m**", "12분전", 5, "완전 재밌고 흥미진진하네요! 다음에 또 보고싶습니다. 배우들의 연기력에도 감탄했습니다", "추천 0"));
        adapter.addItem(new ReviewItem(R.drawable.user1, "abc12**", "15분전", 5, "웃긴 내용보다는 좀 더 진지한 영화.", "추천 1"));
        adapter.addItem(new ReviewItem(R.drawable.user1, "bu_t**", "17분전", 3, "연기가 부족한 느낌이 드는 배우도 있지만 전체적으로 재밌다.", "추천 0"));
        adapter.addItem(new ReviewItem(R.drawable.user1, "em_r2**", "20분전", 4, "말이 필요없어요.", "추천 0"));


        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Toast.makeText(AllReviewActivity.this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
