package com.burukeyou.uniapi.extension;

import com.burukeyou.uniapi.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.core.channel.HttpSender;
import com.burukeyou.uniapi.core.request.HttpMetadata;
import com.burukeyou.uniapi.core.response.HttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author caizhihao
 * @param <T>
 */
public interface HttpApiProcessor<T extends Annotation> {

    default HttpMetadata postBefore(HttpMetadata httpMetadata, HttpApiMethodInvocation<T> methodInvocation){
        return httpMetadata;
    }

    default HttpResponse postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata){
        return httpSender.sendHttpRequest(httpMetadata);
    }

    default Object postAfter(HttpResponse rsp,Method method,HttpMetadata httpMetadata){
        if (HttpResponse.class.isAssignableFrom(method.getReturnType())){
            return rsp;
        }
        return rsp.getReturnObj();
    }

}
