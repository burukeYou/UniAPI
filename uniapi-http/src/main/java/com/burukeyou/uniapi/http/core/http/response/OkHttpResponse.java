package com.burukeyou.uniapi.http.core.http.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.support.Cookie;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;

/**
 * @author      caizhihao
 */

@Getter
@Setter
public class OkHttpResponse extends AbstractUniHttpResponse {

    private Request request;

    private Response response;


    public OkHttpResponse(Request request,Response response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    @Override
    public String getBodyToString() {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }

    @Override
    public byte[] getBodyBytes() {
        if (response.body() == null){
            return null;
        }
        try {
            return response.body().bytes();
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }

    @Override
    public InputStream getBodyToInputStream() {
        if (response.body() == null){
            return null;
        }
        return response.body().byteStream();
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

    @Override
    public boolean isRedirect() {
        return response.isRedirect();
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
