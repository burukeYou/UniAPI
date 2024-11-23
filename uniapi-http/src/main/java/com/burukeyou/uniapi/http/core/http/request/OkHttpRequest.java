package com.burukeyou.uniapi.http.core.http.request;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import okhttp3.Request;

public class OkHttpRequest implements UniHttpRequest {

    private static final long serialVersionUID = 857619446084057392L;

    private Request request;

    public OkHttpRequest(Request request) {
        this.request = request;
    }
}
