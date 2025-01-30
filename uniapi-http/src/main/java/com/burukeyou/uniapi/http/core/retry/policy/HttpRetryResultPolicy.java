package com.burukeyou.uniapi.http.core.retry.policy;

/**
 * Determine whether a retry should be performed
 *
 * @author caizhihao
 * @param <T>
 */
public interface HttpRetryResultPolicy<T> extends HttpRetryPolicy {

    /**
     * is Retry
     * @param bodyResult            bodyResult
     * @return                      If it returns true, the retry will continue, otherwise stop the retry
     */
    boolean canRetry(T bodyResult);

}
