package com.burukeyou.uniapi.http.core.request;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.http.utils.BizUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.portable.InputStream;
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
public class HttpMetadata implements UniHttpRequest {

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
     * set http url root address
     */
    @Override
    public void setUrl(String url){
        this.httpUrl.setUrl(url);
    }

    @Override
    public String getUrl(){
        return this.httpUrl.getUrl();
    }

    /**
     * set http url path
     */
    @Override
    public void setUrlPath(String path){
        this.httpUrl.setPath(path);
    }

    @Override
    public String getUrlPath(String path){
        return this.httpUrl.getPath();
    }

    @Override
    public String getRequestUrl() {
        return this.httpUrl.toUrl();
    }

    /**
     *  add url queryParam
     */
    @Override
    public void putQueryParam(String key,Object value){
        this.httpUrl.putQueryParam(key,value);
    }

    @Override
    public void putQueryParams(Map<String, Object> queryParam) {
        this.httpUrl.getQueryParam().putAll(queryParam);
    }


    @Override
    public void putPathParam(String key, Object value) {
        this.httpUrl.putPathParam(key,value);
    }

    @Override
    public void putHeader(String key, String value) {
        this.headers.put(key,value);
    }

    @Override
    public void putHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    @Override
    public String getHeader(String key){
        return this.headers.get(key);
    }

    @Override
    public void setContentType(String contentType){
        this.headers.put("Content-Type",contentType);
    }

    @Override
    public String getContentType(){
        return this.headers.get("Content-Type");
    }

    public void putPathParams(Map<String, String> pathParam) {
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

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void addCookiesList(List<Cookie> cookiesList) {
        cookies.addAll(cookiesList);
    }

    @Override
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

    @Override
    public Cookie getCookie(String name){
        return this.cookies.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public List<Cookie> getCookies(String name){
        return this.cookies.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList());
    }

    /**
     * Get the complete cookie string
     */
    public String getCookiesToString(){
        return cookies.stream().map(e -> e.getName() + "=" + e.getValue()).collect(Collectors.joining(";"));
    }

    @Override
    public void setBodyJson(String json) {
        this.body = new HttpBodyJSON(json);
    }

    @Override
    public void setBodyFromData(Map<String, String> map) {
        this.body = new HttpBodyFormData(map);
    }

    @Override
    public void setBodyText(String text) {
        this.body = new HttpBodyText(text);
    }

    @Override
    public void setBodyBinary(byte[] binary) {
        this.body = new HttpBodyBinary(new ByteArrayInputStream(binary));
    }

    @Override
    public void setBodyBinary(InputStream stream) {
        this.body = new HttpBodyBinary(stream);
    }

    @Override
    public void addBodyMultipartText(String name, String value) {
        if (body == null || !body.getClass().equals(HttpBodyMultipart.class)){
            this.body = new HttpBodyMultipart();
        }
        ((HttpBodyMultipart)body).addTextItem(name,value);
    }

    @Override
    public void addBodyMultipartFile(String name, Object file) {
        if (file != null && !BizUtil.isFileForClass(file.getClass())){
            throw new IllegalArgumentException("file must be a File,byte[] or InputStream");
        }
        if (body == null || !body.getClass().equals(HttpBodyMultipart.class)){
            this.body = new HttpBodyMultipart();
        }
        ((HttpBodyMultipart)body).addFileItem(name,file);
    }

    @Override
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
        sb.append("\n------------------------------------------------");
        sb.append("\n").append(requestMethod == null ? "" : requestMethod.name())
                .append("\t\t").append(httpUrl.toUrl()).append("\n");

        sb.append("Request Header:\n");
  /*      if (body != null){
            sb.append("\t\tContent-Type:\t\t").append(body.getContentType()).append("\n");
        }*/
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("\t\t").append(entry.getKey()).append(":\t").append(entry.getValue()).append("\n");
        }

        if(!CollectionUtils.isEmpty(cookies)){
            sb.append("\t\t").append("Cookie:\t").append(getCookiesToString()).append("\n");
        }

        sb.append("Request Body:\n");
        if (body != null){
            if (body instanceof HttpBodyMultipart){
                sb.append(body.toStringBody()).append("\n");
            }else {
                sb.append("\t\t").append(body.toStringBody()).append("\n");
            }
        }
        //sb.append("------------------------------------------------\n");
        return sb.toString();
    }
}
