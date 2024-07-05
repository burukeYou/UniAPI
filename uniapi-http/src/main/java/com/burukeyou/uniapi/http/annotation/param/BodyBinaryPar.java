package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 *  支持参数类型举例
 *          InputStream
 *          File
 *          InputStreamSource
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyBinaryPar {

    boolean required() default true;
}
