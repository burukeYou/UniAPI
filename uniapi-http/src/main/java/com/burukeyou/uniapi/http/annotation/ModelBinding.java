package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Value;

/**
 * Mark an object as model, and then the related data binding behavior is automatically executed
 * such as {@link JsonPathMapping}, {@link Value}
 * If it is tagged on a non-custom object, it will not take effect and will not perform the related data binding behavior
 *
 * @author  caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER,ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelBinding {


}
