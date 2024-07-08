package com.burukeyou.demo.config;

import com.burukeyou.demo.annotation.MTuanHttpApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class MTuanChannelHandler implements HttpApiProcessor<MTuanHttpApi> {

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Override
    public HttpMetadata postBeforeHttpMetadata(HttpMetadata httpMetadata,
                                               HttpApiMethodInvocation<MTuanHttpApi> methodInvocation) {
        //
        MTuanHttpApi api = methodInvocation.getProxyApiAnnotation();
        HttpInterface proxyInterface = methodInvocation.getProxyInterface();
        //httpMetadata.updateUrl(url);

    /*    if (httpMetadata.getBody() != null){
            String bodyJson = httpMetadata.getBody().toString();
            JSONObject jsonObject = JSON.parseObject(bodyJson);
            jsonObject.putIfAbsent("appId",appId);
            httpMetadata.updateJsonBody(jsonObject.toJSONString());
        }*/


        return httpMetadata;
    }

    @Override
    public HttpResponse<?> postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata) {
        //log.info("发送请求 \n 参数：{} ",httpMetadata.toString());
        HttpResponse<?> rsp = httpSender.sendHttpRequest(httpMetadata);
        log.info("请求结果: {}", rsp.toHttpProtocol());
        return rsp;
    }

    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, HttpResponse<?> rsp, Method method, HttpMetadata httpMetadata) {
        if (bodyResult instanceof BaseRsp){
            ((BaseRsp) bodyResult).setCode(99999);
        }
        return bodyResult;
    }


}
