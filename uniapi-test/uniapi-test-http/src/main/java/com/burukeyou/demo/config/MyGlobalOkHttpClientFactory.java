package com.burukeyou.demo.config;

import com.burukeyou.uniapi.http.extension.client.GlobalOkHttpClientFactory;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

//@Component
public class MyGlobalOkHttpClientFactory implements GlobalOkHttpClientFactory {
    private  final OkHttpClient client;

    public MyGlobalOkHttpClientFactory() {
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
