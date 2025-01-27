package com.burukeyou.uniapi.http.core.retry.policy;

/**
 * Determine whether a retry should be performed
 *
 * @author caizhihao
 * @param <T>
 */
public interface BodyResultPolicy<T> extends HttpRetryPolicy {

    /**
     * is Retry
     * @param bodyResult            bodyResult
     * @return                      true if retry
     */
    boolean canRetry(T bodyResult);

}
