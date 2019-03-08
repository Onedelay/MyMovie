package com.onedelay.mymovie

import android.app.Application

import com.android.volley.toolbox.Volley
import com.onedelay.mymovie.data.api.VolleyHelper

class MyMovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VolleyHelper.requestQueue = Volley.newRequestQueue(baseContext)
    }
}
