package com.burukeyou.uniapi.http.core.retry.policy;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.retry.invocation.HttpRetryInvocation;

public interface HttpRetryInterceptorPolicy<T> extends HttpRetryPolicy {

    /**
     * Before each retry execution
     *
     * @param invocation proxy method
     * @return if true continue to execute ,  or else stop to execute
     */
    default boolean beforeExecute(UniHttpRequest uniHttpRequest,
                                  HttpRetryInvocation invocation) throws Exception {
        return true;
    }

    /**
     * After each failed retry execution
     *
     * @param exception  the method execute exception
     * @param invocation proxy method
     */
    default boolean afterExecuteFail(Exception exception, UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
        throw exception;
    }


    /**
     * After each successful retry execution
     * @param bodyResult                response body result
     * @param uniHttpRequest            request data
     * @param uniHttpResponse           response data
     * @param invocation                method invocation
     * @return if true continue to retry invoke ,  or else stop retry invoke

     */
    default boolean afterExecuteSuccess(T bodyResult,
                                        UniHttpRequest uniHttpRequest,
                                        UniHttpResponse uniHttpResponse,
                                        HttpRetryInvocation invocation) {
        return false;
    }

}
