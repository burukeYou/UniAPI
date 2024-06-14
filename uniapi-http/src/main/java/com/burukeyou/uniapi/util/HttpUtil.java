package com.burukeyou.uniapi.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author  caizhihao
 */
@Slf4j
public class HttpUtil {

    private HttpUtil(){}

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
            .build();

    public static OkHttpClient getHttpClient(){
        return okHttpClient;
    }




    public static OkHttpClient getClient(String url) {
        boolean https = isHttps(url);
        OkHttpClient okHttpClient;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (https) {
            okHttpClient = builder.connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                    .build();
        } else {
            okHttpClient = builder.readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                    .build();
        }
        return okHttpClient;
    }

    public static boolean isHttps(String url) {
        return url.startsWith("https");
    }
}
