package com.burukeyou.uniapi.http.core.request;

import java.io.File;
import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
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

    /**
     *  File Name
     */
    private String fileName;

    public MultipartDataItem(String key, Object fieldValue, boolean fileFlag, String fileName) {
        this.key = key;
        this.fieldValue = fieldValue;
        this.fileFlag = fileFlag;
        this.fileName = fileName;
    }


    public static MultipartDataItem ofFile(String key, Object fieldValue){
        return ofFile(key, fieldValue, "");
    }

    public static MultipartDataItem ofFile(String key, Object fieldValue, String fileName){
        if (StringUtils.isBlank(fileName) && fieldValue != null && File.class.isAssignableFrom(fieldValue.getClass())){
            fileName = ((File) fieldValue).getName();
        }
        return new MultipartDataItem(key, fieldValue, true,fileName);
    }

    public static MultipartDataItem ofText(String key, Object fieldValue){
        return new MultipartDataItem(key, fieldValue, false,"");
    }


    public String getFileValueString(){
        if (fieldValue == null){
            return "";
        }
        if (fieldValue instanceof File){
            return ((File) fieldValue).getAbsolutePath();
        }
        if (fieldValue instanceof byte[]){
            return  "byte[]@ Length:" + ((byte[]) fieldValue).length  + "    fileName: "  + fileName;
        }
        if (fieldValue instanceof InputStream){
            return "InputStream@" + fieldValue  + "  fileName: "  + fileName;
        }
        return fieldValue.getClass().getSimpleName();
    }

    public String getValueString(){
        if (fieldValue == null){
            return "";
        }
        return fileFlag ? getFileValueString() : fieldValue.toString();
    }

    public String getFileName() {
        if (StringUtils.isNotBlank(fileName)){
            return fileName;
        }
        if (fieldValue != null && fieldValue instanceof File){
            return ((File) fieldValue).getName();
        }
        return fileName;
    }
}
