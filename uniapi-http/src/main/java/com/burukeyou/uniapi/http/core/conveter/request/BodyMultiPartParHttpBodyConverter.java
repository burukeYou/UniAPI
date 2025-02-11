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
import com.burukeyou.uniapi.http.support.HttpFile;
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.ListsUtil;
import com.burukeyou.uniapi.util.StrUtil;
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
        if (param.isObjectOrMap() && !HttpFile.class.isAssignableFrom(param.getType())){
             dataItems = buildItemForObjOrMap(param.getValue(), param.getType());
        }else if (param.isCollection() && byte[].class != param.getType()){
            List<Object> itemList = param.castListValue(Object.class);
            dataItems = buildItemForCollection(itemList,multipartParam);
        }else {
             dataItems = Collections.singletonList(getOneMultipartDataItem(param, multipartParam));
        }
        return dataItems;
    }

    private List<MultipartDataItem> buildItemForCollection(List<Object> itemList, BodyMultiPartPar multipartParam) {
        if (ListsUtil.isEmpty(itemList)){
            return Collections.emptyList();
        }
        List<MultipartDataItem> items = new ArrayList<>(itemList.size());
        for (Object item : itemList) {
            if (item == null){
                continue;
            }
            MultipartDataItem multipartDataItem = null;
            if (isFileType(item.getClass())){
                multipartDataItem = MultipartDataItem.ofFile(multipartParam.value(), item, multipartParam.fileName());
            }else {
                multipartDataItem = MultipartDataItem.ofText(multipartParam.value(), item.toString());
            }
            items.add(multipartDataItem);
        }
        return items;
    }

    private static MultipartDataItem getOneMultipartDataItem(Param param, BodyMultiPartPar multipartParam) {
        Object argValue = param.getValue();
        boolean nameExistFlag = StringUtils.isNotBlank(multipartParam.value());
        if (!nameExistFlag){
            throw new BaseUniHttpException("use @BodyMultiPartPar for type " + param.getType().getName() + "must have name");
        }
        MultipartDataItem multipartDataItem = null;
        if (isFileType(param.getType())){
            multipartDataItem = MultipartDataItem.ofFile(multipartParam.value(), argValue, multipartParam.fileName());
        }else {
            multipartDataItem = MultipartDataItem.ofText(multipartParam.value(), argValue.toString());
        }
        return multipartDataItem;
    }

    private static boolean isFileType(Class<?> clz) {
        return BizUtil.isFileForClass(clz);
    }

    private  List<MultipartDataItem> buildItemForObjOrMap(Object argValue, Class<?> argClass) {
        List<MultipartDataItem> dataItems = new ArrayList<>();
        ArgList argList = paramFinder.autoGetArgList(argValue);
        for (Param param : argList) {
            if (param.isObjectOrMap() && !HttpFile.class.isAssignableFrom(param.getType())){
                continue;
            }

            Class<?> fieldType = param.getType();
            Object fieldValue = param.getValue();
            com.alibaba.fastjson2.annotation.JSONField jsonField2 = param.getAnnotation(com.alibaba.fastjson2.annotation.JSONField.class);
            String fieldName = param.getName();
            if (jsonField2 != null){
                fieldName = jsonField2.name();
            }

            BodyMultiPartPar subBodyParAnno = param.getAnnotation(BodyMultiPartPar.class);
            String fileName = "";
            if (subBodyParAnno != null){
                fileName = subBodyParAnno.fileName();
                if (StrUtil.isNotBlank(subBodyParAnno.value())){
                    fieldName = subBodyParAnno.value();
                }
            }

            if (BizUtil.isFileForClass(fieldType)){
                dataItems.add(MultipartDataItem.ofFile(fieldName,fieldValue,fileName));
                continue;
            }else if (param.isCollection(File.class)){
                for (File file : param.castListValue(File.class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,file,fileName));
                }
                continue;
            }else if (param.isCollection(byte[].class)){
                for (byte[] bytes : param.castListValue(byte[].class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,bytes,fileName));
                }
                continue;
            }else if (param.isCollection(InputStream.class)){
                for (InputStream inputStream : param.castListValue(InputStream.class)) {
                    dataItems.add(MultipartDataItem.ofFile(fieldName,inputStream,fileName));
                }
                continue;
            }else if (param.isCollection(HttpFile.class)){
                for (HttpFile httpFile : param.castListValue(HttpFile.class)) {
                    String uploadFileName = StrUtil.isNotBlank(httpFile.getFileName()) ? httpFile.getFileName() : fileName;
                    dataItems.add(MultipartDataItem.ofFile(fieldName,httpFile.getFile(),uploadFileName));
                }
                continue;
            }

            if (param.isNormalValue()){
                // not file
                dataItems.add(MultipartDataItem.ofText(fieldName,fieldValue == null ? null : fieldValue.toString()));
                continue;
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
