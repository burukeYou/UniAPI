package com.burukeyou.uniapi.http.core.response;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  caizhihao
 */

@Getter
@Setter
public abstract class AbstractHttpResponse implements HttpResponse {

    private Request request;

    protected Response response;

    protected Method method;


    protected boolean ifReturnOriginalResponse() {
        return HttpResponse.class.isAssignableFrom(method.getReturnType());
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
    public String getHeader(String name) {
        return response.header(name);
    }

    @Override
    public List<String> getHeaderList(String name) {
        return  response.headers(name);
    }

    @Override
    public int getHttpCode() {
        return response.code();
    }

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    public List<String> getSetCookiesString() {
        return getHeaderList("Set-Cookie");
    }

    @Override
    public List<Cookie> getSetCookies() {
        return Cookie.parseAll(request.url(), response.headers());
    }


    public static void main(String[] args) {
        String cookieHeader = "xxx_sso_sessionid=fsadf; Domain=\"\"; Path=/; HttpOnly; name=jay";
        List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
        // 创建一个Map来存储解析后的Cookie
        Map<String, String> cookieMap = new HashMap<>();

        // 遍历Cookie列表并将它们添加到Map中
        for (HttpCookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
        System.out.println();
    }
}
