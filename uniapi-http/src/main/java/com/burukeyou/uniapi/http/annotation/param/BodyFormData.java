package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyFormData {

    boolean required() default true;
}
