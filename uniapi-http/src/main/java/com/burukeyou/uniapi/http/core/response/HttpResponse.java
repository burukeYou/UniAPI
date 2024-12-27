package com.burukeyou.uniapi.http.core.response;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.support.Cookie;

/**
 * Http Original Response Object
 *
 *
 * @author caizhihao
 */
public interface HttpResponse<T> {

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received, understood, and accepted.
     * @return                  is response success
     */
    boolean isSuccessful();

    /**
     * Obtain the deserialized object of the HTTP response body,this type is the return value type of the proxy method
     * @return           HTTP response body Object
     */
    T getBodyResult();

    /**
     * Gets the raw response body string of the interface
     */
    String getBodyToString();

    /**
     * Gets the raw response body bytes of the interface
     */
    byte[] getBodyToBytes();

    /**
     * Gets the raw response body as an input stream
     */
    InputStream getBodyToInputStream();

    /**
     * Get all the request  header for the response
     *      If the same request header name exists,
     *      it will be overwritten and returned. In this case,
     *      please use {@link #getHeaderMap()}
     */
    Map<String,String> getHeaders();

    /**
     * Get all the request  header for the response
     */
    Map<String, List<String>> getHeaderMap();

    /**
     * Get the  custom request header for the response
     */
    String getHeader(String name);

    /**
     * Get the  custom request header for the response
     */
    List<String> getHeaderList(String name);

    /**
     * get the http response status code
     */
    int getHttpCode();

    /**
     * get response body content-type
     */
    String getContentType();

    /**
     * get response set-cookie header string
     */
    List<String> getSetCookiesString();

    /**
     * get response set-cookie header to
     */
    List<Cookie> getSetCookies();

    /**
     *  Returns true if this response redirects to another resource.
     */
    boolean isRedirect();

    /**
     * http protocol string
     */
    String toHttpProtocol();

    /**
     * Get Content-Disposition header fileName
     */
    String getContentDispositionFileName();
}
