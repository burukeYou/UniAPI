package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class MapParam extends AbstractParam {

    private Object key;
    private Object value;

    public MapParam(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Class<?> getType() {
        return value != null ? value.getClass() : null;
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
        return key.toString();
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
