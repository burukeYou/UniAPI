package com.burukeyou.uniapi.http.core.conveter.request;

import com.burukeyou.uniapi.http.annotation.param.BodyBinaryPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.exception.UniHttpRequestParamException;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyBinary;
import com.burukeyou.uniapi.support.arg.Param;
import org.springframework.core.io.InputStreamSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author  caizhihao
 */
public class BodyBinaryHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyBinaryPar> {

    public BodyBinaryHttpBodyConverter() {
    }

    public BodyBinaryHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }

    public BodyBinaryHttpBodyConverter(HttpRequestBodyConverter next, AbstractHttpMetadataParamFinder paramFinder) {
        super(next, paramFinder);
    }


    @Override
    protected HttpBody doConvert(Param param, BodyBinaryPar annotation) {
        return getHttpBodyBinaryForValue(param.getValue());
    }

    private HttpBodyBinary getHttpBodyBinaryForValue(Object argValue)  {
        InputStream inputStream = getInputStream(argValue);
        return new HttpBodyBinary(inputStream);
    }

    private InputStream getInputStream(Object argValue) {
        InputStream inputStream = null;
        try {
            if (argValue instanceof InputStream){
                inputStream = (InputStream) argValue;
            }else if (argValue instanceof File){
                inputStream = Files.newInputStream(((File) argValue).toPath());
            } else if (argValue instanceof InputStreamSource){
                inputStream = ((InputStreamSource) argValue).getInputStream();
            }else {
                throw new UniHttpRequestParamException("@BodyBinaryPar annotation only supports use on InputStream、File、InputStreamSource");
            }
        } catch (IOException e) {
            throw new UniHttpRequestParamException(e);
        }

        return inputStream;
    }
}
