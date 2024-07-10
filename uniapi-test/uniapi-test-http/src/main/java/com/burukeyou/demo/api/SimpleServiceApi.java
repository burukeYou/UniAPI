package com.burukeyou.demo.api;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;

@HttpApi(url = "http://127.0.0.1:8999")
public interface SimpleServiceApi {

    @GetHttpInterface("/user-web/add")
    String add(@QueryPar("name") String name);


}
