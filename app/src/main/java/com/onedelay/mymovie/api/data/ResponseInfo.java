package com.onedelay.mymovie.api.data;


public class ResponseInfo<T> {
    private String message;
    private int code;
    private String resultType;
    private T result;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getResultType() {
        return resultType;
    }

    public T getResult() {
        return result;
    }
}
