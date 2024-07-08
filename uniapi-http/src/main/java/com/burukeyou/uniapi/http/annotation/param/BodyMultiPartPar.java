package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark the HTTP request body as a complex form: corresponding content type as multipart/form data
 *
 * Support parameter types for tags：
 *      Custom Object
 *      Map
 *      Normal value
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyMultiPartPar {

    /**
     * Form Field Name
     */
    String value() default "";
}
