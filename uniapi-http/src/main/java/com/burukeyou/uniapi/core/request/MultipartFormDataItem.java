package com.burukeyou.uniapi.core.request;

import lombok.Data;

import java.io.File;

@Data
public class MultipartFormDataItem {

    private String key;
    private String value;
    private File file;

    private boolean fileFlag;

    public MultipartFormDataItem(String key, String value, File file, boolean fileFlag) {
        this.key = key;
        this.value = value;
        this.file = file;
        this.fileFlag = fileFlag;
    }
}
