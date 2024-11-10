package com.burukeyou.uniapi.http.core.response;


import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class HttpFileDownloadLocalResponse extends AbstractHttpResponse<File> implements HttpFileResponse<File> {

    private File file;

    public HttpFileDownloadLocalResponse(File file, ResponseConvertContext context) {
        super(context);
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
