package com.burukeyou.uniapi.http.core.response.entity;

import com.burukeyou.uniapi.http.support.Cookie;

import java.util.List;
import java.util.Map;

/**
 *  Http Response
 */
public interface UniHttpResponse {

    /**
     * get reponse body to string
     */
    String getBodyToString();

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
     * http protocol string
     */
    String toHttpProtocol();
}
