package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

/**
 * @author  caizhihao
 */
public interface HttpSender {

    HttpResponse sendHttpRequest(HttpMetadata httpMetadata);

}
