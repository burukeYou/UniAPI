package com.burukeyou.demo.api;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

@HttpApi(url = "http://127.0.0.1:8999")
public interface SimpleServiceApi {

    @GetHttpInterface("/user-web/add")
    String add(@QueryPar("name") String name);


    @GetHttpInterface(path = "/user-web/add",processor = MyHttpProcessor.class)
    String add2(@QueryPar("name") String name);


    @Slf4j
    class MyHttpProcessor implements HttpApiProcessor<Annotation> {
         @Override
         public HttpResponse<?> postSendingHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata, HttpApiMethodInvocation<Annotation> methodInvocation) {
             HttpResponse<?> rsp = HttpApiProcessor.super.postSendingHttpRequest(httpSender, httpMetadata, methodInvocation);
             log.info("请求日志 {}",rsp.toHttpProtocol());
             return rsp;
         }
     }

}
