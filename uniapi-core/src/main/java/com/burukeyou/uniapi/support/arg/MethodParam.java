package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class MethodParam extends AbstractParam {

    protected Parameter parameter;

    protected Object argValue;

    public MethodParam(Parameter parameter, Object argValue) {
        this.parameter = parameter;
        this.argValue = argValue;
    }

    @Override
    public Class<?> getType() {
        return getValue() == null ? parameter.getType() : getValue().getClass();
    }

    @Override
    public Type getGenericType() {
        return parameter.getParameterizedType();
    }

    @Override
    public Object getValue() {
        return argValue;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);

    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return parameter.isAnnotationPresent(annotationClass);
    }
}
