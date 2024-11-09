package com.burukeyou.uniapi.http.core.exception;

public class UniHttpRequestParamException extends BaseUniHttpException {

    private static final long serialVersionUID = -9178705957413908945L;

    public UniHttpRequestParamException() {
    }

    public UniHttpRequestParamException(String message) {
        super(message);
    }

    public UniHttpRequestParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniHttpRequestParamException(Throwable cause) {
        super(cause);
    }

    public UniHttpRequestParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
