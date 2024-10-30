package com.burukeyou.uniapi.http.extension;


import okhttp3.OkHttpClient;

/**
 *  building custom okHttp Client
 *
 * @author  caizhihao
 */
public interface OkHttpClientFactory extends HttpClientFactory<OkHttpClient> {

    /**
     * get OkHtpClient from factory
     *         Will not automatically cache, request to ensure singleton to prevent duplicate creation
     */
    OkHttpClient getHttpClient();

}
