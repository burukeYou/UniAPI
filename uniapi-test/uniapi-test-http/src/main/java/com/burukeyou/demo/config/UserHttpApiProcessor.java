package com.burukeyou.demo.config;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserHttpApiProcessor implements HttpApiProcessor<UserHttpApi> {

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Override
    public HttpMetadata postBeforeHttpMetadata(HttpMetadata httpMetadata,
                                               HttpApiMethodInvocation<UserHttpApi> methodInvocation) {

        return httpMetadata;
    }

    @Override
    public HttpResponse<?> postSendingHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata, HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        log.info("请求体: {}",httpMetadata.toHttpProtocol());
        HttpResponse<?> rsp = httpSender.sendHttpRequest(httpMetadata);
        log.info("请求结果: {}", rsp.toHttpProtocol());
        return rsp;
    }

    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, HttpResponse<?> rsp, HttpMetadata httpMetadata,HttpApiMethodInvocation<UserHttpApi> methodInvocation) {
        if (bodyResult instanceof BaseRsp){
            ((BaseRsp) bodyResult).setCode(99999);
        }
        return bodyResult;
    }


}
