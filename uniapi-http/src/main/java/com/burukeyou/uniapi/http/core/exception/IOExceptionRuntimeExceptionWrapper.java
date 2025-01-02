package com.burukeyou.uniapi.http.core.exception;

public class IOExceptionRuntimeExceptionWrapper extends RuntimeException {

    public IOExceptionRuntimeExceptionWrapper() {
    }

    public IOExceptionRuntimeExceptionWrapper(String message) {
        super(message);
    }

    public IOExceptionRuntimeExceptionWrapper(String message, Throwable cause) {
        super(message, cause);
    }

    public IOExceptionRuntimeExceptionWrapper(Throwable cause) {
        super(cause);
    }

    public IOExceptionRuntimeExceptionWrapper(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
