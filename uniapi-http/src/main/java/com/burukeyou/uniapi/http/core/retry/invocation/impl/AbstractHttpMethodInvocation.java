package com.burukeyou.uniapi.http.core.retry.invocation.impl;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.support.map.IMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public abstract class AbstractHttpMethodInvocation implements HttpApiMethodInvocation<Annotation> {

    protected final HttpApiMethodInvocation<Annotation> httpMethodInvocation;

    protected AbstractHttpMethodInvocation(HttpApiMethodInvocation<Annotation> methodInvocation) {
        this.httpMethodInvocation = methodInvocation;
    }

    @Override
    public Annotation getProxyApiAnnotation() {
        return httpMethodInvocation.getProxyApiAnnotation();
    }

    @Override
    public HttpInterface getProxyInterface() {
        return httpMethodInvocation.getProxyInterface();
    }

    @Override
    public Class<?> getProxyClass() {
        return httpMethodInvocation.getProxyClass();
    }

    @Override
    public IMap<String, Object> getAttachment() {
        return httpMethodInvocation.getAttachment();
    }

    @Override
    public List<Param> getMethodParamList() {
        return httpMethodInvocation.getMethodParamList();
    }

    @Override
    public Type getBodyResultType() {
        return httpMethodInvocation.getBodyResultType();
    }



    @Override
    public String getMethodAbsoluteName() {
        Method method = getMethod();
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    @Override
    public Method getMethod() {
        return httpMethodInvocation.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return httpMethodInvocation.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        return httpMethodInvocation.proceed();
    }

    @Override
    public Object getThis() {
        return httpMethodInvocation.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return httpMethodInvocation.getStaticPart();
    }


}
