package com.burukeyou.uniapi.http.core.request;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.RequestMethod;
import org.omg.CORBA.portable.InputStream;

/**
 *
 * @author  caizhihao
 */
public interface UniHttpRequest extends Serializable {

    /**
     * set request method such post、get、put、delete
     */
    void setRequestMethod(RequestMethod requestMethod);

    /**
     * get request method
     */
    RequestMethod getRequestMethod();

    /**
     * set request url
     */
    void setUrl(String url);

    /**
     * get request url
     */
    String getUrl();

    /**
     *  set request url path
     */
    void setUrlPath(String path);

    /**
     *  get request url path
     */
    String getUrlPath(String path);

    /**
     * obtain the final request URL path, it's about splicing {@link #getUrl()} onto {@link #getUrl()}
     */
    String getRequestUrl();

    /**
     * add request url query param
     * @param key               query param key
     * @param value             query param value
     */
    void putQueryParam(String key,Object value);

    void putQueryParams(Map<String, Object> queryParam);

    /**
     *  add request url path query param
     * @param key               Path parameter placeholder
     * @param value             path parameter value
     */
    void putPathParam(String key, Object value);

    /**
     * add request header
     * @param key               header key
     * @param value             header value
     */
    void putHeader(String key, String value);

    /**
     * add request header by map
     */
    void putHeaders(Map<String, String> headers);

    /**
     * add request header
     * @param key           header key
     */
    String getHeader(String key);

    /**
     * get all request header
     */
    Map<String,String> getHeaders();

    /**
     * set request header for content-type
     */
    void setContentType(String contentType);

    /**
     * get request header for  content-type
     */
    String getContentType();

    /**
     *  add request cookie
     */
    void addCookie(Cookie cookie);

    /**
     * add request cookie list
     */
    void addCookiesList(List<Cookie> cookiesList);

    /**
     * get cookie string
     */
    void addCookieString(String cookieString);

    /**
     * get cookie by cookie name, only find one
     * @param name   cookie name
     */
    Cookie getCookie(String name);

    /**
     * get all cookie by cookie name
     * @param name   cookie name
     */
    List<Cookie> getCookies(String name);

    /**
     * get all request cookie
     */
    List<Cookie> getCookies();

    /**
     * get all cookie convert to string
     */
    String getCookiesToString();

    /**
     * set request body for application/json  formatted
     */
    void setBodyJson(String json);

    /**
     * set request body for application/x-www-form-urlencoded
     */
    void setBodyFromData(Map<String,String> map);

    /**
     * set request body text/plain
     */
    void setBodyText(String text);

    /**
     * set request body for  application/octet-stream
     */
    void setBodyBinary(byte[] binary);

    /**
     * set request body for  application/octet-stream
     */
    void setBodyBinary(InputStream stream);

    /**
     * set request body for multipart/form-data , add Text key value pairs
     */
    void addBodyMultipartText(String name,String value);

    /**
     * set request body for multipart/form-data , add File key value pairs
     */
    void addBodyMultipartFile(String name, InputStream fileStream);

    /**
     * set request body for multipart/form-data , add File key value pairs
     */
    void addBodyMultipartFile(String name, byte[] fileStream);


    /**
     * set request body for multipart/form-data , add File key value pairs
     */
    void addBodyMultipartFile(String name, File fileStream);
}
