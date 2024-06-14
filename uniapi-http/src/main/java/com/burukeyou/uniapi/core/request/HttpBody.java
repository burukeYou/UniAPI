package com.burukeyou.uniapi.core.request;

/**
 * @author caizhihao
 */
public abstract class HttpBody {

    protected String contentType;

    protected HttpBody(String contentType) {
        this.contentType = contentType;
    }

    public abstract boolean emptyContent();


    public abstract String toString();

    public String getContentType() {
        return contentType;
    }

}
