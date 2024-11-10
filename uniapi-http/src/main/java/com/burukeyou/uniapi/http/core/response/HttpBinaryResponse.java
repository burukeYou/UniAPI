package com.burukeyou.uniapi.http.core.response;


import lombok.Data;

@Data
public class HttpBinaryResponse extends AbstractHttpResponse<byte[]> implements HttpFileResponse<byte[]> {

    private String fileName;
    private byte[] file;

    public HttpBinaryResponse(String fileName, byte[] file) {
        this.fileName = fileName;
        this.file = file;
        this.bodyResult = file;
    }

    @Override
    public String bodyResultString() {
        return file == null ? "" : fileName;
    }
}
