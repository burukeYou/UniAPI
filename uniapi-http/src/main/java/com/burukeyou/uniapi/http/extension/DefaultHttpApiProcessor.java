package com.burukeyou.uniapi.http.extension;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.lang.annotation.Annotation;

/**
 * @author caizhihao
 */

@Slf4j
public class DefaultHttpApiProcessor implements HttpApiProcessor<Annotation> {

    @Override
    public HttpResponse postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String path = httpMetadata.getHttpUrl().getPath();
        HttpResponse rsp = null;
        try {
            rsp = HttpApiProcessor.super.postSendHttpRequest(httpSender, httpMetadata);
            stopWatch.stop();
            log.info("发送第三方Http请求成功-请求接口:{} 耗时(ms):{} 请求参数:{} 响应参数:{}",path, stopWatch.getTotalTimeMillis(),JSON.toJSONString(httpMetadata),rsp);
        }  catch (Exception e) {
            log.error("发送第三方Http请求异常-请求接口:{} 请求参数:{}",path,JSON.toJSONString(httpMetadata));
            throw new SendHttpRequestException(e);
        }

        return rsp;
    }
}
