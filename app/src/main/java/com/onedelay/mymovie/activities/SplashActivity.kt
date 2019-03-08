package com.onedelay.mymovie.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler.postDelayed({
            val intent = Intent(applicationContext, MovieListActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000L)

    }
}
