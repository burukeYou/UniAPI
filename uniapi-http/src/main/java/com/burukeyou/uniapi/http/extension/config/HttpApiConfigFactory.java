package com.burukeyou.uniapi.http.extension.config;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;

/**
 * @author  caizhihao
 */
public interface HttpApiConfigFactory {

    SslConfig getSslConfig(HttpApiMethodInvocation<Annotation> methodInvocation);

}
