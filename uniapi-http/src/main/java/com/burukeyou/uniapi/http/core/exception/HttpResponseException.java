package com.burukeyou.uniapi.http.core.exception;

public class HttpResponseException extends BaseUniHttpException {

    private static final long serialVersionUID = -1103416804782816471L;

    public HttpResponseException() {
    }

    public HttpResponseException(String message) {
        super(message);
    }

    public HttpResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpResponseException(Throwable cause) {
        super(cause);
    }

    public HttpResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
