package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark cookies for HTTP requests
 *
 * Support parameter types for tags：
 *       Map
 *       com.burukeyou.uniapi.http.support.Cookie
 *       com.burukeyou.uniapi.http.support.Cookie Collection
 *       String (specify name)           Treat as a single cookie key value pair
 *       String (not specify name)       Treat as a complete cookie string
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookiePar {

    /**
     *   Cookie Name
     */
    String value() default "";
}
