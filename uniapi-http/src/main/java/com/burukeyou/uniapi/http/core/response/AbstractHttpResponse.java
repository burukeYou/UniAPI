package com.burukeyou.uniapi.http.core.response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.support.Cookie;
import lombok.Getter;
import lombok.Setter;

/**
 * @author  caizhihao
 */

@Setter
@Getter
public abstract class AbstractHttpResponse<T> implements HttpResponse<T> {

    protected UniHttpResponse responseMetadata;

    private static final List<String> NOT_PRINT_HEADER = Arrays.asList("connection","keep-alive","date","transfer-encoding");

    public AbstractHttpResponse(UniHttpResponse responseMetadata) {
        this.responseMetadata = responseMetadata;
    }

    @Override
    public boolean isSuccessful() {
        return responseMetadata.isSuccessful();
    }

    @Override
    public Map<String, String> getHeaders() {
        return responseMetadata.getHeaders();
    }

    @Override
    public Map<String, List<String>> getHeaderMap() {
        return responseMetadata.getHeaderMap();
    }

    @Override
    public String getHeader(String name) {
        return responseMetadata.getHeader(name);
    }

    @Override
    public List<String> getHeaderList(String name) {
        return  responseMetadata.getHeaderList(name);
    }

    @Override
    public int getHttpCode() {
        return responseMetadata.getHttpCode();
    }

    @Override
    public String getContentType() {
        return responseMetadata.getContentType();
    }

    public List<String> getSetCookiesString() {
        return getHeaderList("Set-Cookie");
    }

    @Override
    public List<Cookie> getSetCookies() {
        return responseMetadata.getSetCookies();
    }

    @Override
    public boolean isRedirect() {
        return responseMetadata.isRedirect();
    }

    public abstract String bodyResultString();

    @Override
    public String toHttpProtocol() {
        UniHttpRequest request = responseMetadata.getRequest();
        String requestProtocol = request.toHttpProtocol();
        StringBuilder sb = new StringBuilder(requestProtocol);
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
        T result = getBodyResult();
        if (result != null){
            sb.append("Response Body:\n");
            sb.append("\t\t").append(bodyResultString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getContentDispositionFileName() {
        return responseMetadata.getContentDispositionFileName();
    }

}
