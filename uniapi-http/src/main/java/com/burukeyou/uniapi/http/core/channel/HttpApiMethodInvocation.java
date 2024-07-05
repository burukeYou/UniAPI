package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

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

}
