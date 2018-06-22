package com.onedelay.mymovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onedelay.mymovie.utils.DividerItemDecorator;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.ReviewAdapter;
import com.onedelay.mymovie.ReviewData;
import com.onedelay.mymovie.ReviewItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageButton thumbUpBtn;
    private ImageButton thumbDownBtn;
    private TextView likeCountView;
    private TextView hateCountView;
    private RecyclerView recyclerView;

    private ReviewAdapter adapter;

    private boolean thumbsUpState = false;
    private boolean thumbsDownState = false;

    private int likeCount;
    private int hateCount;

    private ArrayList<ReviewItem> mainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        likeCountView = findViewById(R.id.thumb_up_count_view);
        hateCountView = findViewById(R.id.thumb_down_count_view);

        recyclerView = findViewById(R.id.review_list);

        adapter = new ReviewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        // 구분선
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        mainList = new ArrayList<>();
        mainList.add(new ReviewItem(R.drawable.user1, "kym71**", "10분전", 4, "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요. ", "추천 0"));
        mainList.add(new ReviewItem(R.drawable.user1, "su_m**", "10분전", 5, "완전 재밌고 흥미진진하네요! 다음에 또 보고싶습니다. 배우들의 연기력에도 감탄했어요. 친구들한테도 추천할래요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", "추천 0"));
        adapter.addItems(mainList);

        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Toast.makeText(MainActivity.this, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        likeCount = Integer.parseInt(likeCountView.getText().toString());
        hateCount = Integer.parseInt(hateCountView.getText().toString());

        thumbUpBtn = findViewById(R.id.btn_thumb_up);
        thumbUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeClick();
            }
        });

        thumbDownBtn = findViewById(R.id.btn_thumb_down);
        thumbDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hateClick();
            }
        });

        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteReviewActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.btn_all_see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(putMainList(mainList));
            }
        });

        findViewById(R.id.btn_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "페이스북 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_kakao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "카카오톡 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_ticketing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "예매하기 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeClick() {
        if (!thumbsUpState && !thumbsDownState) {
            thumbsUpState = true;
            likeCount++;
            thumbUpBtn.setSelected(true);
        } else if (thumbsUpState) {
            thumbsUpState = false;
            likeCount--;
            thumbUpBtn.setSelected(false);
        } else {
            thumbsUpState = true;
            thumbsDownState = false;
            likeCount++;
            hateCount--;
            thumbUpBtn.setSelected(true);
            thumbDownBtn.setSelected(false);
        }

        likeCountView.setText(likeCount + "");
        hateCountView.setText(hateCount + "");
    }

    public void hateClick() {
        if (!thumbsUpState && !thumbsDownState) {
            thumbsDownState = true;
            hateCount++;
            thumbDownBtn.setSelected(true);
        } else if (thumbsDownState) {
            thumbsDownState = false;
            hateCount--;
            thumbDownBtn.setSelected(false);
        } else {
            thumbsDownState = true;
            thumbsUpState = false;
            hateCount++;
            likeCount--;
            thumbDownBtn.setSelected(true);
            thumbUpBtn.setSelected(false);
        }

        likeCountView.setText(likeCount + "");
        hateCountView.setText(hateCount + "");
    }

    public Intent putMainList(ArrayList<ReviewItem> items){
        Intent intent = new Intent(MainActivity.this, AllReviewActivity.class);
        ArrayList<ReviewData> mainParcelList = new ArrayList<>();

        for(ReviewItem item : items){
            mainParcelList.add(new ReviewData(item.getImage(), item.getId(), item.getTime(), item.getRating(), item.getContent(), item.getRecommend()));
        }

        intent.putParcelableArrayListExtra("mainList", mainParcelList);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode != Activity.RESULT_CANCELED) {
            float rating = data.getFloatExtra("rating", 0.0f);
            String content = data.getStringExtra("content");

            mainList.remove(0);
            mainList.add(new ReviewItem(R.drawable.user1, "main", "방금", rating, content, "추천 0"));
            adapter.addItems(mainList);
            adapter.notifyDataSetChanged();
        }
    }
}
