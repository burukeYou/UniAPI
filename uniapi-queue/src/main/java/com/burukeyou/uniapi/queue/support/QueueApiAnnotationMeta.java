package com.burukeyou.uniapi.queue.support;

import com.burukeyou.uniapi.queue.annotation.QueueApi;
import com.burukeyou.uniapi.support.ApiAnnotationMeta;
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
