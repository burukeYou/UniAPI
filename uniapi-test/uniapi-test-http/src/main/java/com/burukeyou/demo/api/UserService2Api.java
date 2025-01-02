package com.burukeyou.demo.api;

import java.util.concurrent.CompletableFuture;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.HttpCallCfg;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

@UserHttpApi
public interface UserService2Api {

    @GetHttpInterface("/user-web/del06")
    BaseRsp<String> del06(@QueryPar("name") String name);

    @PostHttpInterface(path = "/user-web/del04")
    @HttpCallCfg(readTimeout = 2000)
    CompletableFuture<String> del04();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpCallCfg(readTimeout = 2000)
    String del041();

    @GetHttpInterface(path = "/user-web/del07")
    CompletableFuture<HttpResponse<String>> del07(@QueryPar("name") String name);
}
