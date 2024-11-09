package com.burukeyou.uniapi.http.core.conveter.request;

import com.burukeyou.uniapi.http.annotation.param.BodyMultiPartPar;
import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyMultipart;
import com.burukeyou.uniapi.http.core.request.MultipartDataItem;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.Param;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        boolean nameExistFlag = StringUtils.isNotBlank(multipartParam.value());
        if (nameExistFlag && File.class.isAssignableFrom(param.getType())){
            MultipartDataItem dataItem = new MultipartDataItem(multipartParam.value(),null,(File)argValue,true);
            return new HttpBodyMultipart(Collections.singletonList(dataItem));
        } else if (paramFinder.isObjOrMap(param.getType())){
            return getHttpBodyMultipartFormData(argValue, param.getType());
        }else if (nameExistFlag){
            // 单个
            MultipartDataItem dataItem = new MultipartDataItem(multipartParam.value(),argValue.toString(),null,false);
            return new HttpBodyMultipart(Collections.singletonList(dataItem));
        }

        throw new BaseUniHttpException("use @BodyMultiPartPar for not support type for  " + param.getType().getName());
    }

    private HttpBodyMultipart getHttpBodyMultipartFormData(Object argValue, Class<?> argClass) {
        List<MultipartDataItem> dataItems = new ArrayList<>();
        ArgList argList = paramFinder.autoGetArgList(argValue);
        for (Param param : argList) {
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

            boolean isFile = paramFinder.isFileField(param);
            if (!isFile && paramFinder.isObjOrMap(param.getType())){
                // 非File的其他对象不处理
                continue;
            }

            if (!isFile){
                String fieldValueStr = (fieldValue == null ? null : fieldValue.toString());
                dataItems.add(new MultipartDataItem(fieldName,fieldValueStr,null,false));
                continue;
            }

            // 文件
            if (!param.getType().isArray() && !Collection.class.isAssignableFrom(param.getType())){
                File onefile = fieldValue == null ? null : (File)fieldValue;
                dataItems.add(new MultipartDataItem(fieldName,null,onefile,true));
                continue;
            }

            if (fieldValue == null){
                dataItems.add(new MultipartDataItem(fieldName,null,null,true));
                continue;
            }

            // 多文件拆成单个
            File[] fileArr = null;
            if (Collection.class.isAssignableFrom(param.getType())){
                fileArr = ((Collection<File>)fieldValue).toArray(new File[0]);
            }else {
                fileArr = (File[])fieldValue;
            }

            for (File file : fileArr) {
                dataItems.add(new MultipartDataItem(fieldName,null,file,true));
            }
        }
        return new HttpBodyMultipart(dataItems);
    }
}
