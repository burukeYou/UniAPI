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
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.Param;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author  caizhihao
 */
public class BodyMultiPartParHttpBodyConverter extends AbstractHttpRequestBodyConverter<BodyMultiPartPar> {

    public BodyMultiPartParHttpBodyConverter(AbstractHttpMetadataParamFinder paramFinder) {
        super(paramFinder);
    }


    @Override
    protected HttpBody doConvert(Param param, BodyMultiPartPar multipartParam) {
        return new HttpBodyMultipart(processBodyMultiParAnnoItem(param, multipartParam));
    }

    private List<MultipartDataItem> processBodyMultiParAnnoItem(Param param, BodyMultiPartPar multipartParam) {
        List<MultipartDataItem> dataItems;
        if (param.isObjectOrMap()){
             dataItems = getHttpBodyMultipartFormData(param.getValue(), param.getType());
        }else {
             dataItems = Collections.singletonList(getOneMultipartDataItem(param, multipartParam));
        }
        return dataItems;
    }

    private static MultipartDataItem getOneMultipartDataItem(Param param, BodyMultiPartPar multipartParam) {
        Object argValue = param.getValue();
        boolean nameExistFlag = StringUtils.isNotBlank(multipartParam.value());
        if (!nameExistFlag){
            throw new BaseUniHttpException("use @BodyMultiPartPar for type " + param.getType().getName() + "must have name");
        }
        MultipartDataItem multipartDataItem = null;
        if (isFileType(param)){
            multipartDataItem = MultipartDataItem.ofFile(multipartParam.value(), argValue, multipartParam.fileName());
        }else {
            multipartDataItem = MultipartDataItem.ofText(multipartParam.value(), argValue.toString());
        }
        return multipartDataItem;
    }

    private static boolean isFileType(Param param) {
        return BizUtil.isFileForClass(param.getType());
    }

    private  List<MultipartDataItem>  getHttpBodyMultipartFormData(Object argValue, Class<?> argClass) {
        List<MultipartDataItem> dataItems = new ArrayList<>();
        ArgList argList = paramFinder.autoGetArgList(argValue);
        for (Param param : argList) {
            if (param.isObjectOrMap()){
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

            BodyMultiPartPar subBodyParAnno = param.getAnnotation(BodyMultiPartPar.class);
            String fileName = "";
            if (subBodyParAnno != null){
                fileName = subBodyParAnno.fileName();
            }

            if (BizUtil.isFileForClass(fieldType)){
                dataItems.add(MultipartDataItem.ofFile(fieldName,fieldValue,fileName));
                continue;
            }
            if (param.isCollection(File.class)){
                for (File file : param.castListValue(File.class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,file,fileName));
                }
            }
            if (param.isCollection(byte[].class)){
                for (byte[] bytes : param.castListValue(byte[].class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,bytes,fileName));
                }
            }
            if (param.isCollection(InputStream.class)){
                for (InputStream inputStream : param.castListValue(InputStream.class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,inputStream,fileName));
                }
            }

            // not file
            if (param.isNormalValue()){
                dataItems.add(MultipartDataItem.ofText(fieldName,fieldValue == null ? null : fieldValue.toString()));
            }

            // 复合对象
            if (subBodyParAnno != null){
                List<MultipartDataItem> items = processBodyMultiParAnnoItem(param, subBodyParAnno);
                if (!CollectionUtils.isEmpty(items)){
                    dataItems.addAll(items);
                }
            }
        }
        return dataItems;
    }

}
