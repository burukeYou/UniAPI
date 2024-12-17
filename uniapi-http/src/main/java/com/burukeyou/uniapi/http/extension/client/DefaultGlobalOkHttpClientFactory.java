package com.burukeyou.uniapi.http.extension.client;


import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 *  the default implement of global okhttp client factory
 *
 */
public class DefaultGlobalOkHttpClientFactory implements GlobalOkHttpClientFactory {

    private  final OkHttpClient client;

    public DefaultGlobalOkHttpClientFactory() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }
}
