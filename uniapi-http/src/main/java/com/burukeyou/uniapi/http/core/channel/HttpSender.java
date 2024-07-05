package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

/**
 * @author  caizhihao
 */
public interface HttpSender {

    /**
     * Send HTTP request
     * @param httpMetadata          request data
     * @return                      response result
     */
    HttpResponse<?> sendHttpRequest(HttpMetadata httpMetadata);

}
