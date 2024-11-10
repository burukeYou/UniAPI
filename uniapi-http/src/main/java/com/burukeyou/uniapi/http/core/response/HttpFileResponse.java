package com.burukeyou.uniapi.http.core.response;

/**
 *
 * @param <T>  only supports types such as byte[]、File、InputStream
 */
public interface HttpFileResponse<T> extends HttpResponse<T> {

    /**
     * get download file name from response header Content-Disposition
     */
    String getFileName();

}
