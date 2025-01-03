package com.burukeyou.uniapi.http.core.response;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.burukeyou.uniapi.http.core.httpclient.response.HttpResponseInfo;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.http.utils.cookie.CookieUtil;
import okhttp3.MediaType;
import org.apache.commons.lang3.StringUtils;

/**
 *  Http Response data
 *
 * @author  caizhihao
 */

public class UniHttpResponse implements Closeable {

    private static final List<String> NOT_PRINT_HEADER = Arrays.asList("connection","keep-alive","date","transfer-encoding");

    private static final Pattern pattern = Pattern.compile("filename\\s*=\\s*\\\"(.*)\\\"");

    /**
     *  Request Data
     */
    private final UniHttpRequest request;

    /**
     *  Response info
     */
    private final HttpResponseInfo originHttpResponse;

    /**
     * Response Code
     */
    private final int code;

    /**
     *  Response Header
     */
    private  Map<String, List<String>> headersMap = new HashMap<>();

    private boolean isUpdateBodyString = false;
    private boolean isUpdateBodyByte = false;

    private String bodyString;
    private byte[] bodyBytes;

    private MediaType bodyMediaType;

    private final String contentType;

    public UniHttpResponse(UniHttpRequest uniHttpRequest, HttpResponseInfo originHttpResponse) {
        this.request = uniHttpRequest;
        this.originHttpResponse = originHttpResponse;
        this.code = originHttpResponse.getHttpCode();
        Map<String, List<String>> headerMap = originHttpResponse.getHeaderMap();
        this.headersMap = headerMap == null ? new HashMap<>() : headerMap;
        this.contentType = getHeader("Content-Type");;

        if (StringUtils.isNotBlank(contentType)){
            this.bodyMediaType = MediaType.parse(contentType);
        }
    }

    @Override
    public void close() throws IOException {
        originHttpResponse.closeResource();
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
    public String getBodyToString(){
        if (isUpdateBodyString){
            return this.bodyString;
        }

        if (isUpdateBodyByte){
            // already read from server to bytes
            this.bodyString = new String(bodyBytes, getBodyCharset());
        }else {
            // read from server
            this.bodyString = originHttpResponse.getBodyToString();
        }

        this.isUpdateBodyString = true;
        return bodyString;
    }

    /**
     *  get response body content charset
     */
    public Charset getBodyCharset() {
        if (bodyMediaType == null){
            return StandardCharsets.UTF_8;
        }
        return bodyMediaType.charset(StandardCharsets.UTF_8);
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
            this.bodyBytes = bodyString.getBytes(getBodyCharset());
        }else {
            // read from server
            this.bodyBytes = originHttpResponse.getBodyBytes();
        }

        this.isUpdateBodyByte = true;
        return bodyBytes;
    }

    /**
     * get response body to InputStream
     */
    public InputStream getBodyToInputStream(){
        if (isUpdateBodyByte){
            // already read from server to byte array
            return new ByteArrayInputStream(bodyBytes);
        }
        if (isUpdateBodyString){
            // already read from server to string
            return new ByteArrayInputStream(bodyString.getBytes(getBodyCharset()));
        }
        return originHttpResponse.getBodyToInputStream();
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
        return contentType;
    }

    /**
     * get response body content-length
     */
    public String getContentLength(){
        return getHeader("Content-Length");
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
        return CookieUtil.parseAll(setCookiesString);
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

    /**
     * update response body content
     */
    public void updateBody(String bodyString){
        this.bodyString = bodyString;
        this.isUpdateBodyString = true;
    }

    /**
     * update response body content
     */
    public void updateBody(byte[] bodyBytes){
        this.bodyBytes = bodyBytes;
        this.isUpdateBodyByte = true;
    }

    /**
     * get request data
     */
    public UniHttpRequest getRequest() {
        return request;
    }


    /**
     * is file download type  response
     */
    public boolean isFileResponse(){
        String contentType = getContentType();
        if (MediaTypeEnum.isFileType(contentType)){
            return true;
        }

        if (MediaTypeEnum.isTextType(contentType)){
            return false;
        }

        // Content-Disposition: attachment; filename=xxx.txt
        String disposition = getHeader("Content-Disposition");
        if(StringUtils.isNotBlank(disposition) && (disposition.contains("attachment") || disposition.contains("inline"))){
            return true;
        }

        return false;
    }

    /**
     * is text type response
     */
    public boolean isTextResponse(){
        return !isFileResponse();
    }

    /**
     *  get header Content-Disposition fileName
     */
    public String getContentDispositionFileName(){
        String header = getHeader("Content-Disposition");
        if(StringUtils.isBlank(header)){
            return null;
        }
        String fileName = null;
        Matcher matcher = pattern.matcher(header);
        if (matcher.find()){
            fileName = matcher.group(1);
        }
        return fileName;
    }

    /**
     * convert to http response protocol string
     */
    public String toHttpProtocol() {
        StringBuilder sb = new StringBuilder();
        RequestMethod requestMethod = request.getRequestMethod();
        HttpUrl httpUrl = request.getHttpUrl();
        sb.append("\n").append(requestMethod == null ? "" : requestMethod.name())
                .append("\t\t").append(httpUrl.toUrl()).append("\n");
        sb.append(buildBodyProtocol());
        return sb.toString();
    }

    private StringBuilder buildBodyProtocol() {
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> stringListMap = getHeaderMap();
        if (stringListMap != null && !stringListMap.isEmpty()){
            sb.append("Response Header:\n");
            stringListMap.forEach((key, value) -> {
                if (!NOT_PRINT_HEADER.contains(key.toLowerCase())){
                    for (String s : value) {
                        sb.append("\t\t").append(key).append(":\t").append(s).append("\n");
                    }
                }
            });
        }

        if (isFileResponse()){
            String responseFileName = getContentDispositionFileName();
            String contentLength = getContentLength();
            if (StringUtils.isNotBlank(contentLength)){
                sb.append("Response Body:\n");
                responseFileName = StringUtils.isBlank(responseFileName) ? "" : "fileName: " + responseFileName;
                sb.append("\t\t").append("【file】 " + "size: " + contentLength  + "  " + responseFileName).append("\n");
            }
        }else {
            String bodyToString = getBodyToString();
            if (StringUtils.isNotBlank(bodyToString)){
                sb.append("Response Body:\n");
                sb.append("\t\t").append(bodyToString).append("\n");
            }
        }
        return sb;
    }

    /**
     * convert to http protocol string, include request
     */
    public String toHttpProtocolIntact(){
        String requestProtocol = request.toHttpProtocol();
        return requestProtocol + buildBodyProtocol();
    }
}
