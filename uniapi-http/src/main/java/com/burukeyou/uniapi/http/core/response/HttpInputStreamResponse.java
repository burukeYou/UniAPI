package com.burukeyou.uniapi.http.core.response;

import lombok.Data;

import java.io.InputStream;

@Data
public class HttpInputStreamResponse extends AbstractHttpResponse<InputStream> {

    private InputStream inputStream;

    private String fileName;

    public HttpInputStreamResponse(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.result = inputStream;
    }

    @Override
    public String bodyResultString() {
        return inputStream == null ? "" : inputStream.toString();
    }
}
