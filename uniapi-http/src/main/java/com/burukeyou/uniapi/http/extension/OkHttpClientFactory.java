package com.burukeyou.uniapi.http.extension;


import okhttp3.OkHttpClient;

/**
 *  building custom http Client
 *
 * @author  caizhihao
 */
public interface OkHttpClientFactory {

    /**
     * get OkHttpClient from factory
     *      will auto cache
     */
    OkHttpClient getOkHttpClient();

}
