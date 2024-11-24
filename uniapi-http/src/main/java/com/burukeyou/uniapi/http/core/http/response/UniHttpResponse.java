package com.burukeyou.uniapi.http.core.http.response;

import com.burukeyou.uniapi.http.support.Cookie;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *  Http Response
 */
public interface UniHttpResponse {

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
     *      If the same request header name exists,
     *      it will be overwritten and returned. In this case,
     *      please use {@link UniHttpResponse#getHeaderMap()}
     */
    Map<String,String> getHeaders();

    /**
     * Get all the request  header for the response
     */
    Map<String, List<String>> getHeaderMap();

    /**
     * Get request header by name
     * @param name   header name
     */
    String getHeader(String name);

    /**
     * Get request header by name , If there are multiple identical request headers, they will all be returned
     * @param name   header name
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


}
