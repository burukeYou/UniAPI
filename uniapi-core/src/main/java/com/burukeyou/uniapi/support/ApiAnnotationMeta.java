package com.burukeyou.uniapi.support;

import lombok.Data;

import java.lang.annotation.Annotation;

@Data
public class ApiAnnotationMeta {

    protected Annotation proxyAnnotation;

    private ProxySupport proxySupport;

    public ApiAnnotationMeta(Annotation proxyAnnotation) {
        this.proxyAnnotation = proxyAnnotation;
    }
}
