package com.burukeyou.uniapi.http.core.response;


import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpBinaryResponse extends AbstractHttpResponse<byte[]> implements HttpFileResponse<byte[]> {

    private String fileName;
    private byte[] file;

    public HttpBinaryResponse(String fileName, byte[] file, ResponseConvertContext context) {
        super(context);
        this.fileName = fileName;
        this.file = file;
        this.bodyResult = file;
    }

    @Override
    public String bodyResultString() {
        return file == null ? "" : fileName;
    }
}
