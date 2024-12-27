package com.burukeyou.uniapi.http.core.response;

import java.io.File;

/**
 *
 * @param <T>  only supports types such as byte[]、File、InputStream
 */
public interface HttpFileResponse<T> extends HttpResponse<T> {

    /**
     * get download file name from response header Content-Disposition
     */
    String getFileName();

    /**
     * save file to local
     * @param savePath          file save path
     */
    File saveFile(String savePath);


}
