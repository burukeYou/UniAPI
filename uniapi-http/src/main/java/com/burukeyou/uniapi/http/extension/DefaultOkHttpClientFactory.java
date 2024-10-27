package com.burukeyou.uniapi.http.extension;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DefaultOkHttpClientFactory implements OkHttpClientFactory {

    @Override
    public OkHttpClient getOkHttpClient() {
          return new OkHttpClient.Builder()
                  .readTimeout(50, TimeUnit.SECONDS)
                  .writeTimeout(50, TimeUnit.SECONDS)
                  .connectTimeout(10, TimeUnit.SECONDS)
                  .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                  .build();
    }
}
