package com.burukeyou.uniapi.core.request;

import com.burukeyou.uniapi.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
public class HttpBodyBinary extends HttpBody {

    private InputStream file;

    public HttpBodyBinary(InputStream file) {
        super(MediaTypeEnum.APPLICATION_OCTET_STREAM.getType());
        this.file = file;
    }

    @Override
    public boolean emptyContent() {
        return file == null;
    }

    @Override
    public String toString() {
        return "";
    }
}
