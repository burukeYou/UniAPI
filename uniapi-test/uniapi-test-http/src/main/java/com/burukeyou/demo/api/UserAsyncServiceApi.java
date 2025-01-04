package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.HttpCallCfg;
import com.burukeyou.uniapi.http.annotation.HttpRequestCfg;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.HttpFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@UserHttpApi
public interface UserAsyncServiceApi {

    @GetHttpInterface("/user-web/del06")
    BaseRsp<String> del06(@QueryPar("name") String name);

    @PostHttpInterface(path = "/user-web/del04")
    @HttpCallCfg(readTimeout = 2000)
    CompletableFuture<String> del04();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpCallCfg(readTimeout = 2000)
    String del041();

    @PostHttpInterface(path = "/user-web/del04")
    CompletableFuture<String> del042();

    @PostHttpInterface(path = "/user-web/del04")
    Future<String> del043();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpRequestCfg(async = true)
    void del044();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpRequestCfg(async = true)
    @HttpCallCfg(readTimeout = 2000)
    HttpFuture<BaseRsp<String>> del045();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpRequestCfg(async = true)
    CompletableFuture<HttpResponse<BaseRsp<String>>> del046();

    @PostHttpInterface(path = "/user-web/del04",async = true)
    //@HttpRequestCfg(async = true)
    HttpFuture<HttpResponse<BaseRsp<String>>> del047();

    @GetHttpInterface(path = "/user-web/del07")
    CompletableFuture<HttpResponse<String>> del07(@QueryPar("name") String name);

    @GetHttpInterface(path = "/user-web/del07")
    @HttpRequestCfg(async = true)
    HttpFuture<BaseRsp<String>> del071(@QueryPar("name") String name);
}
