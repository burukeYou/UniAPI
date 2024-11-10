package com.burukeyou.uniapi.http.core.response.entity;

import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import com.burukeyou.uniapi.http.support.Cookie;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author      caizhihao
 */
public class OkHttpResponse extends AbstractUniHttpResponse {

    private Request request;

    private Response response;


    public OkHttpResponse(ResponseConvertContext context) {
        this.request = context.getRequest();
        this.response = context.getResponse();
    }

    @Override
    public String getBodyToString() {
        // todo 执行多次？
        return response.body().toString();
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> map = new HashMap<>();
        Headers headers = response.headers();
        for (String name : response.headers().names()) {
            map.put(name,headers.get(name));
        }
        return map;
    }

    @Override
    public Map<String, List<String>> getHeaderMap() {
        return response.headers().toMultimap();
    }

    @Override
    public String getHeader(String name) {
        return response.header(name);
    }

    @Override
    public List<String> getHeaderList(String name) {
        return response.headers(name);
    }

    @Override
    public int getHttpCode() {
        return response.code();
    }

    @Override
    public List<String> getSetCookiesString() {
        return getHeaderList("Set-Cookie");
    }

    @Override
    public List<Cookie> getSetCookies() {
        return parseAll(request.url(), response.headers());
    }



    private static List<Cookie> parseAll(okhttp3.HttpUrl url, Headers headers) {
        List<Cookie> cookieList = new ArrayList<>();
        List<okhttp3.Cookie> cookies = okhttp3.Cookie.parseAll(url, headers);
        for (okhttp3.Cookie tmp : cookies) {
            Cookie cookie = new Cookie();
            BeanUtils.copyProperties(tmp,cookie);
            cookieList.add(cookie);
        }
        return cookieList;
    }

}
