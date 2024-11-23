package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpBinaryResponse;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
public class HttpBinaryResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(UniHttpResponse response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        return isFileReturnType(byte[].class,methodInvocation);
    }
    @Override
    protected HttpBinaryResponse doConvert(ResponseConvertContext context) {
            UniHttpResponse response = context.getResponse();
            String fileName = getFileResponseName(response);
            byte[] bytes = response.getBodyBytes();
            return new HttpBinaryResponse(fileName, bytes,context);
    }


}
