package com.burukeyou.uniapi.core.proxy;

import com.burukeyou.uniapi.support.ApiAnnotationMeta;

/**
 * @author caizhihao
 * @param <T>
 */
public abstract class AbstractAnnotationInvokeProxy<T extends ApiAnnotationMeta>  implements AnnotationInvokeProxy {

    protected T annotationMeta;

    public AbstractAnnotationInvokeProxy() {
    }

    public AbstractAnnotationInvokeProxy(T annotationMeta) {
        this.annotationMeta = annotationMeta;
    }

    public T getAnnotationMeta() {
        return annotationMeta;
    }
}
