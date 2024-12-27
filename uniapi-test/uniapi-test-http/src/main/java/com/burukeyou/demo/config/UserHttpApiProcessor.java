package com.burukeyou.demo.config;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserHttpApiProcessor implements HttpApiProcessor<UserHttpApi> {

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Override
    public UniHttpRequest postBeforeHttpMetadata(UniHttpRequest uniHttpRequest,
                                                 HttpApiMethodInvocation<UserHttpApi> methodInvocation) {

        return uniHttpRequest;
    }

    @Override
    public UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        log.info("请求体: {}", uniHttpRequest.toHttpProtocol());
        UniHttpResponse rsp = httpSender.sendHttpRequest(uniHttpRequest);
        log.info("请求结果: {}", rsp.toHttpProtocol());
        return rsp;
    }

    @Override
    public String postAfterHttpResponseBodyString(String bodyString, UniHttpResponse uniHttpResponse, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        return bodyString;
    }


    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, UniHttpResponse rsp, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        if (bodyResult instanceof BaseRsp){
            ((BaseRsp) bodyResult).setCode(99999);
        }
        return bodyResult;
    }
    
    @Override
    public Object postAfterMethodReturnValue(Object methodReturnValue, UniHttpResponse rsp, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        return HttpApiProcessor.super.postAfterMethodReturnValue(methodReturnValue, rsp, methodInvocation);
    }

}
