package com.burukeyou.uniapi.http.core.retry;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import org.springframework.stereotype.Component;

/**
 * default retry strategy, retry when response is not successful
 * @author caihzihao
 */
@Component
public class DefaultHttpRetryStrategy implements HttpRetryStrategy<Object> {

    @Override
    public boolean canRetry(long curRetryCount, UniHttpRequest request, UniHttpResponse response, Object bodyResult, HttpApiMethodInvocation<Annotation> methodInvocation) {
        return !response.isSuccessful();
    }
}
