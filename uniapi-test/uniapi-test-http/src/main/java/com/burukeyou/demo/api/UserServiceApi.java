package com.burukeyou.demo.api;


import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.Add4DTO;
import com.burukeyou.demo.entity.Add6DTO;
import com.burukeyou.demo.entity.Add9DTO;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.demo.entity.U2DTO;
import com.burukeyou.uniapi.http.annotation.HttpCallCfg;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.annotation.param.BodyBinaryPar;
import com.burukeyou.uniapi.http.annotation.param.BodyFormPar;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.annotation.param.BodyMultiPartPar;
import com.burukeyou.uniapi.http.annotation.param.ComposePar;
import com.burukeyou.uniapi.http.annotation.param.CookiePar;
import com.burukeyou.uniapi.http.annotation.param.HeaderPar;
import com.burukeyou.uniapi.http.annotation.param.PathPar;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.DeleteHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PutHttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.HttpFile;
import org.springframework.http.MediaType;

@UserHttpApi
public interface UserServiceApi {

    @GetHttpInterface("/user-web/add")
    BaseRsp<String> add(@QueryPar("name") String name);

    @DeleteHttpInterface("/user-web/add2")
    BaseRsp<String> add2(@ModelBinding @QueryPar U2DTO req);

    @GetHttpInterface("/user-web/add3")
    BaseRsp<String> add3(@QueryPar("name") String name,
                         @HeaderPar("token") String token);

    @PostHttpInterface("/user-web/add4")
    BaseRsp<Add4DTO> add4(@ModelBinding @BodyJsonPar Add4DTO req);

    @PostHttpInterface("/user-web/add4")
    BaseRsp<Add4DTO> add42(@BodyJsonPar Add4DTO req,
                          @BodyJsonPar("level") Integer level,
                          @BodyJsonPar("sex") String sex,
                          @BodyJsonPar("$.bbq.kk.age") Integer age);

    @PostHttpInterface("/user-web/add41")
    BaseRsp<String> add41(@BodyJsonPar String req);

    @GetHttpInterface("/user-web/add5/{userId}")
    BaseRsp<String> add5(@PathPar("userId") String name);

    @PutHttpInterface(path = "/user-web/add6",contentType = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    HttpResponse<BaseRsp<Add4DTO>> add6(@ComposePar Add6DTO req);

    // 支持： InputStream、File、MultipartFile等类型
    @PostHttpInterface("/user-web/add7")
    BaseRsp<String> add7(@BodyBinaryPar File file);

    @PostHttpInterface(path = "/user-web/add8")
    BaseRsp<String> add8(@BodyFormPar Add4DTO file);

    @PostHttpInterface("/user-web/add9")
    BaseRsp<String> add9(@BodyMultiPartPar("reqNo") String value,@BodyMultiPartPar Add9DTO req);

    @PostHttpInterface("/user-web/add9")
    BaseRsp<String> add91(@BodyMultiPartPar("reqNo") String value,
                          @BodyMultiPartPar(value = "userImg",fileName = "牛逼.pdf") byte[] file,
                          @BodyMultiPartPar(value = "logoImg",fileName = "和.mp4") InputStream logoImg,
                          @BodyMultiPartPar(value = "logoImg",fileName = "娃啊.xlsx") File file2);

    @PostHttpInterface("/user-web/add9")
    BaseRsp<String> add92(@BodyMultiPartPar("reqNo") String value,
                          @BodyMultiPartPar(value = "userImg",fileName = "牛逼.pdf") byte[] file,
                          @BodyMultiPartPar(value = "logoImg",fileName = "和.mp4") InputStream logoImg,
                          @BodyMultiPartPar(value = "logoImg",fileName = "娃啊.xlsx") File file2,
                          @BodyMultiPartPar(value = "xxx1") HttpFile httpFile);

    @PostHttpInterface("/user-web/add9")
    BaseRsp<String> add93(
                        @BodyMultiPartPar(value = "logoImg",fileName = "和.mp4") List<InputStream> files,
                        @BodyMultiPartPar(value = "userImg") File[] files2,
                        @BodyMultiPartPar(value = "bbqImg") List<HttpFile> files3,
                        @BodyMultiPartPar(value = "sbImg",fileName = "娃啊.xlsx") byte[] files4,
                        @BodyMultiPartPar(value = "xxx1") List<byte[]> files5);

    @PostHttpInterface("/user-web/add10")
    HttpResponse<byte[]> add10();

    @PostHttpInterface("/user-web/add10")
    HttpResponse<UserServiceApi> add1011();


    @PostHttpInterface("/user-web/add10")
    byte[] add101();

    @PostHttpInterface("/user-web/add10")
    @ResponseFile(saveDir = "/Users/burukeyou/dev/tmp/tmp7/{YYYYMMDD}")
    File add11();

    @PostHttpInterface("/user-web/add10")
    @ResponseFile(saveDir = "/Users/burukeyou/dev/tmp/tmp7/{YYYYMMDD}")
    HttpResponse<File> add111();


    @PostHttpInterface("/user-web/add10")
    InputStream add112();

    @PostHttpInterface("/user-web/add10")
    HttpResponse<InputStream> add113();

    @PostHttpInterface("/user-web/add10")
    HttpResponse<InputStream> add114();

    @PostHttpInterface("/user-web/add10")
    HttpResponse<byte[]> add115();

    @PostHttpInterface("/user-web/add10")
    HttpResponse<File> add116();

    // 保存路径savePath可以是具体路径，也可以是具体目录，如果是具体目录则默认文件名是下载的文件名
    @PostHttpInterface("/user-web/add10")
    File add12(@ResponseFile(overwrite = false) String savePath);

    @PostHttpInterface(path = "/user-web/update",
            headers = {"clientType=sys-app","userId=99","aaa=${server.port}"},
            params = {"name=周杰伦","age=1","appId=${server.port}"},
            paramStr = "a=1&b=2&c=3&d=哈哈&e=%E7%89%9B%E9%80%BC"
    )
    BaseRsp<String> update0();

    @PostHttpInterface("/user-web/update1")
    BaseRsp<String> update1(@CookiePar String cookie);

    @PostHttpInterface("/user-web/update1")
    HttpResponse<Add4DTO> update2(@CookiePar String cookie);

    @PostHttpInterface("/user-web/update3")
    BaseRsp<String> update3(@QueryPar("ids") List<Integer> params);

    @GetHttpInterface("/user-web/del")
    String del01(@QueryPar("name") String name);

    @GetHttpInterface("/user-web/del2")
    String del02();

    @PostHttpInterface(path = "/user-web/del03")
    void del03();

    @PostHttpInterface(path = "/user-web/del04")
    @HttpCallCfg(readTimeout = 3000)
    String del04();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    String del05();
}
