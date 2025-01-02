package com.burukeyou.uniapi.http.support;

import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caizhihao
 */
@Getter
@Setter
public class HttpRequestExecuteInfo {

    private Throwable exception;

    private UniHttpResponse uniHttpResponse;

    public HttpRequestExecuteInfo() {
    }

    public HttpRequestExecuteInfo(Throwable exception, UniHttpResponse uniHttpResponse) {
        this.exception = exception;
        this.uniHttpResponse = uniHttpResponse;
    }
}
