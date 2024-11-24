package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpEmptyBodyResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class HttpEmptyResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(UniHttpResponse response, MethodInvocation methodInvocation) {
        String contentType = getResponseContentType(response);
        if (StringUtils.isBlank(contentType)){
            return true;

        }
        Type type = getBodyResultType(methodInvocation);
        if (void.class.equals(type)){
            return true;
        }
        return false;
    }

    @Override
    protected HttpResponse<?> doConvert(ResponseConvertContext context) {
        return new HttpEmptyBodyResponse(context);
    }

}
