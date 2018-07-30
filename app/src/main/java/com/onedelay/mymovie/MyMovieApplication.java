package com.onedelay.mymovie;

import android.app.Application;

import com.android.volley.toolbox.Volley;
import com.onedelay.mymovie.api.VolleyHelper;

public class MyMovieApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyHelper.requestQueue = Volley.newRequestQueue(getBaseContext());
    }
}
