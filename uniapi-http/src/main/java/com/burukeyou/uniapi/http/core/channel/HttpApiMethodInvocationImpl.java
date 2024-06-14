package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * @author  caihzihao
 */
@Setter
public class HttpApiMethodInvocationImpl implements HttpApiMethodInvocation<Annotation> {


    private Annotation proxyApiAnnotation;
    private HttpInterface proxyInterface;
    private Class<?> proxyClass;

    private MethodInvocation methodInvocation;

    @Override
    public Annotation getProxyApiAnnotation() {
        return proxyApiAnnotation;
    }

    @Override
    public HttpInterface getProxyInterface() {
        return proxyInterface;
    }

    @Override
    public Class<?> getProxyClass() {
        return proxyClass;
    }


    @Override
    public Method getMethod() {
        return methodInvocation.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return methodInvocation.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        return methodInvocation.proceed();
    }

    @Override
    public Object getThis() {
        return methodInvocation.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return methodInvocation.getStaticPart();
    }
}
