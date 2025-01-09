package com.burukeyou.uniapi.http.core.conveter.request;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.ListsUtil;
import com.burukeyou.uniapi.util.StrUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author  caizhihao
 */
public class BodyJsonParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyJsonPar> {

    public BodyJsonParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    @Override
    protected HttpBody doConvert(Param param, BodyJsonPar annotation) {
        boolean need = param.isObject() && (param.isAnnotationPresent(ModelBinding.class) || param.getType().isAnnotationPresent(ModelBinding.class));
        if (!need) {
            return convertToBody(serialize2JsonString(param), annotation);
        }

        Class<?> modelClass = param.getType();
        Map<Field,JsonPathMapping> map = new HashMap<>();
        ReflectionUtils.doWithFields(modelClass, field -> {
            if(Modifier.isStatic(field.getModifiers())){
                return;
            }
            JsonPathMapping jsonPath = AnnotatedElementUtils.getMergedAnnotation(field, JsonPathMapping.class);
            if (jsonPath != null && StrUtil.isNotBlank(jsonPath.value())) {
                map.put(field, jsonPath);
            }
        });

        if (ListsUtil.isEmpty(map)) {
            String jsonValue = serialize2JsonString(param);
            return convertToBody(jsonValue, annotation);
        }

        String originJsonString = serialize2JsonString(param);
        JSONObject jsonObject = JSON.parseObject(originJsonString);
        map.forEach((field,jsonPathAnno) -> {
            Object value = jsonObject.get(field.getName());
            jsonObject.remove(field.getName());
            JSONPath.set(jsonObject, jsonPathAnno.value(),value);
        });

        return new HttpBodyJSON(wrapperJson(jsonObject.toJSONString(), annotation));
    }

    private String wrapperJson(String jsonValue, BodyJsonPar annotation) {
        String jsonPath = annotation.value();
        if (StrUtil.isBlank(jsonPath)) {
            return jsonValue;
        }
        if (!jsonPath.startsWith("$")){
            Map<String, String> map = Collections.singletonMap(jsonPath, jsonValue);
            jsonValue = JSON.toJSONString(map);
        }else {
            jsonValue = JSON.toJSONString(createJsonForPath(jsonPath,jsonValue));
        }
        return jsonValue;
    }

    private static Object createJsonForPath(String jsonPath, Object value) {
        return JSONPath.set(new JSONObject(), jsonPath, value);
    }

    private HttpBodyJSON convertToBody(String jsonValue, BodyJsonPar annotation) {
       return  new HttpBodyJSON(wrapperJson(jsonValue, annotation));
    }


    private String serialize2JsonString(Param param) {
        Object obj = param.getValue();
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (param.isObjectOrMap() || param.isCollection()) {
            return paramFinder.serialize2JsonString(obj);
        }
        return obj.toString();
    }
}
