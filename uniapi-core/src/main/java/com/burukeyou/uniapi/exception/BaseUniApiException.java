package com.burukeyou.uniapi.exception;

public class BaseUniApiException extends RuntimeException {

    public BaseUniApiException() {
    }

    public BaseUniApiException(String message) {
        super(message);
    }

    public BaseUniApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseUniApiException(Throwable cause) {
        super(cause);
    }

    public BaseUniApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
