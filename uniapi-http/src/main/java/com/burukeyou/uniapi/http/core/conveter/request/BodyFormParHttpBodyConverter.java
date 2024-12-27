package com.burukeyou.uniapi.http.core.conveter.request;

import java.util.Collections;

import com.burukeyou.uniapi.http.annotation.param.BodyFormPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyFormData;
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.support.arg.Param;
import org.apache.commons.lang3.StringUtils;

/**
 * @author  caizhihao
 */
public class BodyFormParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyFormPar> {


    public BodyFormParHttpBodyConverter() {
    }

    public BodyFormParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    protected BodyFormParHttpBodyConverter(HttpRequestBodyConverter next, AbstractHttpMetadataParamFinder paramFinder) {
        super(next, paramFinder);
    }

    @Override
    protected HttpBody doConvert(Param param, BodyFormPar annotation) {
        Object argValue = param.getValue();
        if (param.isObjectOrMap()){
            return new HttpBodyFormData(BizUtil.objToMap(argValue));
        }else if (!param.isCollection()){
            if (StringUtils.isBlank(annotation.value())){
                throw new BaseUniHttpException("use @BodyFormPar for single value please specify the parameter name ");
            }
            // 单个
            return new HttpBodyFormData(Collections.singletonMap(annotation.value(),paramFinder.getArgFillValue(argValue).toString()));
        }

        throw new BaseUniHttpException("use @BodyFormPar for not support type for  " + param.getType().getName());
    }
}
