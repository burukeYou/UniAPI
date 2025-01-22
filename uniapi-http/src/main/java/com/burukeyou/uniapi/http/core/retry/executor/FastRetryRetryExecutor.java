package com.burukeyou.uniapi.http.core.retry.executor;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.burukeyou.retry.core.FastRetryQueue;
import com.burukeyou.retry.core.RetryQueue;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import com.burukeyou.uniapi.http.core.retry.RetryExecutor;
import com.burukeyou.uniapi.http.core.retry.UniHttpFastRetryTask;
import com.burukeyou.uniapi.http.support.HttpFuture;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;
import com.burukeyou.uniapi.http.support.config.HttpRetryConfig;
import com.burukeyou.uniapi.http.utils.BizUtil;

/**
 * @author  caizhihao
 */
public class FastRetryRetryExecutor implements RetryExecutor {

    private static RetryQueue retryQueue;

    private final HttpRetryConfig retryConfig;
    private final HttpApiMethodInvocation<Annotation> httpApiMethodInvocation;

    private final Class<?> methodReturnType;

    public FastRetryRetryExecutor(HttpRetryConfig retryConfig, HttpApiMethodInvocation<Annotation> httpApiMethodInvocation, Class<?> methodReturnType) {
        this.retryConfig = retryConfig;
        this.httpApiMethodInvocation = httpApiMethodInvocation;
        this.methodReturnType = methodReturnType;
    }

    public Object execute(UniHttpRequest requestMetadata, Callable<UniHttpResponseParseInfo> callable) {
        lazyInitRetryQueue();

        @SuppressWarnings("unchecked")
        HttpRetryStrategy<Object> retryStrategy = (HttpRetryStrategy<Object>) BizUtil.getBeanOrNew(retryConfig.getRetryStrategy());
        UniHttpFastRetryTask fastRetryTask = new UniHttpFastRetryTask(retryConfig, retryStrategy, requestMetadata, httpApiMethodInvocation, callable);
        CompletableFuture<UniHttpResponseParseInfo> fastRetryFuture = retryQueue.submit(fastRetryTask);
        HttpFuture<Object> retryFuture = new HttpFuture<>();
        fastRetryFuture.whenComplete((parseInfo, throwable) -> {
            if (throwable != null){
                retryFuture.completeExceptionally(throwable);
            }else {
                // success complete
                if (Future.class.isAssignableFrom(methodReturnType) ){
                    retryFuture.complete(parseInfo.getFutureInnerValue());
                }else {
                    retryFuture.complete(parseInfo.getMethodReturnValue());
                }
            }
        });
        return Future.class.isAssignableFrom(methodReturnType) ? retryFuture : retryFuture.get();
    }

    private void lazyInitRetryQueue() {
        if (retryQueue == null){
            synchronized (RetryQueue.class){
                retryQueue  = new FastRetryQueue(new ThreadPoolExecutor(8, 16,
                        300L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(),
                        r -> new Thread(r, "uniHttp-fast-retry-thread")));
            }
        }
    }
}
