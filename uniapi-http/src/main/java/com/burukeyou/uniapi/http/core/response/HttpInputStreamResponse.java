package com.burukeyou.uniapi.http.core.response;

import lombok.Data;

import java.io.InputStream;

@Data
public class HttpInputStreamResponse extends AbstractHttpResponse {

    private InputStream inputStream;

    private String fileName;

    public HttpInputStreamResponse(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    @Override
    public Object getReturnObj() {
        return ifReturnOriginalResponse() ? this : inputStream;
    }
}
