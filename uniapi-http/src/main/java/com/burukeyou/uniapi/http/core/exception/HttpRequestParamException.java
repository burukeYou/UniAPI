package com.burukeyou.uniapi.http.core.exception;

import com.burukeyou.uniapi.exception.BaseUniApiException;

public class HttpRequestParamException extends BaseUniApiException {

    public HttpRequestParamException() {
    }

    public HttpRequestParamException(String message) {
        super(message);
    }

    public HttpRequestParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestParamException(Throwable cause) {
        super(cause);
    }

    public HttpRequestParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
