package com.burukeyou.uniapi.http.core.channel;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.support.map.IMap;
import com.burukeyou.uniapi.support.map.ValueObjectHashMap;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author  caihzihao
 */
@Setter
public class HttpApiMethodInvocationImpl implements HttpApiMethodInvocation<Annotation> {


    private Annotation proxyApiAnnotation;
    private HttpInterface proxyInterface;
    private Class<?> proxyClass;

    private MethodInvocation methodInvocation;

    /**
     *  Ext Properties,Parameter passing
     */
    private transient final IMap<String,Object> attachments = new ValueObjectHashMap<>();


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
    public IMap<String,Object> getAttachment() {
        return attachments;
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
