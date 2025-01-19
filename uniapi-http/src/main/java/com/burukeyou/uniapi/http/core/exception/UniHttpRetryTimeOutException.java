package com.burukeyou.uniapi.http.core.exception;

import com.burukeyou.uniapi.exception.BaseUniApiException;

public class UniHttpRetryTimeOutException extends BaseUniApiException {

    public UniHttpRetryTimeOutException() {
    }

    public UniHttpRetryTimeOutException(String message) {
        super(message);
    }

    public UniHttpRetryTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniHttpRetryTimeOutException(Throwable cause) {
        super(cause);
    }

    public UniHttpRetryTimeOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
