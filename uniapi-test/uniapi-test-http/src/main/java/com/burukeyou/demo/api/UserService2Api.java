package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;

@UserHttpApi
public interface UserService2Api {

    @GetHttpInterface("/user-web/del06")
    BaseRsp<String> del06(@QueryPar("name") String name);

}
