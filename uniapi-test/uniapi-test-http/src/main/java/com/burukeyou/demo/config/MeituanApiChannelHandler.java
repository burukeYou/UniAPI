package com.burukeyou.demo.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.burukeyou.demo.annotation.MetuanDataApi;
import com.burukeyou.uniapi.annotation.request.HttpInterface;
import com.burukeyou.uniapi.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.core.channel.HttpSender;
import com.burukeyou.uniapi.core.request.HttpMetadata;
import com.burukeyou.uniapi.core.response.HttpResponse;
import com.burukeyou.uniapi.extension.HttpApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class MeituanApiChannelHandler implements HttpApiProcessor<MetuanDataApi> {

    @Value("${channel.meituan.appId}")
    private String appId;

    @Override
    public HttpMetadata postBefore(HttpMetadata httpMetadata,
                                   HttpApiMethodInvocation<MetuanDataApi> methodInvocation) {
        //
        MetuanDataApi api = methodInvocation.getProxyApiAnnotation();
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
    public HttpResponse postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata) {
        log.info("发送请求 \n 参数：{} ",JSON.toJSONString(httpMetadata, SerializerFeature.PrettyFormat));
        return httpSender.sendHttpRequest(httpMetadata);
    }

    @Override
    public Object postAfter(HttpResponse rsp, Method method, HttpMetadata httpMetadata) {
        Object responseObj = rsp.getReturnObj();



        return responseObj;
    }
}
