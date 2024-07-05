package com.burukeyou.demo.api.meituan;


import com.burukeyou.demo.annotation.MetuanDataApi;
import com.burukeyou.demo.entity.*;
import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.annotation.param.*;
import com.burukeyou.uniapi.http.annotation.request.DeleteHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PutHttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpBinaryResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

import java.io.File;
import java.util.List;

@MetuanDataApi
public interface MeituanApi {

    @GetHttpInterface("/user-web/add")
    BaseRsp<String> add(@QueryPar("name") String name);

    @DeleteHttpInterface("/user-web/add2")
    BaseRsp<String> add2(@QueryPar U2DTO req);

    @GetHttpInterface("/user-web/add3")
    BaseRsp<String> add3(@QueryPar("name") String name,
                         @HeaderPar("token") String token);

    @PostHttpInterface("/user-web/add4")
    BaseRsp<Add4DTO> add4(@BodyJsonPar Add4DTO req);

    @PostHttpInterface("/user-web/add41")
    BaseRsp<String> add41(@BodyJsonPar String req);

    @GetHttpInterface("/user-web/add5/{userId}")
    BaseRsp<String> add5(@PathPar("userId") String name);

    @PutHttpInterface("/user-web/add6")
    HttpResponse<BaseRsp<Add4DTO>> add6(@ComposePar Add6DTO req);

    // 支持： InputStream、File、MultipartFile等类型
    @PostHttpInterface("/user-web/add7")
    BaseRsp<String> add7(@BodyBinaryPar File file);

    @PostHttpInterface("/user-web/add8")
    BaseRsp<String> add8(@BodyFormPar Add4DTO file);

    @PostHttpInterface("/user-web/add9")
    BaseRsp<String> add9(@BodyMultiPartPar Add9DTO req);

    @PostHttpInterface("/user-web/add10")
    HttpBinaryResponse add10();

    @PostHttpInterface("/user-web/add10")
    @ResponseFile(saveDir = "/Users/burukeyou/dev/tmp/tmp7/{YYYYMMDD}")
    File add11();

    // 保存路径savePath可以是具体路径，也可以是具体目录，如果是具体目录则默认文件名是下载的文件名
    @PostHttpInterface("/user-web/add10")
    File add12(@ResponseFile String savePath);

    @PostHttpInterface(path = "/user-web/update",
            headers = {"clientType=sys-app","userId=99"},
            params = {"name=周杰伦","age:1"},
            paramStr = "a=1&b=2&c=3&d=哈哈&e=%E7%89%9B%E9%80%BC"
    )
    BaseRsp<String> update0();

    @PostHttpInterface("/user-web/update1")
    BaseRsp<String> update1(@CookiePar String cookie);

    @PostHttpInterface("/user-web/update1")
    HttpResponse<Add4DTO> update2(@CookiePar String cookie);

    @PostHttpInterface("/user-web/update3")
    BaseRsp<String> update3(@QueryPar("ids") List<Integer> params);
}
