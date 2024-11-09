package com.burukeyou.uniapi.http.core.exception;

import com.burukeyou.uniapi.exception.BaseUniApiException;

public class BaseUniHttpException extends BaseUniApiException {

    private static final long serialVersionUID = -622059889685753804L;

    public BaseUniHttpException() {
    }

    public BaseUniHttpException(String message) {
        super(message);
    }

    public BaseUniHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseUniHttpException(Throwable cause) {
        super(cause);
    }

    public BaseUniHttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
