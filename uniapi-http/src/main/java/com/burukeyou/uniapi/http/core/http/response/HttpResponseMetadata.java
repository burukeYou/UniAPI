package com.burukeyou.uniapi.http.core.http.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.utils.cookie.CookieUtil;
import okhttp3.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

/**
 *  Http Response data
 *
 * @author  caizhihao
 */

public class HttpResponseMetadata {

    /**
     *  Request Data
     */
    private HttpMetadata requestMetadata;

    /**
     *  Response info
     */
    private UniHttpResponse uniHttpResponse;

    /**
     * Response Code
     */
    private int code;

    /**
     *  Response Header
     */
    private final Map<String, List<String>> headersMap = new HashMap<>();

    private boolean isUpdateBodyString = false;
    private boolean isUpdateBodyByte = false;

    private String bodyString;
    private byte[] bodyBytes;

    private MediaType bodyMediaType;

    public HttpResponseMetadata(HttpMetadata httpMetadata,UniHttpResponse uniHttpResponse) {
        this.requestMetadata = httpMetadata;
        this.uniHttpResponse = uniHttpResponse;

        String contentType = uniHttpResponse.getContentType();
        if (StringUtils.isNotBlank(contentType)){
            this.bodyMediaType = MediaType.parse(contentType);
        }
    }

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received, understood, and accepted.
     * @return                  is response success
     */
    public boolean isSuccessful(){
        return uniHttpResponse.isSuccessful();
    }

    public void updateBody(String bodyString){
       this.bodyString = bodyString;
       this.isUpdateBodyString = true;
    }

    public void updateBody(byte[] bodyBytes){
      this.bodyBytes = bodyBytes;
      this.isUpdateBodyByte = true;
    }

    /**
     * get response body to string
     */
    public String getBodyToString(){
        if (isUpdateBodyString){
            return this.bodyString;
        }

        if (isUpdateBodyByte){
            // already read from server to bytes
            if (bodyMediaType != null){
                this.bodyString = new String(bodyBytes,bodyMediaType.charset(StandardCharsets.UTF_8));
            }else {
                this.bodyString = new String(bodyBytes,StandardCharsets.UTF_8);
            }
        }else {
            // read from server
            this.bodyString = uniHttpResponse.getBodyToString();
        }

        this.isUpdateBodyString = true;
        return bodyString;
    }

    /**
     * get response body to byte array
     */
    public byte[] getBodyToBytes(){
        if (isUpdateBodyByte){
            return bodyBytes;
        }

        if(isUpdateBodyString){
            // already read from server to string
            if (bodyMediaType != null){
                this.bodyBytes = bodyString.getBytes(bodyMediaType.charset(StandardCharsets.UTF_8));
            }else {
                this.bodyBytes = bodyString.getBytes(StandardCharsets.UTF_8);
            }
        }else {
            // read from server
            this.bodyBytes = uniHttpResponse.getBodyBytes();
        }

        this.isUpdateBodyByte = true;
        return bodyBytes;
    }

    /**
     * get response body to InputStream
     */
    public InputStream getBodyToInputStream(){
        return uniHttpResponse.getBodyToInputStream();
    }


    /**
     * Get all the request  header for the response
     */
    public Map<String, List<String>> getHeaderMap(){
        return headersMap;
    }

