package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark the HTTP request body as a complex form: corresponding content type as multipart/form data
 *
 * <pre>
 * Support parameter types for tags：
 *      Custom Object
 *      Map
 *      Normal Text value
 *      File value, support byte[]、InputStream、File
 *</pre>
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

    /**
     * File Name
     *      if not set, use original file name
     */
    String fileName() default "";
}
