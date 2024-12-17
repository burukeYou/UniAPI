package com.burukeyou.uniapi.http.core.conveter.response;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseConvertContext {

    private UniHttpResponse response;

    private UniHttpRequest request;

    private HttpMetadata httpMetadata;

    private HttpApiMethodInvocation<Annotation> methodInvocation;

    private HttpApiProcessor<Annotation> processor;

    private HttpApi httpApi;

    private HttpInterface httpInterface;
}
