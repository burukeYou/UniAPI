package com.burukeyou.uniapi.http.core.retry;

import com.burukeyou.retry.core.policy.RetryPolicy;
import com.burukeyou.retry.spring.core.retrytask.AbstractRetryAnnotationTask;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.retry.invocation.impl.HttpRetryInvocationImpl;
import com.burukeyou.uniapi.http.core.retry.invocation.impl.RetryResultInvocationImpl;
import com.burukeyou.uniapi.http.core.retry.policy.AllResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.BodyResultPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryInterceptorPolicy;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author caizhihao
 */
public class UniHttpRetryTask extends AbstractRetryAnnotationTask<Object> {

    private UniHttpRequest uniHttpRequest;
    private HttpApiMethodInvocation<Annotation> httpApiMethodInvocation;

    private UniHttpResponseParseInfo result;
    private Callable<UniHttpResponseParseInfo> runnable;
    private HttpFastRetry httpFastRetry;

    public UniHttpRetryTask(Callable<UniHttpResponseParseInfo> runnable,
                            HttpFastRetry retry,
                            BeanFactory beanFactory,
                            MethodInvocation methodInvocation,
                            UniHttpRequest uniHttpRequest,
                            HttpApiMethodInvocation<Annotation> httpApiMethodInvocation) {
        super(new HttpRetryAnnotationAdapter(retry), methodInvocation,beanFactory);
        this.httpFastRetry = retry;
        this.uniHttpRequest = uniHttpRequest;
        this.runnable = runnable;
        this.httpApiMethodInvocation = httpApiMethodInvocation;
    }


    @Override
    protected boolean doRetry(RetryPolicy retryPolicy) throws Exception {
        if (retryPolicy == null){
            doInvokeMethod();
            return false;
        }

        if (retryPolicy instanceof BodyResultPolicy){
            BodyResultPolicy<Object> resultPolicy = (BodyResultPolicy<Object>)retryPolicy;
            doInvokeMethod();
            return resultPolicy.canRetry(result.getBodyResult());
        }

        if (retryPolicy instanceof AllResultPolicy){
            AllResultPolicy<Object> interceptorPolicy = (AllResultPolicy<Object>)retryPolicy;
            doInvokeMethod();
            return interceptorPolicy.canRetry(new RetryResultInvocationImpl<>(httpApiMethodInvocation,retryCounter,httpFastRetry,uniHttpRequest, result));
        }

        if (retryPolicy instanceof HttpRetryInterceptorPolicy){
            HttpRetryInvocationImpl invocation = new HttpRetryInvocationImpl(httpApiMethodInvocation, retryCounter, httpFastRetry);
            HttpRetryInterceptorPolicy<Object> interceptorPolicy = (HttpRetryInterceptorPolicy<Object>)retryPolicy;

            if (!interceptorPolicy.beforeExecute(uniHttpRequest,invocation)){
                return false;
            }

            Exception exception = null;
            try {
                doInvokeMethod();
            } catch (Exception e) {
                exception = e;
            }
            if (exception == null) {
                return interceptorPolicy.afterExecuteSuccess(result.getBodyResult(), uniHttpRequest,result.getUniHttpResponse(),invocation);
            } else {
                return interceptorPolicy.afterExecuteFail(exception, uniHttpRequest,invocation);
            }
        }

        throw new IllegalStateException("not support retry policy for " + retryPolicy.getClass().getName());
    }

    @Override
    public Object getResult() {
        if (result == null || result.isNotResponse()){
            return null;
        }

        if (!Future.class.isAssignableFrom(methodInvocation.getMethod().getReturnType())){
            return result.getMethodReturnValue();
        }else {
            return result.getFutureInnerValue();
        }
    }

    protected UniHttpResponseParseInfo doInvokeMethod() throws Exception {
        result = runnable.call();
        return result;
    }
}
