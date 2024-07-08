package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark HTTP request header
 *
 * support parameter types for tags:
 *       Custom Object
 *       Map
 *       Normal value
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderPar {

    /**
     *  Request header name
     */
    String value() default "";
}
