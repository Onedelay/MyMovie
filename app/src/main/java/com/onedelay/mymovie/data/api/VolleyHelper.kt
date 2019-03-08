package com.onedelay.mymovie.data.api

import com.android.volley.RequestQueue

object VolleyHelper {
    var requestQueue: RequestQueue? = null

    var host = "http://boostcourse-appapi.connect.or.kr"
    var port = 10000

    fun requestServer(request: GsonRequest<*>) {
        request.setShouldCache(false)
        if (requestQueue != null) {
            requestQueue!!.add(request)
        }
    }
}
