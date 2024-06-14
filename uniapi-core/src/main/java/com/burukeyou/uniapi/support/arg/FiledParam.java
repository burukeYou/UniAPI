package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author caizhihao
 */
public class FiledParam extends AbstractParam {

    protected Object target;

    protected Field field;

    private Object fieldValue;

    public FiledParam(Object target, Field field) {
        this.target = target;
        this.field = field;
        this.field.setAccessible(true);

        try {
            fieldValue = field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getType() {
        return getValue() == null ? field.getType() : getValue().getClass();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Object getValue() {
        return fieldValue;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }
}
