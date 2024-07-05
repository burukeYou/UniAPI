package com.burukeyou.uniapi.http.core.response;

import okhttp3.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Http Response Object
 *
 *
 * @author caizhihao
 */
public interface HttpResponse<T> {

    /**
     * Obtain the deserialized object of the HTTP response body,this type is the return value type of the proxy method
     * @return           HTTP response body Object
     */
    T getBodyResult();

    /**
     * Get all the request  header for the response
     */
    Map<String,String> getHeaders();

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
     * get response set-cookie header string
     */
    List<Cookie> getSetCookies();

    /**
     * HTTP response message
     */
    String toResponseMessage();
}
