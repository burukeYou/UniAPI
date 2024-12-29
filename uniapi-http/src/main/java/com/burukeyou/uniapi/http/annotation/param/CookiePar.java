package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark cookies for HTTP requests
 *
 * <pre>
 * Support parameter types for tags：
 *       Map
 *       {@link com.burukeyou.uniapi.http.support.Cookie}
 *       Collection Cookie
 *       String (specify name)           Treat as a single cookie key value pair
 *       String (not specify name)       Treat as a complete cookie string
 *</pre>
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
