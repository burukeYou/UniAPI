package com.burukeyou.demo.api;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;

@HttpApi(url = "http://127.0.0.1:8999")
public interface SimpleServiceApi {

    @GetHttpInterface("/user-web/add")
    String add(@QueryPar("name") String name);


    @GetHttpInterface(path = "/user-web/add",processor = MyHttpProcessor.class)
    String add2(@QueryPar("name") String name);


    @Slf4j
    class MyHttpProcessor implements HttpApiProcessor<Annotation> {
         @Override
         public UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<Annotation> methodInvocation) {
             UniHttpResponse rsp = HttpApiProcessor.super.postSendingHttpRequest(httpSender, uniHttpRequest, methodInvocation);
             log.info("请求日志 {}",rsp.getBodyToString());
             return rsp;
         }
     }

}
