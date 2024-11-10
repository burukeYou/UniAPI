package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpInputStreamResponse;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class HttpInputStreamResponseConverter extends AbstractHttpResponseBodyConverter {

    @Override
    protected boolean isConvert(Response response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        return isFileReturnType(InputStream.class,methodInvocation);
    }


    @Override
    protected HttpInputStreamResponse doConvert(Response response, MethodInvocation methodInvocation) {
        return new HttpInputStreamResponse(response.body().byteStream(),getFileResponseName(response));
    }
}
