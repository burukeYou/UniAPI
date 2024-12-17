package com.burukeyou.demo.config;

import com.burukeyou.uniapi.http.extension.client.OkHttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserChannelOkHttpClientFactory implements OkHttpClientFactory {
    private  final OkHttpClient client;

    public UserChannelOkHttpClientFactory() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                .build();
        log.info("UserChannelOkHttpClientFactory client:{}",client);
    }

    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }
}
