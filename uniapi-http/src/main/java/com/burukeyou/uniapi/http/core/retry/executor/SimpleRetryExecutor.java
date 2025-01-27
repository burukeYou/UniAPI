package com.burukeyou.uniapi.http.core.retry.executor;

import com.burukeyou.uniapi.http.annotation.HttpRetry;
import com.burukeyou.uniapi.http.core.channel.DefaultHttpApiInvoker;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.exception.UniHttpRetryTimeOutException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.retry.RetryExecutor;
import com.burukeyou.uniapi.http.support.HttpFuture;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * simple sync retry
 * @author  caizhihao
 */
public class SimpleRetryExecutor implements RetryExecutor {

    private final HttpRetry retryConfig;
    private final HttpApiMethodInvocation<Annotation> httpApiMethodInvocation;

    private final Class<?> methodReturnType;

    public SimpleRetryExecutor(HttpRetry retryConfig, HttpApiMethodInvocation<Annotation> httpApiMethodInvocation, Class<?> methodReturnType) {
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

        DefaultHttpApiInvoker.runAsync(() -> {
            try {
                UniHttpResponseParseInfo parseInfo = doInvokeForSyncRetry(retryConfig,requestMetadata,httpApiMethodInvocation,callable);
                retryFuture.complete(parseInfo.getFutureInnerValue());
            } catch (Throwable e) {
                retryFuture.completeExceptionally(e);
            }
        });

        return retryFuture;
    }

    private UniHttpResponseParseInfo doInvokeForSyncRetry(HttpRetry retryConfig,
                                                          UniHttpRequest requestMetadata,
                                                          HttpApiMethodInvocation<Annotation> httpApiMethodInvocation,
                                                          Callable<UniHttpResponseParseInfo> callable) throws Throwable {
        //HttpRetryStrategy<Object> retryStrategy = (HttpRetryStrategy<Object>) BizUtil.getBeanOrNew(retryConfig.getRetryStrategy());
        long delay = retryConfig.delay();

        Integer maxAttempts = retryConfig.maxAttempts();
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
                if (retryConfig.include().length == 0 && retryConfig.exclude().length == 0){
                    retryFlag = true;
                }else {
                    if (retryConfig.include().length > 0){
                        retryFlag = isIncludeException(retryConfig.include(),e.getClass());
                    }
                    if (retryConfig.exclude().length > 0){
                        retryFlag = !isExcludeException(retryConfig.exclude(),e.getClass());
                    }
                }
            }

            if (!isException && curParseInfo.isNotResponse()){
                //  没有抛异常，说明被提前终止请求了或者异常被吞掉了, 结束重试
                break;
            }

//            if (!isException){
//                retryFlag = retryStrategy.canRetry(executeCount,requestMetadata,curParseInfo.getUniHttpResponse(),curParseInfo.getBodyResult(),httpApiMethodInvocation);
//            }

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

    public boolean isIncludeException(Class<? extends Exception>[] include,Class<? extends Exception> exceptionClass){
        if (include == null || include.length == 0){
            return false;
        }
        for (Class<? extends Exception> aClass : include) {
            if (aClass.isAssignableFrom(exceptionClass)){
                return true;
            }
        }
        return false;
    }

    public boolean isExcludeException(Class<? extends Exception>[] exclude,Class<? extends Exception> exceptionClass){
        if (exclude == null || exclude.length == 0){
            return false;
        }
        for (Class<? extends Exception> aClass : exclude) {
            if (aClass.isAssignableFrom(exceptionClass)){
                return true;
            }
        }
        return false;
    }


}
