package com.burukeyou.uniapi.http.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class HttpResponseConfig implements Serializable {

    private static final long serialVersionUID = -6769135220896976117L;

    /**
     *  If the response format of the http-interface is JSON,
     *  and you want to convert some fields of the JSON from JSON strings to JSON objects,
     *  you can use this method to set the path list of the JSON fields to be converted
     */
    private List<String> jsonPathUnPack;
}
