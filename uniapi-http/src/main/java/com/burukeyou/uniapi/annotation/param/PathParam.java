package com.burukeyou.uniapi.annotation.param;

import java.lang.annotation.*;

/**
 *  url路径参数
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    String value() default "";
}
