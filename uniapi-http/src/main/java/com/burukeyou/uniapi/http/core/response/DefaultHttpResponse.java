package com.burukeyou.uniapi.http.core.response;

import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caizhihao
 */
@Getter
@Setter
public class DefaultHttpResponse extends AbstractHttpResponse<Object> {

    /**
     *  origin http response body string
     */
    protected String originBodyPrintString;

    protected Object bodyResult;

    public DefaultHttpResponse(UniHttpResponse uniHttpResponse) {
       this(uniHttpResponse,null,null);
    }

    public DefaultHttpResponse(UniHttpResponse uniHttpResponse, String originBodyPrintString, Object bodyResult) {
        super(uniHttpResponse);
        this.originBodyPrintString = originBodyPrintString;
        this.bodyResult = bodyResult;
    }


    @Override
    public String bodyResultString() {
        return originBodyPrintString;
    }

    @Override
    public Object getBodyResult() {
        return bodyResult;
    }

    @Override
    public String getBodyToString() {
        return responseMetadata.getBodyToString();
    }

    @Override
    public byte[] getBodyToBytes() {
        return responseMetadata.getBodyToBytes();
    }

    @Override
    public InputStream getBodyToInputStream() {
        return responseMetadata.getBodyToInputStream();
    }
}
