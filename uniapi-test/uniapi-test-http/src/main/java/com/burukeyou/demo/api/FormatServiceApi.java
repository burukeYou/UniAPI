package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.json.StuDTO;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import com.burukeyou.uniapi.http.annotation.FillModel;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.annotation.param.BodyXmlPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;

@UserHttpApi
public interface FormatServiceApi {

    @GetHttpInterface("/user-web/format/get02")
    UserXmlDTO get02();

    @PostHttpInterface("/user-web/format/get03")
    void get03(@BodyXmlPar UserXmlDTO dto);

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @FillModel
    StuDTO get04();
}
