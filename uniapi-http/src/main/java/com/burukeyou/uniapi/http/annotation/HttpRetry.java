package com.burukeyou.uniapi.http.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.burukeyou.uniapi.http.core.retry.DefaultHttpRetryStrategy;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;

/**
 * Retry config annotation, when an exception occurs or {@link #retryStrategy} is return true, will be retried.
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
    long delay() default 0;

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

    /**
     *  use custom result retry strategy,
     *  this policy can determine whether a retry is needed based on the results
     *
     * @return the class of retry-result-policy
     */
    Class<? extends HttpRetryStrategy<?>> retryStrategy() default DefaultHttpRetryStrategy.class;

    /**
     * Whether to use FastRetry to perform a retry <a href="https://github.com/burukeYou/fast-retry">FastRetry</a>
     */
    boolean fastRetry() default false;
}
