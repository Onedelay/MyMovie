package com.onedelay.mymovie.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.onedelay.mymovie.Constants
import com.onedelay.mymovie.R

class PhotoViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        // 앱바 제목 텍스트 변경
        val ab = supportActionBar
        if (ab != null) {
            ab.title = getString(R.string.appbar_photo_view)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeButtonEnabled(true)
        }

        val view = findViewById<PhotoView>(R.id.imageView)
        Glide.with(this).load(intent.getStringExtra(Constants.KEY_IMAGE_URL)).into(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}
