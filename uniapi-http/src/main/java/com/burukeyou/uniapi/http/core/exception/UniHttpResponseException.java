package com.burukeyou.uniapi.http.core.exception;

public class UniHttpResponseException extends BaseUniHttpException {

    private static final long serialVersionUID = 8533426505149800812L;


    public UniHttpResponseException() {
    }

    public UniHttpResponseException(String message) {
        super(message);
    }

    public UniHttpResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniHttpResponseException(Throwable cause) {
        super(cause);
    }

    public UniHttpResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
