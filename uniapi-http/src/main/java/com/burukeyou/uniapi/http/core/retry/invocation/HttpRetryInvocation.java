package com.burukeyou.uniapi.http.core.retry.invocation;

import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;

import java.lang.annotation.Annotation;

public interface HttpRetryInvocation extends HttpApiMethodInvocation<Annotation> {

    /**
     * get the retry task
     */
    long getCurExecuteCount();

    /**
     * get retry annotation
     */
    HttpFastRetry getHttpFastRetry();
}
