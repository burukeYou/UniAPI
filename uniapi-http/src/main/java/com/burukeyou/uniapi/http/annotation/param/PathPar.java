package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * Mark HTTP request path variable parameters
 *
 * <pre></pre>Support parameter types for tags: Normal value
 *
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathPar {

    /**
     *  Path variable value
     */
    String value() default "";
}
