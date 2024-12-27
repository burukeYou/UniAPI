package com.burukeyou.uniapi.http.core.http.response;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.http.support.RequestMethod;
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

public class UniHttpResponse implements Closeable {

    private static final List<String> NOT_PRINT_HEADER = Arrays.asList("connection","keep-alive","date","transfer-encoding");

    private static final Pattern pattern = Pattern.compile("filename\\s*=\\s*\\\"(.*)\\\"");

    /**
     *  Request Data
     */
    private HttpMetadata request;

    /**
     *  Response info
     */
    private HttpResponseInfo httpResponseInfo;

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

    public UniHttpResponse(HttpMetadata httpMetadata, HttpResponseInfo httpResponseInfo) {
        this.request = httpMetadata;
        this.httpResponseInfo = httpResponseInfo;
        this.code = httpResponseInfo.getHttpCode();
        Map<String, List<String>> headerMap = httpResponseInfo.getHeaderMap();
        this.headersMap = headerMap == null ? new HashMap<>() : headerMap;
        this.contentType = getContentType();

        if (StringUtils.isNotBlank(contentType)){
            this.bodyMediaType = MediaType.parse(contentType);
        }
    }

    @Override
    public void close() throws IOException {
        httpResponseInfo.closeResource();
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
            this.bodyString = httpResponseInfo.getBodyToString();
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
            this.bodyBytes = httpResponseInfo.getBodyBytes();
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
        return httpResponseInfo.getBodyToInputStream();
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

    public String getContentLength(){
        return getHeader("Content-Length");
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

    public void updateBody(String bodyString){
        this.bodyString = bodyString;
        this.isUpdateBodyString = true;
    }

    public void updateBody(byte[] bodyBytes){
        this.bodyBytes = bodyBytes;
        this.isUpdateBodyByte = true;
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

    public HttpMetadata getRequest() {
        return request;
    }

    public HttpResponseInfo getUniHttpResponse() {
        return httpResponseInfo;
    }


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

    public String toHttpProtocolIntact(){
        String requestProtocol = request.toHttpProtocol();
        return requestProtocol + toHttpProtocol();
    }

    public String toHttpProtocol() {
        StringBuilder sb = new StringBuilder();
        RequestMethod requestMethod = request.getRequestMethod();
        HttpUrl httpUrl = request.getHttpUrl();
        sb.append("\n").append(requestMethod == null ? "" : requestMethod.name())
                .append("\t\t").append(httpUrl.toUrl()).append("\n");

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
        return sb.toString();
    }
}
