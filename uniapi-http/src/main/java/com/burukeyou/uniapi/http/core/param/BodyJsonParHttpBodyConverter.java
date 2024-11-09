package com.burukeyou.uniapi.http.core.param;

import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.support.arg.Param;

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
        return new HttpBodyJSON(paramFinder.getArgFillValue(param.getValue()).toString());
    }
}
