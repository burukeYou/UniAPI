package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.annotation.Annotation;

@Getter
@Setter
public class ResponseConvertContext {

    private Response response;

    private Request request;

    private HttpMetadata httpMetadata;

    private HttpApiMethodInvocation<Annotation> methodInvocation;

    private HttpApiProcessor<Annotation> processor;

}
