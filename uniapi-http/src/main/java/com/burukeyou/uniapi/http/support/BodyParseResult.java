package com.burukeyou.uniapi.http.support;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodyParseResult {

    private String originBodyPrintString = "";
    private Object bodyResult = null;

    public BodyParseResult() {
    }

    public BodyParseResult(String originBodyPrintString, Object bodyResult) {
        this.originBodyPrintString = originBodyPrintString;
        this.bodyResult = bodyResult;
    }
}
