package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;

/**
 * @author  caizhihao
 */
public interface HttpSender {

    /**
     * Send HTTP request
     * @param httpMetadata          request data
     * @return                      response result
     */
    UniHttpResponse sendHttpRequest(HttpMetadata httpMetadata);

}
