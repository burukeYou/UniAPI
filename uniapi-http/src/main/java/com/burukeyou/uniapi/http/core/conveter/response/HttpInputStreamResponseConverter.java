package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpInputStreamResponse;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class HttpInputStreamResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(UniHttpResponse response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        return isFileReturnType(InputStream.class,methodInvocation);
    }


    @Override
    protected HttpInputStreamResponse doConvert(ResponseConvertContext context) {
        UniHttpResponse response = context.getResponse();
        return new HttpInputStreamResponse(response.getBodyToInputStream(),getFileResponseName(response),context);
    }
}
