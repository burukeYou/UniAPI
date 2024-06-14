package com.burukeyou.uniapi.support;

import com.burukeyou.uniapi.annotation.QueueApi;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

@Getter
@Setter
public class QueueApiAnnotationMeta extends ApiAnnotationMeta {

    private QueueApi queueApi;

    public QueueApiAnnotationMeta(Annotation proxyAnnotation, QueueApi queueApi) {
        super(proxyAnnotation);
        this.queueApi = queueApi;
    }

}
