package com.burukeyou.uniapi.http.core.conveter.request;

import com.burukeyou.uniapi.http.annotation.param.BodyXmlPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyXML;
import com.burukeyou.uniapi.http.core.serialize.xml.XmlSerializeConverter;
import com.burukeyou.uniapi.support.arg.Param;

public class BodyXmlParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyXmlPar> {

    public BodyXmlParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    @Override
    protected HttpBody doConvert(Param param, BodyXmlPar annotation) {
        XmlSerializeConverter xmlSerializeConverter = paramFinder.getXmlSerializeConverter();
        Object value = param.getValue();
        String xmlString = xmlSerializeConverter.serialize(value);
        return new HttpBodyXML(xmlString);
    }
}
