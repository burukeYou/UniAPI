package com.burukeyou.uniapi.http.core.exception;

import com.burukeyou.uniapi.exception.BaseDataApiException;

public class SendHttpRequestException extends BaseDataApiException {

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
