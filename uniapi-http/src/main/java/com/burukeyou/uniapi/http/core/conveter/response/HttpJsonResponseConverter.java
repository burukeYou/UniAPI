package com.burukeyou.uniapi.http.core.conveter.response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpJsonResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
            if (StringUtils.isBlank(originBodyString)){
                return httpJsonResponse;
            }

            //
            String[] jsonStringFormatPath = getResponseJsonStringFormatPath(context.getHttpApi(), context.getHttpInterface());
            if (jsonStringFormatPath.length > 0){
                originBodyString = formatOriginBodyStringByJsonStringPath(originBodyString,jsonStringFormatPath);
            }

            originBodyString = super.postAfterBodyString(originBodyString,httpJsonResponse,context);
            httpJsonResponse.setJsonValue(originBodyString);

            //
            Type bodyResultType = getBodyResultType(context.getMethodInvocation());
            if (String.class.equals(bodyResultType) || void.class.equals(bodyResultType)){
                httpJsonResponse.setBodyResult(originBodyString);
                return httpJsonResponse;
            }

            //
            Object bodyResultObj = JSON.parseObject(originBodyString, bodyResultType);
            httpJsonResponse.setBodyResult(bodyResultObj);
            return httpJsonResponse;
    }

    private String formatOriginBodyStringByJsonStringPath(String originBodyString, String[] jsonStringFormatPath) {
        DocumentContext documentContext = JsonPath.parse(originBodyString);
        Map<String,Object> jsonPathMap = new HashMap<>(jsonStringFormatPath.length);
        for (String jsonPath : jsonStringFormatPath) {
            Object pathValue = documentContext.read(jsonPath);
            if (pathValue != null){
                jsonPathMap.put(jsonPath,pathValue);
            }
        }
        if (jsonPathMap.isEmpty()){
            return originBodyString;
        }
        for (Map.Entry<String, Object> entry : jsonPathMap.entrySet()) {
            documentContext.set(entry.getKey(),JSON.toJSONString(entry.getValue()));
        }
        return documentContext.toString();
    }

    protected String[] getResponseJsonStringFormatPath(HttpApi httpApi, HttpInterface httpInterface) {
        String[] pathFormat = httpInterface.responseJsonPathFormat();
        if (pathFormat.length > 0){
            return pathFormat;
        }
        return httpApi.responseJsonPathFormat();
    }
}
