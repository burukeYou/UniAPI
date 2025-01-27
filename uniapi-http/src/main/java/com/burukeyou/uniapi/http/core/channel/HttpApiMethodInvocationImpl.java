package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.support.map.IMap;
import com.burukeyou.uniapi.support.map.ValueObjectHashMap;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Future;

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
    public List<Param> getMethodParamList() {
        Method method = methodInvocation.getMethod();
        Object[] args = methodInvocation.getArguments();
        return new MethodArgList(method, args);
    }

    @Override
    public Type getBodyResultType() {
        Method method = methodInvocation.getMethod();
        Class<?> currentClass = method.getReturnType();
        Type currentType = method.getGenericReturnType();
        if (Future.class.isAssignableFrom(currentClass)){
            currentType =  ((ParameterizedType) currentType).getActualTypeArguments()[0];
            currentClass = getTypeClass(currentType);
        }
        if (HttpResponse.class.isAssignableFrom(currentClass)) {
            currentType =  ((ParameterizedType) currentType).getActualTypeArguments()[0];
        }
        return currentType;
    }

    @Override
    public String getMethodAbsoluteName() {
        Method method = methodInvocation.getMethod();
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private Class<?> getTypeClass(Type type){
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getTypeClass(((ParameterizedType) type).getRawType());
        }
        throw new IllegalStateException("can not get class for type " + type);
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
