package com.burukeyou.uniapi.exception;

public class BaseDataApiException extends RuntimeException {

    public BaseDataApiException() {
    }

    public BaseDataApiException(String message) {
        super(message);
    }

    public BaseDataApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseDataApiException(Throwable cause) {
        super(cause);
    }

    public BaseDataApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
