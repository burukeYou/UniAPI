package com.burukeyou.uniapi.http.core.retry.invocation;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;

public interface ResultInvocation<T> extends HttpRetryInvocation {

    /**
     * get the http request
     */
    UniHttpRequest getRequest();

    /**
     * get  http response
     */
    UniHttpResponse getResponse();

    /**
     * get response body result object
     */
    T getBodyResult();

    /**
     * Get the absolute name of the method, including the class name
     */
    String getMethodAbsoluteName();


}
