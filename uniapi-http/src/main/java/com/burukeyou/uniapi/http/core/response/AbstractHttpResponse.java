package com.burukeyou.uniapi.http.core.response;

import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.support.RequestMethod;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    private transient Request request;

    protected transient Response response;

    protected transient Method method;

    protected T bodyResult;

    protected Type bodyResultType;

    public T getBodyResult() {
        return bodyResult;
    }

    public Type getBodyResultType() {
        if (bodyResultType == null){
            if (HttpResponse.class.isAssignableFrom(method.getReturnType())){
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType){
                    Type actualTypeArgument = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                    bodyResultType = actualTypeArgument;
                }
            }else {
                bodyResultType = method.getGenericReturnType();
            }
        }

        return bodyResultType;
    }

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

    public abstract String bodyResultString();

    @Override
    public String toHttpProtocol() {
        RequestMethod requestMethod = httpMetadata.getRequestMethod();
        HttpUrl httpUrl = httpMetadata.getHttpUrl();
        HttpBody body = httpMetadata.getBody();
        Map<String, String> headers = httpMetadata.getHeaders();

        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------");
        sb.append("\n").append(requestMethod == null ? "" : requestMethod.name())
                .append("\t\t").append(httpUrl.toUrl()).append("\n");

        sb.append("Request Header:\n");
        if (body != null){
            sb.append("\t\tContent-Type:\t\t").append(body.getContentType()).append("\n");
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("\t\t").append(entry.getKey()).append(":\t").append(entry.getValue()).append("\n");
        }

        if(!CollectionUtils.isEmpty(httpMetadata.getCookies())){
            sb.append("\t\t").append("Cookie:\t").append(httpMetadata.getCookieString()).append("\n");
        }

        sb.append("Request Body:\n");
        if (body != null){
            sb.append("\t\t").append(body.toStringBody()).append("\n");
        }

        sb.append("Response Header:\n");
        Map<String, List<String>> stringListMap = response.headers().toMultimap();
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
