package com.onedelay.mymovie.data.api

import android.content.Context
import android.net.ConnectivityManager

object RequestProvider {
    internal val TAG = "Onedelay"

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null
    }
}
