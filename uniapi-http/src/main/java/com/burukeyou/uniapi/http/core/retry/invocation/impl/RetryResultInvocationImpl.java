package com.burukeyou.uniapi.http.core.retry.invocation.impl;

import com.burukeyou.retry.core.entity.RetryCounter;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.retry.invocation.ResultInvocation;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;

import java.lang.annotation.Annotation;


/**
 * @author  caizhihao
 */
public class RetryResultInvocationImpl<T> extends HttpRetryInvocationImpl implements ResultInvocation<T> {


    private final UniHttpRequest request;
    private final UniHttpResponseParseInfo parseInfo;

    public RetryResultInvocationImpl(HttpApiMethodInvocation<Annotation> methodInvocation,
                                     RetryCounter retryCounter,
                                     HttpFastRetry httpFastRetry,
                                     UniHttpRequest request,
                                     UniHttpResponseParseInfo parseInfo) {
        super(methodInvocation, retryCounter, httpFastRetry);
        this.request = request;
        this.parseInfo = parseInfo;
    }

    private boolean isSuccessResponse() {
        return parseInfo != null && !parseInfo.isNotResponse();
    }

    @Override
    public T getBodyResult() {
        return isSuccessResponse() ? (T)parseInfo.getBodyResult() : null;
    }

    @Override
    public UniHttpResponse getResponse() {
        return isSuccessResponse() ? parseInfo.getUniHttpResponse() : null;
    }

    @Override
    public UniHttpRequest getRequest() {
        return request;
    }

}
