package com.burukeyou.uniapi.http.support.config;

import java.io.Serializable;
import java.util.List;

import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRetryConfig implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * The maximum number of attempts, if -1, it means unlimited until the processing is successful.
     */
    private Integer maxAttempts;

    /**
     * The delay in milliseconds between retries.
     */
    private Long delay;

    /**
     * Exception types that are retryable.
     */
    private List<Class<? extends Exception>> include;

    /**
     * Exception types that are not retryable.
     */
    private List<Class<? extends Exception>> exclude;

    /**
     * Use custom result retry strategy, this policy can determine whether a retry is needed based on the results.
     */
    private Class<? extends HttpRetryStrategy<?>> retryStrategy;

    /**
     * Whether to use FastRetry to perform a retry
     */
    private boolean fastRetry;

    public boolean isIncludeException(Class<? extends Exception> exceptionClass){
        if (include == null || include.isEmpty()){
            return false;
        }
        for (Class<? extends Exception> aClass : include) {
            if (aClass.isAssignableFrom(exceptionClass)){
                return true;
            }
        }
        return false;
    }

    public boolean isExcludeException(Class<? extends Exception> exceptionClass){
        if (exclude == null || exclude.isEmpty()){
            return false;
        }
        for (Class<? extends Exception> aClass : exclude) {
            if (aClass.isAssignableFrom(exceptionClass)){
                return true;
            }
        }
        return false;
    }

}
