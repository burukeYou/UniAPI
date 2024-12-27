package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * Query parameters for marking HTTP request URLs
 *
 * <pre>
 *  Support parameter types for tags
 *     Map
 *     Custom Object
 *     Normal value
 *     Normal value Collection
 *</pre>
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryPar {

    /**
     * Query parameter name
     */
    String value() default "";

}
