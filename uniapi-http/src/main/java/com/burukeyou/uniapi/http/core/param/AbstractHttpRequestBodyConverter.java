package com.burukeyou.uniapi.http.core.param;

import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.support.ClassUtil;
import com.burukeyou.uniapi.support.arg.Param;

import java.lang.annotation.Annotation;

/**
 * @author  caizhihao
 */
public abstract class AbstractHttpRequestBodyConverter<T extends Annotation> implements HttpRequestBodyConverter {

    protected  AbstractHttpMetadataParamFinder paramFinder;

    protected  HttpRequestBodyConverter next;

    public AbstractHttpRequestBodyConverter() {
    }

    public AbstractHttpRequestBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        this.paramFinder = paramFinder;
    }

    protected AbstractHttpRequestBodyConverter(HttpRequestBodyConverter next, AbstractHttpMetadataParamFinder paramFinder) {
        this.paramFinder = paramFinder;
        this.next = next;
    }

    public HttpBody convert(Param param) {
        Class<T> annotationClass = (Class<T>) ClassUtil.getSuperClassParamFirstClass(getClass());
        T annotation = param.getAnnotation(annotationClass);
        if (annotation != null) {
            return doConvert(param,annotation);
        }

        if (next == null){
            return null;
        }

        return next.convert(param);
    }

    protected abstract HttpBody doConvert(Param param,T annotation);

    @Override
    public void setNext(HttpRequestBodyConverter nextConverter) {
        this.next = nextConverter;
    }
}
