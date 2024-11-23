package com.burukeyou.uniapi.http.core.conveter.request;

import com.burukeyou.uniapi.http.annotation.param.BodyTextPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyText;
import com.burukeyou.uniapi.support.arg.Param;

/**
 * @author  caizhihao
 */
public class BodyTextParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyTextPar> {


    public BodyTextParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    public BodyTextParHttpBodyConverter(HttpRequestBodyConverter next, AbstractHttpMetadataParamFinder paramFinder) {
        super(next, paramFinder);
    }

    @Override
    protected HttpBody doConvert(Param param, BodyTextPar annotation) {
        Object value = param.getValue();
        return new HttpBodyText(value == null ? "" : value.toString());
    }
}
