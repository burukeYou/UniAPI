package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;

/**
 * @author  caizhihao
 */
public interface HttpSender {

    /**
     * Send HTTP request
     * @param uniHttpRequest          request data
     * @return                      response result
     */
    UniHttpResponse sendHttpRequest(UniHttpRequest uniHttpRequest);

}
