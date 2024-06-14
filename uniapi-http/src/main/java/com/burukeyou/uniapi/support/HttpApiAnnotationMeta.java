package com.burukeyou.uniapi.support;

import com.burukeyou.uniapi.annotation.HttpApi;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

@Getter
@Setter
public class HttpApiAnnotationMeta extends ApiAnnotationMeta {

    private HttpApi httpApi;

    public HttpApiAnnotationMeta(Annotation proxyAnnotation, HttpApi httpApi) {
        super(proxyAnnotation);
        this.httpApi = httpApi;
    }

}
