package com.burukeyou.uniapi.http.core.retry;

import com.burukeyou.retry.core.policy.RetryPolicy;
import com.burukeyou.retry.spring.annotations.RetryWait;
import com.burukeyou.retry.spring.core.interceptor.FastRetryInterceptor;
import com.burukeyou.retry.spring.core.policy.LogEnum;
import com.burukeyou.retry.spring.core.retrytask.FastRetryAdapter;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;

/**
 * @author  caizhihao
 */
public class HttpRetryAnnotationAdapter implements FastRetryAdapter {

    private final HttpFastRetry fastRetry;

    public HttpRetryAnnotationAdapter(HttpFastRetry fastRetry) {
        this.fastRetry = fastRetry;
    }

    @Override
    public int maxAttempts() {
        return fastRetry.maxAttempts();
    }

    @Override
    public RetryWait retryWait() {
        RetryWait[] retryWaits = fastRetry.retryWait();
        return  retryWaits.length > 0 ? retryWaits[0] : null;
    }

    @Override
    public long delay() {
        return fastRetry.delay();
    }

    @Override
    public boolean retryIfException() {
        return fastRetry.retryIfException();
    }

    @Override
    public Class<? extends Exception>[] include() {
        return fastRetry.include();
    }

    @Override
    public Class<? extends Exception>[] exclude() {
        return fastRetry.exclude();
    }

    @Override
    public boolean exceptionRecover() {
        return fastRetry.exceptionRecover();
    }

    @Override
    public LogEnum errLog() {
        return fastRetry.errLog();
    }

    @Override
    public boolean briefErrorLog() {
        return fastRetry.briefErrorLog();
    }

    @Override
    public Class<? extends RetryPolicy> policy() {
        return fastRetry.policy().length > 0 ? fastRetry.policy()[0] : null;
    }

    @Override
    public Class<? extends FastRetryInterceptor> interceptor() {
        return null;
    }
}
