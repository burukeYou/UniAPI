package com.burukeyou.uniapi.http.support;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.support.ApiAnnotationMeta;
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
