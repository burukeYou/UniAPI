package com.burukeyou.uniapi.http.core.proxy;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.DefaultHttpApiInvoker;
import com.burukeyou.uniapi.core.proxy.AbstractAnnotationInvokeProxy;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

public class HttpApiAnnotationProxy  extends AbstractAnnotationInvokeProxy<HttpApiAnnotationMeta> {

    public HttpApiAnnotationProxy() {
    }

    public HttpApiAnnotationProxy(HttpApiAnnotationMeta annotationMeta) {
        super(annotationMeta);
    }

    @Override
    public Object invoke(Class<?> targetClass,MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        if (annotationMeta.getHttpApi() != null){
            HttpInterface httpInterface = AnnotatedElementUtils.getMergedAnnotation(method, HttpInterface.class);
            if (httpInterface != null){
                return new DefaultHttpApiInvoker(annotationMeta,targetClass,httpInterface,methodInvocation).invoke();
            }
        }

        return null;
    }
}
