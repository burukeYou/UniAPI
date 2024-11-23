package com.burukeyou.uniapi.http.core.http.request;

import okhttp3.Request;

public class OkHttpRequest implements UniHttpRequest {

    private Request request;

    public OkHttpRequest(Request request) {
        this.request = request;
    }
}
