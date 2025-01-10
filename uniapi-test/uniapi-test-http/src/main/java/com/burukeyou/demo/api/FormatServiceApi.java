package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.json.StuDTO;
import com.burukeyou.demo.entity.json.StuReq;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
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
    @JsonPathMapping("$.son.detail")
    StuDTO get04();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    StuDTO get05();

    @PostHttpInterface(path = "/xxxx")
    void get06(@BodyJsonPar StuReq req);
}
