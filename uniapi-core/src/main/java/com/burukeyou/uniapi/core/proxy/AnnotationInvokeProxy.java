package com.burukeyou.uniapi.core.proxy;

import org.aopalliance.intercept.MethodInvocation;


/**
 * @author caizhihao
 */
public interface AnnotationInvokeProxy {

    Object invoke(Class<?> targetClass, MethodInvocation methodInvocation);

}
