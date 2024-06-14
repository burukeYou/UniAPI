package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author  caizhihao
 */
public interface Param {

    Class<?> getType();

    Type getGenericType();

    Object getValue();

    String getName();

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    boolean isObject();

    boolean isCollection();

    boolean isCollection(Class<?> elementClass);

    <T> List<T> castListValue(Class<T> elementClass);
}
