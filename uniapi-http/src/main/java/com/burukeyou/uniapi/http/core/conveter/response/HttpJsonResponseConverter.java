package com.burukeyou.uniapi.http.core.conveter.response;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.core.response.HttpJsonResponse;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class HttpJsonResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(UniHttpResponse response, MethodInvocation methodInvocation) {
        return getResponseContentType(response).contains(MediaTypeEnum.APPLICATION_JSON.getType());
    }

    @Override
    protected HttpJsonResponse<?> doConvert(ResponseConvertContext context) {
            String originBodyString = super.getResponseBodyString(context.getResponse());
            HttpJsonResponse<Object> httpJsonResponse = new HttpJsonResponse<>(originBodyString,context);

            originBodyString = super.postAfterBodyString(originBodyString,httpJsonResponse,context);
            httpJsonResponse.setJsonValue(originBodyString);

            //
            Type bodyResultType = getBodyResultType(context.getMethodInvocation());
            if (String.class.equals(bodyResultType) || void.class.equals(bodyResultType)){
                return httpJsonResponse;
            }

            //
            Object bodyResultObj = JSON.parseObject(originBodyString, bodyResultType);
            httpJsonResponse.setResult(bodyResultObj);
            return httpJsonResponse;
    }
}
