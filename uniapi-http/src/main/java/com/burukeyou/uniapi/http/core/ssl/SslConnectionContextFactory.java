package com.burukeyou.uniapi.http.core.ssl;

/**
 * @author caizhihao
 */
public interface SslConnectionContextFactory {

    SslConnectionContext create(SslConfig sslConfig);

}
