package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author caizhihao
 */
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
