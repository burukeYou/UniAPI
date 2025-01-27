package com.burukeyou.uniapi.http.core.retry.invocation.impl;

import com.burukeyou.retry.spring.core.retrytask.RetryCounter;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.retry.invocation.HttpRetryInvocation;

import java.lang.annotation.Annotation;


public class HttpRetryInvocationImpl extends AbstractHttpMethodInvocation implements HttpRetryInvocation {

    private final RetryCounter retryCounter;
    private final HttpFastRetry httpFastRetry;

    public HttpRetryInvocationImpl(HttpApiMethodInvocation<Annotation> methodInvocation,
                                   RetryCounter retryCounter,
                                   HttpFastRetry httpFastRetry) {
        super(methodInvocation);
        this.retryCounter = retryCounter;
        this.httpFastRetry = httpFastRetry;
    }

    @Override
    public long getCurExecuteCount() {
        return retryCounter.getCurExecuteCount();
    }

    @Override
    public HttpFastRetry getHttpFastRetry() {
        return httpFastRetry;
    }
}
