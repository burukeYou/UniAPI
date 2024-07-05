package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;

/**
 * @author caizhihao
 */
public abstract class HttpBody {

    /**
     *  HTTP request body content type
     * {@link MediaTypeEnum}
     */
    protected String contentType;

    /**
     * new a http body
     */
    protected HttpBody(String contentType) {
        this.contentType = contentType;
    }

    /**
     * is empty request body
     */
    public abstract boolean emptyContent();


    /**
     * get http body ContentType
     */
    public String getContentType() {
        return contentType;
    }

    public abstract String toString();


}
