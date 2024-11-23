package com.burukeyou.uniapi.http.core.conveter.request;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.burukeyou.uniapi.http.annotation.param.BodyMultiPartPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyMultipart;
import com.burukeyou.uniapi.http.core.request.MultipartDataItem;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.Param;
import org.apache.commons.lang3.StringUtils;

/**
 * @author  caizhihao
 */
public class BodyMultiPartParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyMultiPartPar> {

    public BodyMultiPartParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }


    @Override
    protected HttpBody doConvert(Param param, BodyMultiPartPar multipartParam) {
        Object argValue = param.getValue();
        if (paramFinder.isObjOrMap(param.getType())){
            return getHttpBodyMultipartFormData(argValue, param.getType());
        }

        boolean nameExistFlag = StringUtils.isNotBlank(multipartParam.value());
        if (!nameExistFlag){
            throw new BaseUniHttpException("use @BodyMultiPartPar for type " + param.getType().getName() + "must have name");
        }

        if (isFileType(param)){
            MultipartDataItem dataItem = new MultipartDataItem(multipartParam.value(), argValue,true);
            return new HttpBodyMultipart(Collections.singletonList(dataItem));
        }

        MultipartDataItem dataItem = new MultipartDataItem(multipartParam.value(),argValue.toString(),false);
        return new HttpBodyMultipart(Collections.singletonList(dataItem));
    }

    private static boolean isFileType(Param param) {
        return byte[].class.equals(param.getType())
                || File.class.isAssignableFrom(param.getType())
                || InputStream.class.isAssignableFrom(param.getType());
    }

    private HttpBodyMultipart getHttpBodyMultipartFormData(Object argValue, Class<?> argClass) {
        List<MultipartDataItem> dataItems = new ArrayList<>();
        ArgList argList = paramFinder.autoGetArgList(argValue);
        for (Param param : argList) {
            if (paramFinder.isObjOrMap(param.getType())){
                continue;
            }

            Class<?> fieldType = param.getType();
            Object fieldValue = param.getValue();
            com.alibaba.fastjson.annotation.JSONField jsonField = param.getAnnotation(com.alibaba.fastjson.annotation.JSONField.class);
            com.alibaba.fastjson2.annotation.JSONField jsonField2 = param.getAnnotation(com.alibaba.fastjson2.annotation.JSONField.class);
            String fieldName = param.getName();
            if (jsonField != null){
                fieldName = jsonField.name();
            }
            if (jsonField2 != null){
                fieldName = jsonField2.name();
            }

            if (File.class.equals(fieldType) || byte[].class.equals(fieldType) || InputStream.class.equals(fieldType)){
                dataItems.add(new MultipartDataItem(fieldName,fieldValue,true));
                continue;
            }
            if (param.isCollection(File.class)){
                for (File file : param.castListValue(File.class)) {
                    dataItems.add(new MultipartDataItem(fieldName,file,true));
                }
            }
            if (param.isCollection(byte[].class)){
                for (byte[] bytes : param.castListValue(byte[].class)) {
                    dataItems.add(new MultipartDataItem(fieldName,bytes,true));
                }
            }
            if (param.isCollection(InputStream.class)){
                for (InputStream inputStream : param.castListValue(InputStream.class)) {
                    dataItems.add(new MultipartDataItem(fieldName,inputStream,true));
                }
            }

            if (fieldValue == null){
                dataItems.add(new MultipartDataItem(fieldName,null,true));
            }

            if (param.isNormalValue()){
                dataItems.add(new MultipartDataItem(fieldName,fieldValue == null ? null : fieldValue.toString(),false));
            }
        }
        return new HttpBodyMultipart(dataItems);
    }

}
