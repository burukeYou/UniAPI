package com.burukeyou.uniapi.http.support;


import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniHttpResponseParseInfo {

    private Object methodReturnValue;

    private HttpResponse<Object> httpResponse;

    private UniHttpResponse uniHttpResponse;

    private Object bodyResult;

    private Object futureInnerValue;

    public UniHttpResponseParseInfo() {
    }


}
