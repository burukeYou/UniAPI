package com.burukeyou.uniapi.http.core.retry;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;

/**
 * Determine whether a retry should be performed
 *
 * @author caizhihao
 * @param <T>
 */
public interface HttpRetryStrategy<T> {

    /**
     * is Retry
     * @param curRetryCount         current retry count
     * @param request               request data
     * @param response              response data
     * @param bodyResult            response body result object
     * @param methodInvocation      methodInvocation
     * @return                      true if retry
     */
    boolean canRetry(long curRetryCount,
                     UniHttpRequest request,
                     UniHttpResponse response,
                     T bodyResult,
                     HttpApiMethodInvocation<Annotation> methodInvocation);

}
