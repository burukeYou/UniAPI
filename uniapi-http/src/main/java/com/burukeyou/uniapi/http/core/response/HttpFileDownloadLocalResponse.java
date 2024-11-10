package com.burukeyou.uniapi.http.core.response;


import lombok.Data;

import java.io.File;

@Data
public class HttpFileDownloadLocalResponse extends AbstractHttpResponse<File> implements HttpFileResponse<File> {

    private File file;

    public HttpFileDownloadLocalResponse(File file) {
        this.file = file;
        this.bodyResult = file;
    }

    @Override
    public String bodyResultString() {
        return file == null ? "" : file.getAbsolutePath();
    }

    @Override
    public String getFileName() {
        return file.getName();
    }
}
