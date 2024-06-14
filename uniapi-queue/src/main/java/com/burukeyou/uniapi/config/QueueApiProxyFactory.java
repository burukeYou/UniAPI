package com.burukeyou.uniapi.config;

import com.burukeyou.uniapi.annotation.QueueApi;
import com.burukeyou.uniapi.core.proxy.AnnotationInvokeProxy;
import com.burukeyou.uniapi.core.proxy.ApiProxyFactory;
import com.burukeyou.uniapi.core.proxy.QueueApiAnnotationProxy;
import com.burukeyou.uniapi.support.ProxySupport;
import com.burukeyou.uniapi.support.QueueApiAnnotationMeta;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class QueueApiProxyFactory implements ApiProxyFactory {

    @Override
    public boolean isProxy(ProxySupport proxySupport) {
        return  AnnotatedElementUtils.getMergedAnnotation(proxySupport.getTargetClass(), QueueApi.class) != null;
    }

    @Override
    public AnnotationInvokeProxy getProxy(ProxySupport proxySupport) {
        Class<?> targetClass = proxySupport.getTargetClass();
        QueueApi apiAnnotation = AnnotatedElementUtils.getMergedAnnotation(targetClass, QueueApi.class);

        Annotation proxyAnnotation =  null;
        lab: for (Annotation annotation : targetClass.getAnnotations()) {
                if (AnnotatedElementUtils.findMergedAnnotation(annotation.annotationType(),QueueApi.class) != null){
                    proxyAnnotation = annotation;
                    break lab;
                }
        }

        if (proxyAnnotation == null){
            throw new IllegalArgumentException("can not find proxyAnnotation for " + targetClass.getName());
        }

        QueueApiAnnotationMeta meta = new QueueApiAnnotationMeta(proxyAnnotation, apiAnnotation);
        meta.setProxySupport(proxySupport);
        return new QueueApiAnnotationProxy(meta);
    }
}
