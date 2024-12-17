package com.burukeyou.uniapi.http.extension.client;


/**
 *  global okhttp client factory
 *          if you want to use custom okhttp client，
 *          you can implement it and register to spring context
 */
public interface GlobalOkHttpClientFactory extends OkHttpClientFactory {


}
