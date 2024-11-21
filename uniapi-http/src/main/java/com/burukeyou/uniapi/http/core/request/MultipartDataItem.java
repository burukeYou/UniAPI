package com.burukeyou.uniapi.http.core.request;

import lombok.Data;

import java.io.File;
import java.io.InputStream;

@Data
public class MultipartDataItem {

    /**
     *  Form Field Name
     */
    private String key;

    /**
     *  Form Field value
     *        if file  support File 、 byte[]、 InputStream
     */
    private Object fieldValue;

    /**
     *  Form Field type, If true, it is the File field
     */
    private boolean fileFlag;

    public MultipartDataItem(String key, Object fieldValue, boolean fileFlag) {
        this.key = key;
        this.fieldValue = fieldValue;
        this.fileFlag = fileFlag;
    }

    public String getFileValueString(){
        if (fieldValue == null){
            return "";
        }
        if (fieldValue instanceof File){
            return ((File) fieldValue).getAbsolutePath();
        }
        if (fieldValue instanceof byte[]){
            return "byte[]@" + ((byte[]) fieldValue).length;
        }
        if (fieldValue instanceof InputStream){
            return "InputStream@" + fieldValue;
        }
        return fieldValue.getClass().getSimpleName();
    }

    public String getValueString(){
        if (fieldValue == null){
            return "";
        }
        return fileFlag ? getFileValueString() : fieldValue.toString();
    }
}
