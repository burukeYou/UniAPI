package com.burukeyou.uniapi.http.core.response;

import okhttp3.Cookie;

import java.util.List;
import java.util.Map;

/**
 * @author caizhihao
 */
public interface HttpResponse<T> {

    T getResult();

    Map<String,String> getHeaders();

    String getHeader(String name);

    List<String> getHeaderList(String name);

    int getHttpCode();

    String getContentType();

    List<String> getSetCookiesString();

    List<Cookie> getSetCookies();
}
