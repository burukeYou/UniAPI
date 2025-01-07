package com.burukeyou.uniapi.http.support;

import java.util.Arrays;

/**
 * @author ciahzihao
 */
public enum ProcessorMethod {

    BEFORE_HTTP_REQUEST("postBeforeHttpRequest"),
    BEFORE_SEND_HTTP_REQUEST("postBeforeSendHttpRequest"),

    SENDING_HTTP_REQUEST("postSendingHttpRequest"),

    AFTER_HTTP_RESPONSE("postAfterHttpResponse"),
    AFTER_HTTP_RESPONSE_BODY_STRING("postAfterHttpResponseBodyString"),
    AFTER_HTTP_RESPONSE_BODY_STRING_DESERIALIZE("postAfterHttpResponseBodyStringDeserialize"),
    AFTER_HTTP_RESPONSE_BODY_RESULT("postAfterHttpResponseBodyResult"),
    AFTER_METHOD_RETURN_VALUE("postAfterMethodReturnValue"),


    LIST_BEFORE_METHOD(merge(BEFORE_HTTP_REQUEST,
                            BEFORE_SEND_HTTP_REQUEST)),
    LIST_AFTER_METHOD(merge(AFTER_HTTP_RESPONSE,
                            AFTER_HTTP_RESPONSE_BODY_STRING,
                            AFTER_HTTP_RESPONSE_BODY_STRING_DESERIALIZE,
                            AFTER_HTTP_RESPONSE_BODY_RESULT,
                            AFTER_METHOD_RETURN_VALUE)),
    ;

    private final String[] methodNames;

    ProcessorMethod(String...methodName) {
        this.methodNames = methodName;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    private static String[] merge(ProcessorMethod... processorMethods){
        if (processorMethods == null || processorMethods.length == 0){
            return new String[0];
        }
        return Arrays.stream(processorMethods).flatMap(e -> Arrays.stream(e.getMethodNames())).toArray(String[]::new);
    }
}
