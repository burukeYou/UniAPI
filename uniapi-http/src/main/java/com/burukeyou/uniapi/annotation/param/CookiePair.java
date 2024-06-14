package com.burukeyou.uniapi.annotation.param;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookiePair {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

}
