package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.response.HttpTextResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpTextResponseConverter extends AbstractHttpResponseBodyConverter {

    @Override
    protected boolean isConvert(Response response, MethodInvocation methodInvocation) {
        return MediaTypeEnum.isTextType(getResponseContentType(response));
    }

    @Override
    protected HttpTextResponse<?> doConvert(Response response, MethodInvocation methodInvocation) {
        try {
            return new HttpTextResponse<>(response.body().string(),methodInvocation.getMethod());
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }
}
