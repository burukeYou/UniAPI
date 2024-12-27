package com.burukeyou.uniapi.http.core.http.response;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *  Http Response Data Info
 */
public interface HttpResponseInfo {

    /**
     * get response body to string
     */
    String getBodyToString();

    /**
     * get response body to byte[]
     */
    byte[] getBodyBytes();

    /**
     * get response body to InputStream
     */
    InputStream getBodyToInputStream();

    /**
     * Get all the request  header for the response
     */
    Map<String, List<String>> getHeaderMap();

    /**
     * get the http response status code
     */
    int getHttpCode();

    /**
     *  close the response resource
     */
    void closeResource();
}
