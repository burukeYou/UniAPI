package com.burukeyou.uniapi.http.core.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author caizhihao
 */
public interface SslContextFactory {

    SSLContext createSslContext() ;

    HostnameVerifier createHostnameVerifier();
}
