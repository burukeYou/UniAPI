package com.burukeyou.uniapi.http.core.http.response;

import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractUniHttpResponse implements UniHttpResponse {

    private HttpMetadata httpMetadata;

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override
    public String toHttpProtocol() {
        String requestProtocol = httpMetadata.toHttpProtocol();
        StringBuilder sb = new StringBuilder(requestProtocol);
        sb.append("Response Header:\n");
        Map<String, List<String>> stringListMap = getHeaderMap();
        stringListMap.forEach((key, value) -> {
            for (String s : value) {
                sb.append("\t\t").append(key).append(":\t").append(s).append("\n");
            }
        });
        sb.append("Response Body:\n");
        String bodyToString = getBodyToString();
        if (StringUtils.isNotBlank(bodyToString)){
            sb.append("\t\t").append(bodyToString).append("\n");
        }
        sb.append("------------------------------------------------\n");
        return sb.toString();
    }
}
