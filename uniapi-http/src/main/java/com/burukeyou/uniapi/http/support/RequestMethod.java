package com.burukeyou.uniapi.http.support;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    public boolean needBody(){
        return Arrays.asList(POST,PUT).contains(this);
    }

}
