package com.burukeyou.uniapi.http.core.http.response;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.support.Cookie;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpResponseMetadata implements Closeable {

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
    private Map<String, List<String>> headersMap = new HashMap<>();

    private String bodyString;

    private InputStream bodyInputStream;

    private byte[] bodyBytes;

    private boolean isReadString = false;
    private boolean isReadByte = false;
    private boolean isReadInputStream = false;


    @Override
    public void close() throws IOException {

    }

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received, understood, and accepted.
     * @return                  is response success
     */
    public boolean isSuccessful(){
        return code >= 200 && code < 300;
    }

    /**
     * get response body to string
     */
    public String getBodyString(){
        if (isReadString){
            return bodyString;
        }
        this.bodyString = uniHttpResponse.getBodyToString();
        this.isReadString = true;
        return bodyString;
    }

    /**
     * get response body to InputStream
     */
    public InputStream getBodyInputStream(){
        if (isReadInputStream){
            return bodyInputStream;
        }
        this.bodyInputStream = uniHttpResponse.getBodyToInputStream();
        this.isReadInputStream = true;
        return bodyInputStream;
    }

    /**
     * get response body to byte[]
     */
    public byte[] getBodyBytes(){
        if (isReadByte){
            return bodyBytes;
        }
        InputStream inputStream = getBodyInputStream();
        try {
            return FileCopyUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }finally {
            this.isReadByte = true;
        }
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
       // todo
        return null;
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

}
