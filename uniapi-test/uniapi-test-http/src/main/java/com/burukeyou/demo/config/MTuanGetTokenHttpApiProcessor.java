package com.burukeyou.demo.config;

import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MTuanGetTokenHttpApiProcessor extends MTuanHttpApiProcessor {

    @Override
    public HttpResponse<?> postSendingHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata) {
        return httpSender.sendHttpRequest(httpMetadata);
    }


}
