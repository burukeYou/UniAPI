package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ParamWrapper extends AbstractParam {

    private Object value;

    public ParamWrapper(Object value) {
        this.value = value;
    }

    @Override
    public Class<?> getType() {
        return value == null ? null : value.getClass();
    }

    @Override
    public Type getGenericType() {
        return  getType();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return false;
    }
}
