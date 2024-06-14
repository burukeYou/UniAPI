package com.burukeyou.uniapi.http.core.response;


import lombok.Data;

@Data
public class HttpBinaryResponse extends AbstractHttpResponse {

    private String fileName;
    private byte[] file;

    public HttpBinaryResponse(String fileName, byte[] file) {
        this.fileName = fileName;
        this.file = file;
    }

    @Override
    public Object getReturnObj() {
        return this;
    }
}
