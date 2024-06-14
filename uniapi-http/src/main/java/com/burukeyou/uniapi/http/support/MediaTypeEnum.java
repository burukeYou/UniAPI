package com.burukeyou.uniapi.http.support;

import org.apache.commons.lang3.StringUtils;

public enum MediaTypeEnum {

    APPLICATION_JSON("application/json"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data"),

    ;

    private String type;

    MediaTypeEnum(String mediaType) {
        this.type = mediaType;
    }

    public String getType() {
        return type;
    }

    public String getChartSetType(){
        return this.type + ";charset=utf-8";
    }

    public boolean equalsMediaType(String mediaType) {
        if(StringUtils.isBlank(mediaType)){
            return false;
        }
        return mediaType.contains(this.type);
    }

}
