package com.burukeyou.demo.api;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.retry.HttpRetry;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

@UserHttpApi
public interface  RetryServiceAPI {

    @GetHttpInterface("/retry-web/rt01")
    @HttpRetry(maxAttempts = 3,delay = 2000)
    BaseRsp<String > rto1(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/rt02")
    @HttpRetry(maxAttempts = 5,delay = 1000, retryStrategy = Rt02HttpRetryStrategy.class)
    BaseRsp<String > rto2(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/404")
    @HttpRetry(maxAttempts = 3,delay = 1000, include = SendHttpRequestException.class)
    BaseRsp<String > rto3(@QueryPar("key") String value);

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
}
