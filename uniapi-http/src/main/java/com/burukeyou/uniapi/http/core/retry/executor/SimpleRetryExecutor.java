package com.burukeyou.uniapi.http.core.retry.executor;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.exception.UniHttpRetryTimeOutException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import com.burukeyou.uniapi.http.core.retry.RetryExecutor;
import com.burukeyou.uniapi.http.support.HttpFuture;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;
import com.burukeyou.uniapi.http.support.config.HttpRetryConfig;
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.util.ListsUtil;

/**
 *
 * @author  caizhihao
 */
public class SimpleRetryExecutor implements RetryExecutor {

    private final HttpRetryConfig retryConfig;
    private final HttpApiMethodInvocation<Annotation> httpApiMethodInvocation;

    private final Class<?> methodReturnType;

    public SimpleRetryExecutor(HttpRetryConfig retryConfig, HttpApiMethodInvocation<Annotation> httpApiMethodInvocation, Class<?> methodReturnType) {
        this.retryConfig = retryConfig;
        this.httpApiMethodInvocation = httpApiMethodInvocation;
        this.methodReturnType = methodReturnType;
    }

    public Object execute(UniHttpRequest requestMetadata, Callable<UniHttpResponseParseInfo> callable) throws Throwable {
        //  sync  retry
        if (!Future.class.isAssignableFrom(methodReturnType)) {
            return doInvokeForSyncRetry(retryConfig,requestMetadata,httpApiMethodInvocation,callable).getMethodReturnValue();
        }

        // async retry
        HttpFuture<Object> retryFuture = new HttpFuture<>();

        // todo
        CompletableFuture.runAsync(() -> {
            try {
                UniHttpResponseParseInfo parseInfo = doInvokeForSyncRetry(retryConfig,requestMetadata,httpApiMethodInvocation,callable);
                retryFuture.complete(parseInfo.getFutureInnerValue());
            } catch (Throwable e) {
                retryFuture.completeExceptionally(e);
            }
        });

        return retryFuture;
    }

    private UniHttpResponseParseInfo doInvokeForSyncRetry(HttpRetryConfig retryConfig,
                                                          UniHttpRequest requestMetadata,
                                                          HttpApiMethodInvocation<Annotation> httpApiMethodInvocation,
                                                          Callable<UniHttpResponseParseInfo> callable) throws Throwable {
        HttpRetryStrategy<Object> retryStrategy = (HttpRetryStrategy<Object>) BizUtil.getBeanOrNew(retryConfig.getRetryStrategy());
        Long delay = retryConfig.getDelay();

        Integer maxAttempts = retryConfig.getMaxAttempts();
        Exception curException;
        Exception lastException = null;
        UniHttpResponseParseInfo curParseInfo;
        long executeCount = 0;
        while (true){
            curException = null;
            curParseInfo = null;
            executeCount++;
            long curRetryCount = executeCount -1;
            // 小于0一直重试，直到拿到结果
            if (maxAttempts > 0 && curRetryCount > maxAttempts){
                throw new UniHttpRetryTimeOutException("Exceeded the maximum retry count " + maxAttempts + ", stop retry",lastException);
            }
            boolean retryFlag = false;
            boolean isException =false;
            try {
                // do call
                curParseInfo = callable.call();
            } catch (Exception e) {
                lastException = e;
                curException = e;
                isException = true;
                if (ListsUtil.isEmpty(retryConfig.getInclude()) && ListsUtil.isEmpty(retryConfig.getExclude())){
                    retryFlag = true;
                }else {
                    if (ListsUtil.isNotEmpty(retryConfig.getInclude())){
                        retryFlag = retryConfig.isIncludeException(e.getClass());
                    }
                    if (ListsUtil.isNotEmpty(retryConfig.getExclude())){
                        retryFlag = !retryConfig.isExcludeException(e.getClass());
                    }
                }
            }

            if (!isException && curParseInfo.isNotResponse()){
                //  没有抛异常，说明被提前终止请求了或者异常被吞掉了, 结束重试
                break;
            }

            if (!isException){
                retryFlag = retryStrategy.canRetry(executeCount,requestMetadata,curParseInfo.getUniHttpResponse(),curParseInfo.getBodyResult(),httpApiMethodInvocation);
            }

            if (!retryFlag){
                break;
            }

            if (delay > 0){
                Thread.sleep(delay);
            }
        }

        if (curException != null){
            throw curException;
        }
        return curParseInfo;

    }

}
