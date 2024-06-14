package com.burukeyou.uniapi.core.response;


import lombok.Data;

import java.io.File;

@Data
public class HttpFileResponse extends AbstractHttpResponse {

    private File file;

    public HttpFileResponse(File file) {
        this.file = file;
    }

    @Override
    public Object getReturnObj() {
        return ifReturnOriginalResponse() ? this : file;
    }
}
