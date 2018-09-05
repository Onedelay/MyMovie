package com.onedelay.mymovie.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.appbar_photo_view));
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        PhotoView view = findViewById(R.id.imageView);
        Glide.with(this).load(getIntent().getStringExtra(Constants.KEY_IMAGE_URL)).into(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
