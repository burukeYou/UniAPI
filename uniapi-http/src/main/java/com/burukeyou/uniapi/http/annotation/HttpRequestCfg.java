package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http Request Config
 *
 *  // todo implement
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestCfg {

    /**
     * Whether to enable async requests
     */
    boolean async() default false;

    /**
     * Whether to enable follow redirects
     */
    boolean followRedirect() default true;

    /**
     * Whether to enable  follow redirects from HTTPS to HTTP and from HTTP to HTTPS
     */
    boolean followSslRedirect() default true;

}
