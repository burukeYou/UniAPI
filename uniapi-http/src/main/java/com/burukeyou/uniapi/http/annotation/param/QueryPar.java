package com.burukeyou.uniapi.http.annotation.param;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 *
 *  支持的参数类型举例
 *        List<Integer>
 *        int[]
 *        User  user
 *        Map<String,Object>
 *        String
 *        Integer
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryPar {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean required() default true;
}
