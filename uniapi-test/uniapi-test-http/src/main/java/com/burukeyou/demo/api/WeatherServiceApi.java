package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.MTuanHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.param.HeaderPar;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

@MTuanHttpApi
public interface WeatherServiceApi {

    /**
     *  根据appId和公钥获取令牌
     */
    @PostHttpInterface(path = "/mtuan/weather/getToken")
    HttpResponse<String> getToken(@HeaderPar("appId") String appId, @HeaderPar("publicKey")String publicKey);

    /**
     * 根据城市名获取天气情况
     */
    @GetHttpInterface("/mtuan/weather/getCityByName")
    BaseRsp<String> getCityWeather(@QueryPar("city") String cityName);

}
