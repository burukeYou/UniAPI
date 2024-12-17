package com.burukeyou.uniapi.http.extension.client;


import okhttp3.OkHttpClient;

/**
 *  building custom http Client
 *
 * @author  caizhihao
 */
public interface HttpClientFactory<T> {


    /**
     * get HtpClient from factory
     *          Will not automatically cache, request to ensure singleton to prevent duplicate creation
     *          Currently, only returns are supported {@link OkHttpClient}
     *
     */
    T getHttpClient();

}
