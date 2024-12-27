package com.burukeyou.uniapi.http.core.exception;

public class UniHttpResponseDeserializeException extends BaseUniHttpException {

    public UniHttpResponseDeserializeException() {
    }

    public UniHttpResponseDeserializeException(String message) {
        super(message);
    }

    public UniHttpResponseDeserializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniHttpResponseDeserializeException(Throwable cause) {
        super(cause);
    }

    public UniHttpResponseDeserializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
