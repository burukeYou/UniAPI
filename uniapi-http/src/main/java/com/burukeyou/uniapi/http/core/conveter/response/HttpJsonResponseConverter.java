package com.burukeyou.uniapi.http.core.conveter.response;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpJsonResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.jayway.jsonpath.*;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

            // json path field string to json object
            String[] jsonStringFormatPath = getResponseJsonStringFormatPath(context);
            if (jsonStringFormatPath != null && jsonStringFormatPath.length > 0){
                originBodyString = formatOriginBodyStringByJsonStringPath2(originBodyString,jsonStringFormatPath);
            }

            // post after body string
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
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        DocumentContext documentContext = JsonPath.using(conf).parse(originBodyString);
        Map<String,Object> jsonPathMap = new HashMap<>(jsonStringFormatPath.length);
        for (String jsonPath : jsonStringFormatPath) {
            Object pathValue = null;
            pathValue = documentContext.read(jsonPath);

            if (pathValue == null){
                continue;
            }

            // net.minidev.json.JSONArray
            Object parseObject = null;
            if (Collection.class.isAssignableFrom(pathValue.getClass())){
                Collection<?> valueCollection = (Collection<?>) pathValue;
                if (valueCollection.isEmpty()){
                    continue;
                }
                parseObject = valueCollection.stream().filter(this::isJsonString).map(e -> JSON.parse(e.toString())).collect(Collectors.toList());
            }else if (isJsonString(pathValue)){
                parseObject = JSON.parse(pathValue.toString());
            }
            if (parseObject != null){
                jsonPathMap.put(jsonPath, parseObject);
            }
        }
        if (jsonPathMap.isEmpty()){
            return originBodyString;
        }
        for (Map.Entry<String, Object> entry : jsonPathMap.entrySet()) {
            documentContext = documentContext.set(entry.getKey(),entry.getValue());
        }
        return documentContext.jsonString();
    }

    private String formatOriginBodyStringByJsonStringPath2(String originBodyString, String[] jsonStringFormatPath) {
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        DocumentContext documentContext = JsonPath.using(conf).parse(originBodyString);
        for (String jsonPath : jsonStringFormatPath) {
            try {
                documentContext.map(jsonPath, new MapFunction() {
                    @Override
                    public Object map(Object currentValue, Configuration configuration) {
                        if (isJsonString(currentValue)){
                            return JSON.parse(currentValue.toString());
                        }
                        return currentValue;
                    }
                });
            } catch (PathNotFoundException e) {
                // ignore
            }
        }

        return documentContext.jsonString();
    }

    private boolean isJsonString(Object value){
        return value != null && value.getClass().equals(String.class) && JSON.isValid(value.toString());
    }

    protected String[] getResponseJsonStringFormatPath(ResponseConvertContext context) {
        // todo cache
        Method method = context.getMethodInvocation().getMethod();
        HttpResponseCfg responseConfig = method.getAnnotation(HttpResponseCfg.class);
        if (responseConfig != null && responseConfig.jsonPathUnPack().length > 0){
            return responseConfig.jsonPathUnPack();
        }
        HttpResponseCfg[] responseConfigArr = context.getHttpApi().responseConfig();
        if (responseConfigArr == null || responseConfigArr.length == 0){
            return null;
        }
        return responseConfigArr[0].jsonPathUnPack();
    }
}
