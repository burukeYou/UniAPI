package com.burukeyou.uniapi.http.core.conveter.request;

import java.util.Collections;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.StrUtil;

/**
 * @author  caizhihao
 */
public class BodyJsonParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyJsonPar> {

    public BodyJsonParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    public BodyJsonParHttpBodyConverter(HttpRequestBodyConverter nextParser, AbstractHttpMetadataParamFinder paramFinder) {
        super(nextParser, paramFinder);
    }

    @Override
    protected HttpBody doConvert(Param param, BodyJsonPar annotation) {
        String value = paramFinder.getArgFillValue(param.getValue()).toString();
        String jsonPath = annotation.value();
        if (StrUtil.isBlank(jsonPath)) {
            return new HttpBodyJSON(value);
        }

        if (!jsonPath.startsWith("$")){
            Map<String, String> map = Collections.singletonMap(jsonPath, value);
            value = JSON.toJSONString(map);
        }else {
            value = JSON.toJSONString(JSONPath.set(new JSONObject(),jsonPath,value));
        }
        return new HttpBodyJSON(value);
    }

}
