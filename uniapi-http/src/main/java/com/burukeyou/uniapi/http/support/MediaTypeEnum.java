package com.burukeyou.uniapi.http.support;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum MediaTypeEnum {

    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_PDF("application/pdf"),

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_XML("text/xml"),

    APPLICATION_OCTET_STREAM("application/octet-stream"),


    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data"),

    ;
    private final String type;
    private static final String[] filePrefixArr = {"image","audio","video"};

    private static final String[] FILE_MEDIA_TYPE = {
            APPLICATION_OCTET_STREAM.getType(),
            "application/x-download",
            "application/pdf",
            "application/zip",
            "application/x-rar-compressed",
    };

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

    public static boolean isFileType(String contentType){
        final String mediaType = contentType.trim();
        if(StringUtils.isBlank(mediaType)){
            return false;
        }
        if (Arrays.stream(filePrefixArr).anyMatch(mediaType::startsWith)){
            return true;
        }
        return Arrays.stream(FILE_MEDIA_TYPE).anyMatch(mediaType::contains);
    }

    public static boolean isTextType(String contentType){
        final String mediaType = contentType.trim();
        if(StringUtils.isBlank(mediaType)){
            return false;
        }
        if (mediaType.startsWith("text")){
            return true;
        }
        // todo xml
        return mediaType.contains(APPLICATION_JSON.getType()) || mediaType.contains(APPLICATION_XML.getType());
    }

    public static boolean isJsonType(String contentType){
        final String mediaType = contentType.trim();
        if(StringUtils.isBlank(mediaType)){
            return false;
        }
        return mediaType.contains(APPLICATION_JSON.getType());
    }

}
