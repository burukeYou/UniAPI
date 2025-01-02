package com.burukeyou.uniapi.http.core.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.support.map.IMap;
import com.burukeyou.uniapi.support.map.ValueObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * Request metadata
 *
 * @author caizhihao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniHttpRequest implements Serializable {

    private static final long serialVersionUID = 3712288492261896042L;

    /**
     *  Request url Metadata
     */
    private HttpUrl httpUrl = new HttpUrl();

    /**
     *  Request Method
     */
    private RequestMethod requestMethod;

    /**
     *  Request Body
     */
    private HttpBody body;

    /**
     *  Request Headers
     */
    private Map<String,String> headers = new LinkedHashMap<>();

    /**
     *  Request Cookies
     */
    private List<Cookie> cookies = new ArrayList<>();

    /**
     *  Request Send Time (Milliseconds)
     */
    private long requestTime;

    /**
     *  Request Ext Properties，do not process, only do transparent transmission
     */
    private  IMap<String,Object> attachments = new ValueObjectHashMap<>();

    /**
     * set http url root address
     */
    
    public void setUrl(String url){
        this.httpUrl.setUrl(url);
    }


    /**
     * get request url
     */
    public String getUrl(){
        return this.httpUrl.getUrl();
    }

    /**
     * set http url path
     */
    
    public void setUrlPath(String path){
        this.httpUrl.setPath(path);
    }

    /**
     *  get request url path
     */
    public String getUrlPath(){
        return this.httpUrl.getPath();
    }

    /**
     * obtain the final request URL path, it's about splicing {@link #getUrl()} onto {@link #getUrl()}
     */
    public String getRequestUrl() {
        return this.httpUrl.toUrl();
    }

    /**
     * add request url query param
     * @param key               query param key
     * @param value             query param value
     */
    public void putQueryParam(String key,Object value){
        this.httpUrl.putQueryParam(key,value);
    }

    /**
     * add batch request url query param
     */
    public void putQueryParams(Map<String, Object> queryParam) {
        this.httpUrl.getQueryParam().putAll(queryParam);
    }


    /**
     *  add request url path query param
     * @param key               Path parameter placeholder
     * @param value             path parameter value
     */
    public void putPathParam(String key, Object value) {
        this.httpUrl.putPathParam(key,value);
    }

    /**
     * add request header
     * @param key               header key
     * @param value             header value
     */
    public void putHeader(String key, String value) {
        this.headers.put(key,value);
    }

    /**
     * add batch request header
     */
    public void putHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * add request header
     * @param key           header key
     */
    public String getHeader(String key){
        return this.headers.get(key);
    }

    /**
     * set request header for content-type
     */
    public void setContentType(String contentType){
        this.headers.put("Content-Type",contentType);
    }


    /**
     * get request header for  content-type
     */
    public String getContentType(){
        return this.headers.get("Content-Type");
    }

    /**
     * if path use placeholder such {xxx} ，can set placeholder valur
     */
    public void putPathParams(Map<String, String> pathParam) {
        httpUrl.getPathParam().putAll(pathParam);
    }

    /**
     * Set custom Http Request Body
     */
    public void setBodyIfAbsent(HttpBody httpBody) {
        if (httpBody == null){
            return;
        }
        if (body == null){
            this.body = httpBody;
        }
    }

    /**
     *  add request cookie
     */
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * add request cookie list
     */
    public void addCookiesList(List<Cookie> cookiesList) {
        cookies.addAll(cookiesList);
    }

    /**
     * add cookie string
     */
    public void addCookieString(String cookieString){
        if (StringUtils.isBlank(cookieString)){
            return;
        }
        try {
            for (String item : cookieString.split(";")) {
                String[] split = item.split("=");
                cookies.add(new Cookie(split[0].trim(),split[1].trim()));
            }
        } catch (Exception e) {
            throw new BaseUniHttpException("please use the correct cookie string format such as a=1;b=2",e);
        }
    }

    /**
     * get cookie by cookie name, only find one
     * @param name   cookie name
     */
    public Cookie getCookie(String name){
        return this.cookies.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * get all cookie by cookie name
     * @param name   cookie name
     */
    public List<Cookie> getCookies(String name){
        return this.cookies.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList());
    }

    /**
     * get all cookie convert to string
     */
    public String getCookiesToString(){
        return cookies.stream().map(e -> e.getName() + "=" + e.getValue()).collect(Collectors.joining(";"));
    }

    /**
     * set request body for application/json  formatted
     */
    public void setBodyJson(String json) {
        this.body = new HttpBodyJSON(json);
    }

    /**
     * set request body for application/x-www-form-urlencoded
     */
    public void setBodyFromData(Map<String, String> map) {
        this.body = new HttpBodyFormData(map);
    }

    /**
     * set request body text/*
     */
    public void setBodyText(String text) {
        this.body = new HttpBodyText(text);
    }

    /**
     * set request body for  application/octet-stream
     */
    public void setBodyBinary(byte[] binary) {
        this.body = new HttpBodyBinary(new ByteArrayInputStream(binary));
    }

    /**
     * set request body for  application/octet-stream
     */
    public void setBodyBinary(InputStream stream) {
        this.body = new HttpBodyBinary(stream);
    }

    /**
     * set request body for multipart/form-data , add Text key value pairs
     */
    public void addBodyMultipartText(String name, String value) {
        if (body == null || !body.getClass().equals(HttpBodyMultipart.class)){
            this.body = new HttpBodyMultipart();
        }
        ((HttpBodyMultipart)body).addTextItem(name,value);
    }

    /**
     * set request body for multipart/form-data , add File key value pairs only support InputStream、File、byte[]
     * @param name          key name
     * @param file          File or InputStream or byte[]
     */
    public void addBodyMultipartFile(String name, Object file) {
        if (file != null && !BizUtil.isFileForClass(file.getClass())){
            throw new IllegalArgumentException("file must be a File,byte[] or InputStream");
        }
        if (body == null || !body.getClass().equals(HttpBodyMultipart.class)){
            this.body = new HttpBodyMultipart();
        }
        ((HttpBodyMultipart)body).addFileItem(name,file);
    }

    /**
     * set request body for multipart/form-data , add File key value pairs only support InputStream、File、byte[]
     * @param name          key name
     * @param file          File or InputStream or byte[]
     * @param fileName      file name
     */
    public void addBodyMultipartFile(String name, Object file, String fileName) {
        if (file != null && !BizUtil.isFileForClass(file.getClass())){
            throw new IllegalArgumentException("file must be a File,byte[] or InputStream");
        }
        if (body == null || !body.getClass().equals(HttpBodyMultipart.class)){
            this.body = new HttpBodyMultipart();
        }
        ((HttpBodyMultipart)body).addFileItem(name,file,fileName);
    }

    /**
     *  http protocol string
     */
    public String toHttpProtocol() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(requestMethod == null ? "" : requestMethod.name())
                .append("\t\t").append(httpUrl.toUrl()).append("\n");

        if (headers != null && !headers.entrySet().isEmpty()){
            sb.append("Request Header:\n");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                sb.append("\t\t").append(entry.getKey()).append(":\t").append(entry.getValue()).append("\n");
            }
        }

        if(!CollectionUtils.isEmpty(cookies)){
            sb.append("\t\t").append("Cookie:\t").append(getCookiesToString()).append("\n");
        }
        if (body != null){
            sb.append("Request Body:\n");
            if (body instanceof HttpBodyMultipart){
                sb.append(body.toStringBody()).append("\n");
            }else {
                sb.append("\t\t").append(body.toStringBody()).append("\n");
            }
        }
        return sb.toString();
    }
}
