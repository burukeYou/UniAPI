package com.burukeyou.uniapi.http.core.retry.executor;

import com.burukeyou.retry.core.FastRetryQueue;
import com.burukeyou.retry.core.RetryQueue;
import com.burukeyou.retry.core.support.FastRetryThreadPool;
import com.burukeyou.retry.spring.utils.SystemUtil;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.retry.RetryExecutor;
import com.burukeyou.uniapi.http.core.retry.UniHttpRetryTask;
import com.burukeyou.uniapi.http.support.HttpFuture;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;
import com.burukeyou.uniapi.support.thread.UniAPIThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.util.concurrent.*;

/**
 * @author  caizhihao
 */
@Slf4j
public class FastRetryRetryExecutor implements RetryExecutor {

    private static RetryQueue retryQueue;

    private HttpFastRetry httpFastRetry;

    private final HttpApiMethodInvocation<Annotation> httpApiMethodInvocation;

    private final Class<?> methodReturnType;

    private BeanFactory beanFactory;

    public FastRetryRetryExecutor(BeanFactory beanFactory,
                                  HttpFastRetry httpFastRetry,
                                  HttpApiMethodInvocation<Annotation> httpApiMethodInvocation,
                                  Class<?> methodReturnType) {
        this.beanFactory = beanFactory;
        this.httpFastRetry = httpFastRetry;
        this.httpApiMethodInvocation = httpApiMethodInvocation;
        this.methodReturnType = methodReturnType;

    }

    public Object execute(UniHttpRequest requestMetadata, Callable<UniHttpResponseParseInfo> callable) throws Throwable  {
        // init retry queue
        lazyInitRetryQueue();

        UniHttpRetryTask fastRetryTask = new UniHttpRetryTask(callable, httpFastRetry,beanFactory, httpApiMethodInvocation, requestMetadata,httpApiMethodInvocation);
        CompletableFuture<Object> fastRetryFuture = retryQueue.submit(fastRetryTask);

        if (!Future.class.isAssignableFrom(methodReturnType)){
            return fastRetryFuture.get();
        }

        if (methodReturnType.equals(CompletableFuture.class) || methodReturnType.equals(Future.class)){
            return fastRetryFuture;
        }

        if (methodReturnType.equals(HttpFuture.class)){
            HttpFuture<Object> httpFuture = new HttpFuture<>();
            fastRetryFuture.whenComplete((data,ex) -> {
                if (ex != null){
                    httpFuture.completeExceptionally(ex);
                }else {
                    httpFuture.complete(data);
                }
            });
            return httpFuture;
        }

       throw new IllegalStateException("unknow error");
    }

    private void lazyInitRetryQueue() {
        if (retryQueue == null){
            synchronized (FastRetryRetryExecutor.class){
                if (retryQueue == null){
                    int cpuCount = SystemUtil.CPU_COUNT;
                    log.info("[Uni-Http-Retry] init default retry queue cpuSize:{} ", cpuCount);
                    ExecutorService executorService = new FastRetryThreadPool(
                            4,
                            cpuCount * 4,
                            60, TimeUnit.SECONDS,
                            new UniAPIThreadFactory("Uni-Http-Retry")
                    );
                    retryQueue = new FastRetryQueue(executorService);
                }
            }
        }
    }
}
