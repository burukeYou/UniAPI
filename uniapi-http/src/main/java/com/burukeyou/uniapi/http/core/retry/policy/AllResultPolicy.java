package com.burukeyou.uniapi.http.core.retry.policy;

import com.burukeyou.uniapi.http.core.retry.invocation.ResultInvocation;

/**
 * Determine whether a retry should be performed
 *
 * @author caizhihao
 * @param <T>
 */
public interface AllResultPolicy<T> extends HttpRetryPolicy {

    /**
     * is Retry
     * @param invocation            methodInvocation
     * @return                      true if retry
     */
    boolean canRetry(ResultInvocation<T> invocation);

}
