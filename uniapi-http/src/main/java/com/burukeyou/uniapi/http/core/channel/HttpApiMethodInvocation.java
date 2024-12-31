package com.burukeyou.uniapi.http.core.channel;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.support.map.IMap;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author  caihzihao
 */
public interface HttpApiMethodInvocation<T extends Annotation> extends MethodInvocation {

    /**
     * Obtain HttpAPI annotations for the proxy
     */
    T getProxyApiAnnotation();

    /**
     * Proxy interface
     */
    HttpInterface getProxyInterface();

    /**
     * The specific class being represented
     */
    Class<?> getProxyClass();

    /**
     * get method invocation attachment map
     * @return          all attachment param
     */
    IMap<String,Object> getAttachment();

}
