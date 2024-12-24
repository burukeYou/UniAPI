package com.burukeyou.uniapi.http.core.conveter.response;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpJsonResponse;
import com.burukeyou.uniapi.http.support.HttpApiConfigContext;
import com.burukeyou.uniapi.http.support.HttpResponseConfig;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.MapFunction;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
            List<String> jsonStringFormatPath = getResponseJsonStringFormatPath(context);
            if (!CollectionUtils.isEmpty(jsonStringFormatPath)){
                originBodyString = formatOriginBodyStringByJsonStringPath2(originBodyString,jsonStringFormatPath);
            }

            // post after body string
            originBodyString = super.postAfterBodyString(originBodyString,httpJsonResponse,context);

            // after json string format path
            List<String> afterJsonStringFormatPath = getResponseAfterJsonStringFormatPath(context);
            if (!CollectionUtils.isEmpty(afterJsonStringFormatPath)){
                originBodyString  = formatOriginBodyStringByJsonStringPath2(originBodyString,afterJsonStringFormatPath);
            }

            // update value
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

    private String formatOriginBodyStringByJsonStringPath2(String originBodyString, List<String> jsonStringFormatPath) {
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

    protected List<String> getResponseJsonStringFormatPath(ResponseConvertContext context) {
        HttpApiConfigContext configContext = context.getConfigContext();
        HttpResponseConfig responseConfig = configContext.getHttpResponseConfig();
        if (responseConfig == null){
            return Collections.emptyList();
        }
        return responseConfig.getJsonPathUnPack();
    }

    protected static List<String> getResponseAfterJsonStringFormatPath(ResponseConvertContext context) {
        return Optional.ofNullable(context.getConfigContext())
                       .map(HttpApiConfigContext::getHttpResponseConfig)
                       .map(HttpResponseConfig::getAfterJsonPathUnPack)
                       .orElse(Collections.emptyList());
    }
}
