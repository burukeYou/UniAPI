package com.burukeyou.uniapi.queue.core.proxy;

import com.burukeyou.uniapi.queue.annotation.QueueApi;
import com.burukeyou.uniapi.queue.annotation.QueueInterface;
import com.burukeyou.uniapi.queue.core.channel.BaseQueueApiInvoker;
import com.burukeyou.uniapi.core.proxy.AbstractAnnotationInvokeProxy;
import com.burukeyou.uniapi.queue.support.QueueApiAnnotationMeta;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

public class QueueApiAnnotationProxy extends AbstractAnnotationInvokeProxy<QueueApiAnnotationMeta> {

    public QueueApiAnnotationProxy() {
    }

    public QueueApiAnnotationProxy(QueueApiAnnotationMeta annotationMeta) {
        super(annotationMeta);
    }

    @Override
    public Object invoke(Class<?> targetClass, MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        QueueApi queueApi = annotationMeta.getQueueApi();
        if (queueApi == null){
            return null;
        }

        if (AnnotatedElementUtils.hasAnnotation(method,QueueInterface.class)){
            return new BaseQueueApiInvoker(queueApi,targetClass).invoke(methodInvocation);
        }

        return null;
    }
}
