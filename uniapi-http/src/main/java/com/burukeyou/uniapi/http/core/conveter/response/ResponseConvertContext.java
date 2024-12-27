package com.burukeyou.uniapi.http.core.conveter.response;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.http.response.HttpResponseInfo;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.HttpApiConfigContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseConvertContext {

    private HttpResponseInfo response;

    private HttpMetadata httpMetadata;

    private HttpApiMethodInvocation<Annotation> methodInvocation;

    private HttpApiProcessor<Annotation> processor;

    private HttpApi httpApi;

    private HttpInterface httpInterface;

    private HttpApiConfigContext configContext;
}
