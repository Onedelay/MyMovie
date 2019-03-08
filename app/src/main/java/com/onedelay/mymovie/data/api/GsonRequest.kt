package com.onedelay.mymovie.data.api

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

import java.io.UnsupportedEncodingException

/*
 * onResponse, onErrorResponse 에서 Toast 띄우지 말기. 현재 WorkerThread 로 지정해놓았기 때문에
 * Can't toast on a thread that has not called Looper.prepare() 라는 에러가 발생한다!
 * 여러 시도 끝에 Toast 띄우는 건 UiThread 에서 해야한다는 참된 교훈을 얻을 수 있었다.
 */
open class GsonRequest<T>(method: Int,
                     url: String,
                     private val token: TypeToken<T>,
                     private val listener: Response.Listener<T>,
                     errorListener: Response.ErrorListener) : Request<T>(method, url, errorListener) {

    private val gson = Gson()

    @WorkerThread
    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        return try {
            val json = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))
            val result = gson.fromJson<T>(json, token.type)
            listener.onResponse(result)

            Response.success(result, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }

    @UiThread
    override fun deliverResponse(response: T) {
        // Do nothing
    }
}