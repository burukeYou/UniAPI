package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.response.HttpBinaryResponse;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 *
 */
@Component
public class HttpBinaryResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(Response response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        return isFileReturnType(byte[].class,methodInvocation);
    }
    @Override
    protected HttpBinaryResponse doConvert(ResponseConvertContext context) {
        try {
            Response response = context.getResponse();
            ResponseBody responseBody = response.body();
            String fileName = getFileResponseName(response);
            byte[] bytes = responseBody.bytes();
            return new HttpBinaryResponse(fileName, bytes,context);
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }


}
