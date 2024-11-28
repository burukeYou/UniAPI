package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure the functional properties of HTTP calls
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpCallConfig {

    /**
     * The call timeout spans the entire call: resolving DNS, connecting, writing the request body,
     * server processing, and reading the response body. If the call requires redirects or retries all must complete within one timeout period.
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     *
     * @return      call timeout in milliseconds
     */
    long callTimeout() default 0L;


    /**
     * connect timeout is applied when connecting a TCP socket to the target host.
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds.
     */
    long connectTimeout() default 0L;

    /**
     * The write timeout is applied for individual write IO operations
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     * @return      write timeout in milliseconds
     */
    long writeTimeout() default 0L;

    /**
     * The read timeout is applied to both the TCP socket and for individual read IO operations including on Source of the Response
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     * @return      read timeout in milliseconds
     */
    long readTimeout() default 0L;

}