    /**
     * Get all the response header for the response
     *      If the same request header name exists,
     *      it will be overwritten and returned. In this case,
     *      please use {@link #getHeaderMap()}
     */
    public Map<String,String> getHeaders(){
        Map<String, List<String>> headerMap = getHeaderMap();
        if (headersMap == null || headerMap.isEmpty()){
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>(headerMap.size());
        headerMap.forEach((name,value) -> {
            if (value != null && !value.isEmpty()){
                map.put(name,value.get(0));
            }
        });
        return map;
    }


    /**
     * Get request header by name
     * @param name   header name
     */
    public String getHeader(String name){
        List<String> values = getHeaderList(name);
        if (values == null || values.isEmpty()){
            return "";
        }
        return values.get(0);
    }

    /**
     * Get request header by name , If there are multiple identical request headers, they will all be returned
     * @param name   header name
     */
    public List<String> getHeaderList(String name){
        List<String> values = headersMap.get(name);
        if (values == null || values.isEmpty()){
            return Collections.emptyList();
        }
        return values;
    }

    /**
     * get the http response status code
     */
    public int getHttpCode(){
        return code;
    }

    /**
     * get response body content-type
     */
    public String getContentType(){
        return getHeader("Content-Type");
    }

    /**
     *  set response body content-type
     */
    public void setContentType(String contentType){
        List<String> values = headersMap.get(contentType);
        if (CollectionUtils.isEmpty(values)){
            headersMap.put("Content-Type",Collections.singletonList(contentType));
        }else {
            values.add(contentType);
        }
    }

    /**
     * get response set-cookie header string
     */
    public List<String> getSetCookiesString(){
        return getHeaderList("Set-Cookie");
    }

    /**
     * get response set-cookie header to
     */
    public List<Cookie> getSetCookies(){
        List<String> setCookiesString = getSetCookiesString();
        return CookieUtil.parseAll(requestMetadata.getUrl(),setCookiesString);
    }

    /**
     *  Returns true if this response redirects to another resource.
     */
    public boolean isRedirect(){
        switch (code) {
            case 308:
            case 307:
            case 300:
            case 301:
            case 302:
            case 303:
                return true;
            default:
                return false;
        }
    }

    private <T> T convertBodyContentToType(Object bodyContent, Class<T> bodyTargetClass) {
        if (bodyContent == null){
            return null;
        }
        Class<?> actualClass = bodyContent.getClass();
        if (bodyTargetClass.equals(actualClass) || bodyTargetClass.isAssignableFrom(actualClass)){
            return (T)bodyContent;
        }

        // convert string to other type
        if (actualClass.equals(String.class)){
            // string ==> inputStream
            if (InputStream.class.isAssignableFrom(bodyTargetClass)){
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bodyContent.toString().getBytes());
                return bodyTargetClass.cast(inputStream);
            }

            // string ==> byte[]
            if (byte[].class.equals(bodyTargetClass)){
                byte[] bytes = bodyContent.toString().getBytes();
                return bodyTargetClass.cast(bytes);
            }
        }

        // convert byte[] to other type
        if (actualClass.equals(byte[].class)){
            // byte[] ==> inputStream
            if (InputStream.class.isAssignableFrom(bodyTargetClass)){
                ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])bodyContent);
                return bodyTargetClass.cast(inputStream);
            }

            // byte[] ==> string
            if (String.class.equals(bodyTargetClass)){
                String string = new String((byte[])bodyContent);
                return bodyTargetClass.cast(string);
            }
        }

        // convert inputStream to other type
        if (InputStream.class.isAssignableFrom(actualClass)){
            // inputStream ==> string
            if (String.class.equals(bodyTargetClass)){
                try {
                    String string = FileCopyUtils.copyToString(new InputStreamReader(castBodyInputStream(bodyContent)));
                    return bodyTargetClass.cast(string);
                } catch (IOException e) {
                    throw new UniHttpResponseException(e);
                }
            }

            // inputStream ==> byte[]
            if (byte[].class.equals(bodyTargetClass)){
                try {
                    byte[] bytes = FileCopyUtils.copyToByteArray(castBodyInputStream(bodyContent));
                    return bodyTargetClass.cast(bytes);
                } catch (IOException e) {
                    throw new UniHttpResponseException(e);
                }
            }
        }

        throw new IllegalStateException("Unsupported body type conversion from " + actualClass + " to " + bodyTargetClass);
    }

    private static InputStream castBodyInputStream(Object bodyContent) {
        return (InputStream)bodyContent;
    }

}
