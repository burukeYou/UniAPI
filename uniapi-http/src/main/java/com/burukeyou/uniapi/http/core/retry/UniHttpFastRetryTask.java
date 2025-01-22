package com.burukeyou.uniapi.http.core.retry;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.Callable;

import com.burukeyou.retry.core.task.RetryTask;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;
import com.burukeyou.uniapi.http.support.config.HttpRetryConfig;

/**
 * @author caizhihao
 */
public class UniHttpFastRetryTask implements RetryTask<UniHttpResponseParseInfo> {

    private HttpRetryConfig retryConfig;

    private HttpRetryStrategy<Object> retryStrategy;

    private UniHttpRequest uniHttpRequest;

    private HttpApiMethodInvocation<Annotation> methodInvocation;

    private Callable<UniHttpResponseParseInfo> callable;

    private UniHttpResponseParseInfo result;

    public UniHttpFastRetryTask(HttpRetryConfig retryConfig,
                                HttpRetryStrategy<Object> retryStrategy,
                                UniHttpRequest uniHttpRequest,
                                HttpApiMethodInvocation<Annotation> methodInvocation,
                                Callable<UniHttpResponseParseInfo> callable) {
        this.retryConfig = retryConfig;
        this.retryStrategy = retryStrategy;
        this.callable = callable;
        this.uniHttpRequest = uniHttpRequest;
        this.methodInvocation = methodInvocation;
    }

    @Override
    public int attemptMaxTimes() {
        return retryConfig.getMaxAttempts();
    }

    @Override
    public long waitRetryTime() {
        return retryConfig.getDelay();
    }

    @Override
    public boolean retry(long curRetryCount) throws Exception {
        result = callable.call();

        if (result.isNotResponse()) {
            // 没有抛异常，说明被提前终止请求了或者异常被吞掉了, 结束重试
            return false;
        }

        if (retryStrategy == null) {
            return false;
        }
        return retryStrategy.canRetry(curRetryCount, uniHttpRequest, result.getUniHttpResponse(), result.getBodyResult(), methodInvocation);
    }

    @Override
    public UniHttpResponseParseInfo getResult() {
        return result;
    }


    @Override
    public List<Class<? extends Exception>> include() {
        return retryConfig.getInclude();
    }

    @Override
    public List<Class<? extends Exception>> exclude() {
        return retryConfig.getExclude();
    }


    @Override
    public boolean printExceptionLog() {
        return false;
    }
}
