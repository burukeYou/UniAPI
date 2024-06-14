package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.RequestMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caizhihao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpMetadata {

    private HttpUrl httpUrl = new HttpUrl();

    private RequestMethod requestMethod;

    private HttpBody body;

    private Map<String,String> headers = new HashMap<>();

    private List<Cookie> cookies = new ArrayList<>();

    public void setUrl(String url){
        this.httpUrl.setUrl(url);
    }

    public void setUrlPath(String path){
        this.httpUrl.setPath(path);
    }

    public void putUrlParam(String key,Object value){
        this.httpUrl.putUrlParam(key,value);
    }

    public void putPathParam(String key, Object value) {
        this.httpUrl.putPathParam(key,value);
    }

    public void putHeader(String key, String value) {
        this.headers.put(key,value);
    }

    public void putAllQueryParam(Map<String, Object> queryParam) {
        httpUrl.getQueryParam().putAll(queryParam);
    }

    public void putAllPathParam(Map<String, String> pathParam) {
        httpUrl.getPathParam().putAll(pathParam);
    }

    public void setBodyIfAbsent(HttpBody httpBody) {
        if (httpBody == null){
            return;
        }

        if (body == null){
            this.body = httpBody;
        }
    }

    public void putAllHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void addAllCookies(List<Cookie> cookiesList) {
        cookies.addAll(cookiesList);
    }

    public String getCookie(){
        return cookies.stream().map(e -> e.getName() + "=" + e.getValue()).collect(Collectors.joining(";"));
    }

    public String toHttpString(){
        // 打印Http报文
        //
        return null;
    }
}
