package com.burukeyou.uniapi.http.core.exception;

public class HttpRequestParamException extends BaseUniHttpException {

    private static final long serialVersionUID = -9178705957413908945L;

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
