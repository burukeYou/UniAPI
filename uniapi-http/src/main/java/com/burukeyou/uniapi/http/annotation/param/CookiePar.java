package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark cookies for HTTP requests
 *
 * <pre>
 * Support parameter types for tags：
 *       Custom Object
 *       Map
 *       {@link com.burukeyou.uniapi.http.support.Cookie}
 *       Collection Cookie
 *       String , It is automatically recognized whether it is a single cookie or multiple cookies string
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
