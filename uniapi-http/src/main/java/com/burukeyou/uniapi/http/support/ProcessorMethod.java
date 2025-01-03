package com.burukeyou.uniapi.http.support;

/**
 * @author ciahzihao
 */
public enum ProcessorMethod {

    BEFORE_HTTP_REQUEST("postBeforeHttpRequest"),
    BEFORE_SEND_HTTP_REQUEST("postBeforeSendHttpRequest"),

    SENDING_HTTP_REQUEST("postSendingHttpRequest"),

    AFTER_HTTP_RESPONSE("postAfterHttpResponse"),
    AFTER_HTTP_RESPONSE_BODY_STRING("postAfterHttpResponseBodyString"),
    AFTER_HTTP_RESPONSE_BODY_RESULT("postAfterHttpResponseBodyResult"),
    AFTER_METHOD_RETURN_VALUE("postAfterMethodReturnValue"),


    LIST_BEFORE_METHOD("postBeforeHttpRequest","postBeforeSendHttpRequest"),

    LIST_AFTER_METHOD("postAfterHttpResponse","postAfterHttpResponseBodyString","postAfterHttpResponseBodyResult","postAfterMethodReturnValue")
    ;

    private final String[] methodName;

    ProcessorMethod(String...methodName) {
        this.methodName = methodName;
    }

    public String[] getMethodName() {
        return methodName;
    }
}
