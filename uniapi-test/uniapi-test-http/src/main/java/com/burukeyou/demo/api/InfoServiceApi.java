package com.burukeyou.demo.api;

import com.burukeyou.demo.config.InfoChannelSSLFactory;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;

@HttpApi(
        //url = "https://localhost:8999",
        url = "https://10.94.22.74:8999",
        httpClient = InfoChannelSSLFactory.class
)
public interface InfoServiceApi {

    @GetHttpInterface("/info-web/get01")
    BaseRsp<String> get01(String name);
}
