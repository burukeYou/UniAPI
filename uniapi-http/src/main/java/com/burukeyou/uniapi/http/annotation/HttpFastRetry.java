package com.burukeyou.uniapi.http.annotation;


import com.burukeyou.retry.spring.annotations.RetryWait;
import com.burukeyou.retry.spring.core.policy.LogEnum;
import com.burukeyou.retry.spring.core.policy.RetryInterceptorPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.AllResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.BodyResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryPolicy;

import java.lang.annotation.*;

/**
 * Retry annotation
 *
 * @author caizhihao
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpFastRetry {

    /**
     * @return the maximum number of attempts , if -1, it means unlimited
     */
    int maxAttempts() default 3;

    /**
     * How long will it take to start the next retry, equals to {@link #delay},
     * If both are configured, retryWait() will take effect first
     */
    RetryWait[] retryWait() default {};

    /**
     * How long will it take to start the next retry, unit is MILLISECONDS
     */
    long delay() default 500;

    /**
     * Flag to say that whether try again when an exception occurs
     * @return try again if true
     */
    boolean retryIfException() default true;

    /**
     * Exception types that are retryable.
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
     * Flag to say that whether recover when an exception occurs
     *
     * @return throw exception if false, if true return null and print exception log
     */
    boolean exceptionRecover() default false;

    /**
     * Flag to say that whether print every time execute retry exception log, just prevent printing too many logs
     */
    LogEnum errLog() default LogEnum.EVERY;

    /**
     * Set whether to simplify the stack information of the printing exception,
     * if so, only the first three lines of stack information will be printed,
     * and if the first execution fails, this configuration will be ignored,
     * and the complete exception information will still be printed
     * @see #errLog()
     */
    boolean briefErrorLog() default false;

    /**
     *  use custom  retry strategy,
     *  <p>Currently, the following policies are supported:</p>
     *  <ul>
     *      <li>{@link BodyResultPolicy}</li>
     *      <li>{@link AllResultPolicy}</li>
     *      <li>{@link RetryInterceptorPolicy}</li>
     *  </ul>
     * @return the class of retry-result-policy
     */
    Class<? extends HttpRetryPolicy>[] policy() default {};

}
