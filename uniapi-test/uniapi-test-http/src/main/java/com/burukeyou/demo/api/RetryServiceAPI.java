package com.burukeyou.demo.api;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.FilterProcessor;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.annotation.HttpRetry;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import lombok.extern.slf4j.Slf4j;

@UserHttpApi
public interface  RetryServiceAPI {

    @GetHttpInterface("/retry-web/rt01")
    @HttpRetry(maxAttempts = 3,delay = 2000)
    BaseRsp<String > get1(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/rt02")
    @HttpRetry(maxAttempts = 5,delay = 1000, retryStrategy = Rt02HttpRetryStrategy.class)
    BaseRsp<String > get2(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/404")
    @HttpRetry(maxAttempts = 3,delay = 1000, include = SendHttpRequestException.class)
    BaseRsp<String > get3(@QueryPar("key") String value);

    @Slf4j
     class  Rt02HttpRetryStrategy implements HttpRetryStrategy<BaseRsp<String>> {
         @Override
         public boolean canRetry(long curRetryCount, UniHttpRequest request, UniHttpResponse response, BaseRsp<String> bodyResult, HttpApiMethodInvocation<Annotation> methodInvocation) {
              log.info("拿到结果，判断是否重试 curRetryCount:{} rsp:{}",curRetryCount, JSON.toJSONString(bodyResult));
              if (!response.isSuccessful()){
                  return true;
              }
             return bodyResult == null;
         }
     }

    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000, retryStrategy = Rt02HttpRetryStrategy.class)
    @FilterProcessor(ignoreAll = true)
    CompletableFuture<BaseRsp<String>> get4(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000, retryStrategy = Rt02HttpRetryStrategy.class)
    @FilterProcessor(ignoreAll = true)
    BaseRsp<String> get4Sync(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000, retryStrategy = Rt02HttpRetryStrategy.class,fastRetry = true)
    @FilterProcessor(ignoreAll = true)
    BaseRsp<String> get4SyncFast(@QueryPar("key") String value);


    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000, retryStrategy = Rt02HttpRetryStrategy.class)
    @FilterProcessor(ignoreAll = true)
    CompletableFuture<BaseRsp<String>> get4Many(@QueryPar("key") String value);


    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000, retryStrategy = Rt02HttpRetryStrategy.class,fastRetry = true)
    @FilterProcessor(ignoreAll = true)
    CompletableFuture<BaseRsp<String>> get4ManyFast(@QueryPar("key") String value);

}
