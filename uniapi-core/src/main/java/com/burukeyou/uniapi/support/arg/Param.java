package com.burukeyou.uniapi.support.arg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author  caizhihao
 */
public interface Param {

    /**
     * Get parameter types
     */
    Class<?> getType();

    /**
     * Get generic parameter types
     */
    Type getGenericType();

    /**
     * get the parameter value
     */
    Object getValue();

    /**
     *  if the value is not exist
     *      if the parameter value is  null  or blank  or  empty then return true, or else false
     */
    boolean isValueNotExist();

    /**
     * if the value is exist
     */
    boolean isValueExist();

    /**
     * get the parameter name
     */
    String getName();


    /**
     * Obtain target annotations on parameters
     * @param annotationClass         target annotations
     * @param <T>                     target annotations type
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * Determine if there is a target annotation on this parameter
     * @param annotationClass           target annotation
     */
    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    /**
     *  Normal values refer to types other than custom objects, collections, arrays, and Maps,
     *  such as basic types, wrapper types for basic types, strings, and so on
     */
    boolean isNormalValue();

    /**
     * Determine whether the parameter type is a custom object
     */
    boolean isObject();

    /**
     * Determine whether the parameter type is an array or collection
     */
    boolean isCollection();

    /**
     * Determine whether the parameter type is a collection or array of that element type
     * @param elementClass          element type
     */
    boolean isCollection(Class<?> elementClass);

    /**
     * Forcefully convert the parameter value to a collection of the element type.
     * Before using it, please use the {@link Param#isCollection()} method to determine whether the type is composite
     * @param elementClass      the element type.
     */
    <T> List<T> castListValue(Class<T> elementClass);
}
