package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

/**
 * @author  caihzihao
 */
public interface HttpApiMethodInvocation<T extends Annotation> extends MethodInvocation {

    T getProxyApiAnnotation();

    HttpInterface getProxyInterface();

    Class<?> getProxyClass();

}
