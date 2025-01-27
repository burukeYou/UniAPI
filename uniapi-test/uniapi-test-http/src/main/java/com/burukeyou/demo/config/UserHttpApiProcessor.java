package com.burukeyou.demo.config;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.support.arg.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Component
public class UserHttpApiProcessor implements HttpApiProcessor<UserHttpApi> {

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Override
    public UniHttpRequest postBeforeHttpRequest(UniHttpRequest uniHttpRequest,
                                                HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        Type bodyResultType = methodInvocation.getBodyResultType();
        List<Param> methodParamList = methodInvocation.getMethodParamList();
        return uniHttpRequest;
    }

    @Override
    public UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        //log.info("请求体: {}", uniHttpRequest.toHttpProtocol());
        UniHttpResponse rsp = httpSender.sendHttpRequest(uniHttpRequest);
        //log.info("请求结果: {}", rsp.toHttpProtocol());
        return rsp;
    }

    @Override
    public void postAfterHttpResponse(Throwable exception, UniHttpRequest request, UniHttpResponse response, HttpApiMethodInvocation<UserHttpApi> methodInvocation)  {
        log.info("请求日志: {}",request.toHttpProtocol());
        HttpApiProcessor.super.postAfterHttpResponse(exception, request, response, methodInvocation);
        log.info("响应日志: {}",response.toHttpProtocol());
        log.info("=======================================");
    }

    @Override
    public String postAfterHttpResponseBodyString(String bodyString, UniHttpResponse uniHttpResponse, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        return bodyString;
    }


    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, UniHttpResponse rsp, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        if (bodyResult instanceof BaseRsp){
           // do some thing
            ((BaseRsp)bodyResult).setExt("后置处理设置");
        }
        return bodyResult;
    }
    
    @Override
    public Object postAfterMethodReturnValue(Object methodReturnValue, UniHttpResponse rsp, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        return methodReturnValue;
    }

}
