package com.burukeyou.uniapi.http.core.exception;

public class UniHttpIOException extends RuntimeException {

    public UniHttpIOException() {
    }

    public UniHttpIOException(String message) {
        super(message);
    }

    public UniHttpIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniHttpIOException(Throwable cause) {
        super(cause);
    }

    public UniHttpIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
