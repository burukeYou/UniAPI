package com.burukeyou.uniapi.http.core.response;


import lombok.Data;

import java.io.File;

@Data
public class HttpFileResponse extends AbstractHttpResponse<File> {

    private File file;

    public HttpFileResponse(File file) {
        this.file = file;
        this.bodyResult = file;
    }

    @Override
    public String bodyResultString() {
        return file == null ? "" : file.getAbsolutePath();
    }
}
