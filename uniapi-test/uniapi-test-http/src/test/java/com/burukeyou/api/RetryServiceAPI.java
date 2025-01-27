package com.burukeyou.api;

import com.burukeyou.PolicyData;
import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.FilterProcessor;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.annotation.HttpRetry;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.retry.policy.AllResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.BodyResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryInterceptorPolicy;

import java.util.concurrent.CompletableFuture;

@UserHttpApi
public interface  RetryServiceAPI {

    @GetHttpInterface("/retry-web/rt01")
    @HttpRetry(maxAttempts = 3,delay = 2000)
    BaseRsp<String > get1(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/rt02")
    @HttpRetry(maxAttempts = 5,delay = 1000)
    BaseRsp<String > get2(@QueryPar("key") String value);

    @GetHttpInterface("/retry-web/404")
    @HttpRetry(maxAttempts = 3,delay = 1000, include = SendHttpRequestException.class)
    BaseRsp<String > get3(@QueryPar("key") String value);


    @GetHttpInterface(value = "/retry-web/rt02")
    // @HttpRetry(maxAttempts = 8,delay = 2000)
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4)
    CompletableFuture<BaseRsp<String>> get4(@QueryPar("key") String value, AllResultPolicy<BaseRsp<String>> policy);


    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4,policy = PolicyData.Policy1.class)
    CompletableFuture<BaseRsp<String>> get40(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4,policy = PolicyData.Policy2.class)
    CompletableFuture<BaseRsp<String>> get41(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4,policy = PolicyData.Policy3.class)
    CompletableFuture<BaseRsp<String>> get42(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4)
    CompletableFuture<BaseRsp<String>> get43(@QueryPar("key") String value, BodyResultPolicy<BaseRsp<String>> policy);


    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4)
    CompletableFuture<BaseRsp<String>> get44(@QueryPar("key") String value, AllResultPolicy<BaseRsp<String>> policy);


    @GetHttpInterface(value = "/retry-web/rt03")
    @HttpFastRetry(briefErrorLog = true,maxAttempts = 4)
    CompletableFuture<BaseRsp<String>> get45(@QueryPar("key") String value, HttpRetryInterceptorPolicy<BaseRsp<String>> policy);


    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000)
    @FilterProcessor(ignoreAll = true)
    BaseRsp<String> get4Sync(@QueryPar("key") String value);

    @GetHttpInterface(value = "/retry-web/rt02")
    //@HttpRetry(maxAttempts = 8,delay = 2000)
    @HttpFastRetry(briefErrorLog = true)
    //@FilterProcessor(ignoreAll = true)
    BaseRsp<String> get4SyncFast(@QueryPar("key") String value);


    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000)
    @FilterProcessor(ignoreAll = true)
    CompletableFuture<BaseRsp<String>> get4Many(@QueryPar("key") String value);


    @GetHttpInterface(value = "/retry-web/rt02")
    @HttpRetry(maxAttempts = 8,delay = 2000)
    @FilterProcessor(ignoreAll = true)
    CompletableFuture<BaseRsp<String>> get4ManyFast(@QueryPar("key") String value);

}
