package com.burukeyou.uniapi.core.exception;

import com.burukeyou.uniapi.exception.BaseDataApiException;

public class HttpRequestParamException extends BaseDataApiException {

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
