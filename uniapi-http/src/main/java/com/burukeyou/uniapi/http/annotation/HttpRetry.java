package com.burukeyou.uniapi.http.annotation;


import java.lang.annotation.*;

/**
 * Retry config annotation, when an exception occurs, will be retried.
 *
 * @author caizhihao
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HttpRetry {

    /**
     *  the maximum number of attempts , if less than 0, it means unlimited until the processing is successful
     */
    int maxAttempts() default 3;

    /**
     * the delay in milliseconds between retries
     */
    long delay() default 300;

    /**
     * Retries are performed only when a specified exception type occurs，By default, all exceptions are retried
     *
     * @return exception types to retry
     */
    Class<? extends Exception>[] include() default {};

    /**
     * Exception types that are not retryable.
     *
     * @return exception types to stop retry
     */
    Class<? extends Exception>[] exclude() default {};
}
