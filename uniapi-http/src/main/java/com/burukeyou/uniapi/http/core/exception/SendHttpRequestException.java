package com.burukeyou.uniapi.http.core.exception;

public class SendHttpRequestException extends BaseUniHttpException {

    private static final long serialVersionUID = -1103416804782816471L;

    public SendHttpRequestException() {
    }

    public SendHttpRequestException(String message) {
        super(message);
    }

    public SendHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendHttpRequestException(Throwable cause) {
        super(cause);
    }

    public SendHttpRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
