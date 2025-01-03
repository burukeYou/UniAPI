package com.burukeyou.uniapi.http.support;


import com.burukeyou.uniapi.http.core.response.HttpResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniHttpResponseParseInfo {

    private Object methodReturnValue;

    private HttpResponse<Object> httpResponse;

    public UniHttpResponseParseInfo() {
    }


}
