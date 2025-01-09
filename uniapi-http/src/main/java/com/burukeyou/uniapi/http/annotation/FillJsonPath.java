package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FillJsonPath {

    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";


}
