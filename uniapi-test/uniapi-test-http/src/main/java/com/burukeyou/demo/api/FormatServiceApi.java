package com.burukeyou.demo.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;

@UserHttpApi
public interface FormatServiceApi {

    @GetHttpInterface("/user-web/format/get02")
    UserXmlDTO get02();



}
