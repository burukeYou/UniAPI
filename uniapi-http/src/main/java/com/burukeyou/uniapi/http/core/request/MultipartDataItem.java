package com.burukeyou.uniapi.http.core.request;

import lombok.Data;

import java.io.File;

@Data
public class MultipartDataItem {

    /**
     *  Form Field Name
     */
    private String key;

    /**
     *  Form Field text value
     */
    private String textValue;

    /**
     *  Form Field file value
     */
    private File fileValue;

    /**
     *  Form Field type, If true, it is the File field
     */
    private boolean fileFlag;

    public MultipartDataItem(String key, String textValue, File fileValue, boolean fileFlag) {
        this.key = key;
        this.textValue = textValue;
        this.fileValue = fileValue;
        this.fileFlag = fileFlag;
    }
}
