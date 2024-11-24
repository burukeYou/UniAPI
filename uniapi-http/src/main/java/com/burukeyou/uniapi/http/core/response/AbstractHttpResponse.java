package com.burukeyou.uniapi.http.core.response;

import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.support.Cookie;
import lombok.Getter;
import lombok.Setter;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  caizhihao
 */

@Setter
@Getter
public abstract class AbstractHttpResponse<T> implements HttpResponse<T> {

    protected transient HttpMetadata httpMetadata;


    protected transient UniHttpResponse response;

    public AbstractHttpResponse(ResponseConvertContext context, T bodyResult) {
        this.httpMetadata = context.getHttpMetadata();
        this.response = context.getResponse();
    }

    protected T bodyResult;

    public void setBodyResult(T bodyResult) {
        this.bodyResult = bodyResult;
    }

    @Override
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    @Override
    public Map<String, String> getHeaders() {
        return response.getHeaders();
    }

    @Override
    public Map<String, List<String>> getHeaderMap() {
        return response.getHeaderMap();
    }

    @Override
    public String getHeader(String name) {
        return response.getHeader(name);
    }

    @Override
    public List<String> getHeaderList(String name) {
        return  response.getHeaderList(name);
    }

    @Override
    public int getHttpCode() {
        return response.getHttpCode();
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    public List<String> getSetCookiesString() {
        return getHeaderList("Set-Cookie");
    }

    @Override
    public List<Cookie> getSetCookies() {
        return response.getSetCookies();
    }

    @Override
    public boolean isRedirect() {
        return response.isRedirect();
    }

    public abstract String bodyResultString();

    @Override
    public String toHttpProtocol() {
        String requestProtocol = httpMetadata.toHttpProtocol();
        StringBuilder sb = new StringBuilder(requestProtocol);
        sb.append("Response Header:\n");
        Map<String, List<String>> stringListMap = response.getHeaderMap();
        stringListMap.forEach((key, value) -> {
            for (String s : value) {
                sb.append("\t\t").append(key).append(":\t").append(s).append("\n");
            }
        });
        sb.append("Response Body:\n");
        T result = getBodyResult();
        if (result != null){
            sb.append("\t\t").append(bodyResultString()).append("\n");
        }
        sb.append("------------------------------------------------\n");
        return sb.toString();
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
