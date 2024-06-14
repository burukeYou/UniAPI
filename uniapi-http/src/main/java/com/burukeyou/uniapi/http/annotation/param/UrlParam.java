package com.burukeyou.uniapi.http.annotation.param;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 *  url路径参数 和 请求参数
 *
 *  比如：  /poi/{userId}/categoty?cityId={cityId}
 *
 *  其中 userId 和 cityId都属于 url参数
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UrlParam {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean required() default true;
}
