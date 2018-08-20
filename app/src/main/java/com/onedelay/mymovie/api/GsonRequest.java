package com.onedelay.mymovie.api;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final TypeToken<T> token;
    private final Response.Listener<T> listener;

    public GsonRequest(int method, String url, TypeToken<T> token, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.token = token;
        this.listener = listener;
    }

    @WorkerThread
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            T result = gson.fromJson(json, token.getType());
            listener.onResponse(result);
            return Response.success(
                    result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @UiThread
    @Override
    protected void deliverResponse(T response) {
        // Do nothing
    }
}