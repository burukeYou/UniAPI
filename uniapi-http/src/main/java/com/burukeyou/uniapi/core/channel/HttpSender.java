package com.burukeyou.uniapi.core.channel;

import com.burukeyou.uniapi.core.request.HttpMetadata;
import com.burukeyou.uniapi.core.response.HttpResponse;

/**
 * @author  caizhihao
 */
public interface HttpSender {

    HttpResponse sendHttpRequest(HttpMetadata httpMetadata);

}
