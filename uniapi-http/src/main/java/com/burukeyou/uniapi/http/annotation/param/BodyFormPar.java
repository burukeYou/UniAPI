package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * Mark the HTTP request body as a regular form:
 *      the corresponding content type is application/x-www form urlencoded
 *
 *      Support parameter types for tags:
 *          Custom Object
 *          Map
 *          Normal value
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyFormPar {

    /**
     * Form Field Name
     */
    String value() default "";

}
