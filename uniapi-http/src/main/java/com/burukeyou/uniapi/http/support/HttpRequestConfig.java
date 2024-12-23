package com.burukeyou.uniapi.http.support;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequestConfig implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *  If the format of the Http request body is json，and you want to convert some fields of the JSON from objects to JSON String,
     *  you can use this method to set the path list of the JSON fields to be converted
     */
    private List<String> jsonPathPack;

}
