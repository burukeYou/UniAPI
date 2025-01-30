package com.burukeyou.uniapi.http.core.conveter.request;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.support.arg.ParamWrapper;
import com.burukeyou.uniapi.util.ListsUtil;
import com.burukeyou.uniapi.util.StrUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            return convertToBody(param, annotation);
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
            return convertToBody(param, annotation);
        }

        String originJsonString = serialize2JsonString(param);
        JSONObject jsonObject = JSON.parseObject(originJsonString);
        map.forEach((field,jsonPathAnno) -> {
            Object value = jsonObject.get(field.getName());
            jsonObject.remove(field.getName());
            JSONPath.set(jsonObject, jsonPathAnno.value(),value);
        });

        return new HttpBodyJSON(wrapperJson(new ParamWrapper(jsonObject), annotation));
    }

    private String wrapperJson(Param param, BodyJsonPar annotation) {
        String jsonPath = annotation.value();
        if (StrUtil.isBlank(jsonPath)) {
            return serialize2JsonString(param);
        }
        if (!jsonPath.startsWith("$")){
            Map<String, Object> map = Collections.singletonMap(jsonPath, param.getValue());
            return JSON.toJSONString(map);
        }else {
            return JSON.toJSONString(createJsonForPath(jsonPath,param.getValue()));
        }
    }

    private static Object createJsonForPath(String jsonPath, Object value) {
        return JSONPath.set(new JSONObject(), jsonPath, value);
    }

    private HttpBodyJSON convertToBody(Param param, BodyJsonPar annotation) {
       return  new HttpBodyJSON(wrapperJson(param, annotation));
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
